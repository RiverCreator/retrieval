import requests
from bs4 import BeautifulSoup
import re
import urllib.request,urllib.error
import sqlite3
import os
import time
import json
import socket
import random
import psycopg2
import networks
import torch
import numpy
from torchvision import transforms, utils
from PIL import Image
#加载模型
"""加载为resnet"""
model = networks.TestNet(networks.EmbeddingNet2())
#model.load_state_dict(torch.load("model-cpu.pt",False))
model.load_state_dict(torch.load("model-cpu-resnet.pt"))
"""加载为vgg"""
# model = networks.TestNet(networks.EmbeddingNet())

# model.load_state_dict(torch.load("model-cpu-vgg.pt"))

model.eval()
normalize=transforms.Normalize(
mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]
)
train_transforms = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    normalize
])
#链接数据库
conn = psycopg2.connect(database="postgres", user="postgres", password="030924", host="localhost", port="5432")
cur = conn.cursor()  # 创建指针对象
"""id 商品名称 图片url 价格 商品链接 特征"""
cur.execute("CREATE TABLE item3(id integer,name varchar,img_url varchar ,price float ,item_url varchar ,feature real[]);")
#cur.execute("INSERT INTO student(id,name,feature)VALUES(%s,%s,%s)", (1, 'Aspirin',z ))   插入数据

def default_loader(path):
    return Image.open(path).convert('RGB')
def main():
    url="https://search.jd.com/Search?keyword=shoe&wq=shoe&pvid=babb6539b1b645c7a929c153f34cf090&page="
    end_url="&s=1&click=0"
    id=1
    # model.get_embedding()
    for i in range(1,50):
        page_url=url+str(i)+end_url
        print(page_url)
        id=getimg(page_url,id)
    conn.commit()
    cur.close()
    conn.close()

def validateTitle(title):
    rstr = r"[\/\\\:\*\?\"\<\>\|]"  # ‘/ \ : * ? " < > |‘
    new_title = re.sub(rstr, "_", title)  # 替换为下划线
    return new_title
def getimg(url,id):
    html = askURL(url)
    soup = BeautifulSoup(html, "html.parser")
    path="D:\\pycharm2019.1.2\\code\\server\\database"
    if os.path.exists(path) == False:
        os.mkdir(path)
    img_findlink=re.compile(r'data-lazy-img="(.*?)" height="220" width="220">',re.S)
    url_findlink=re.compile(r'href="(.*?)"',re.S)
    name_findlink=re.compile(r'<em>(.*?)</em>',re.S)
    price_findlink = re.compile(r'<i>(.*?)</i>', re.S)
    for item in soup.find_all('div', class_="gl-i-wrap"):
        item=str(item)
        #print(item)
        img_url=re.findall(img_findlink, item)[0]
        jpg_link="https:"+img_url
        item_url=re.findall(url_findlink,item)[0]
        price=re.findall(price_findlink,item)[0]
        item_name=re.findall(name_findlink,item)[1]
        item_name=item_name.replace("<(.*?)>","")
        new_item_name = re.sub(r'<(.*?)>', '', item_name)
        new_item_name=validateTitle(new_item_name)
        if("￥"in new_item_name):
            item_name = re.findall(name_findlink, item)[2]
            item_name = item_name.replace("<(.*?)>", "")
            new_item_name = re.sub(r'<(.*?)>', '', item_name)
            new_item_name = new_item_name.replace("/", "")
        new_item_name=new_item_name.replace("\n","")
        new_item_name = new_item_name.replace("\t", "")
        new_item_name=new_item_name.replace("\\","")
        new_item_name=new_item_name.replace("|","")
        new_item_name=new_item_name.replace("?","")
        new_item_name = new_item_name.replace("*", "")
        new_item_name=new_item_name.replace("｜","")
        new_item_name=new_item_name.replace("丨","")
        item_url="https:"+item_url
        #save image
        print(img_url)
        print(item_url)
        print(new_item_name)
        print(price)
        features=save_img(jpg_link,new_item_name,path)
        cur.execute("INSERT INTO item3(id ,name ,img_url  ,price  ,item_url ,feature )VALUES(%s,%s,%s,%s,%s,%s)", (id,new_item_name,jpg_link,price, item_url, features))
        conn.commit()
        id+=1
    return id
def askURL(url):
    #用户代理，表示告诉服务器，我们是什么类型的机器，浏览器（本质上是告诉浏览器，我们可以接收什么水平的文件内容）
    #模拟头部信息，向服务器发送信息
    head_0={"user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36"
          }
    head_1={"user-agent":"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36 Edg/90.0.818.46"}
    random.seed(time.time())
    i=random.randint(0,1)
    if i:
        request=urllib.request.Request(url,headers=head_1)
    else:
        request = urllib.request.Request(url, headers=head_0)
    html=""
    print("get html.....")
    try_time=3
    for i in range(try_time):
        try:
            if i>0:
                print("retrying.....")
            time.sleep(5)
            response=urllib.request.urlopen(request,timeout=7)
            html = response.read().decode("utf-8")
            if response.status == 200:
                break
        #print(html)
        except urllib.error.HTTPError as e:
            if( hasattr(e,"code")):
                print(e.code)
            if(hasattr(e,"reason")):
                print(e.reason)
            print(e)
        except urllib.error.URLError as k:
            if (hasattr(k, "code")):
                print(k.code)
            if (hasattr(k, "reason")):
                print(k.reason)
            print(k)
        except socket.timeout as j:
            print(j)
    return html
def save_img(img_url,name,path):
    res_sub = requests.get(img_url)
    save_path = path + '\\' + name + ".jpg"
    print(save_path)
    with open(save_path, 'wb') as f:
        f.write(res_sub.content)
    print(name + '.jpg is saved！')
    img=default_loader(save_path)
    img=train_transforms(img)
    print(img.size())
    img = img.view(1, *img.size())
    print(img.size())
    features=model.get_embbeding(img)
    y = features.detach().numpy()
    z = y.tolist()
    return z
main()