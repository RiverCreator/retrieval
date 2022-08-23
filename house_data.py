#-*-encoding:utf-8-*-
from asyncore import read
import csv
import collections
from dataclasses import field
from hashlib import new
# fieldnames = ['id','name','price','house_type','orientation','area','level','single_price','decoration']
# writer = csv.DictWriter(open('new_data.csv', mode='a+'), fieldnames=fieldnames)
# writer.writeheader()
# with open("./data2.csv", "r",encoding='gbk',errors='ignore') as f:
#     reader = csv.reader(f)
#     id=1
#     for row in reader:
        
#         print(row[0])
#         new_rows=collections.OrderedDict()
#         if(row[1]=='name'):
#             continue
#         new_rows={'id':str(id),'name':row[1],'price':row[2],'house_type':row[3],'orientation':row[4],'area':row[5],'level':row[6],'single_price':row[7],'decoration':row[8]}
#         print(new_rows)
#         with open('new_data.csv', mode='a+') as csv_file:
#             writer.writerow(new_rows)
#             id+=1

# 0   1     2      3           4         5   6       7            8        
#id,name,price,house_type,orientation,area,level,single_price,decoration
#handle data
with open("new_data.csv","r",encoding='gbk',errors='ignore') as f:
    with open("handled_data.csv","w",encoding='gbk',errors='ignore',newline='') as hd_file:
        reader =csv.reader(f)
        fieldnames=['house_type','orientation','area','level','decoration','price']
        writer = csv.DictWriter(hd_file, fieldnames=fieldnames)
        writer.writeheader()
        for row in reader:
            if(len(row)==0 or row[1]=='name'):
                continue
            new_rows={'house_type':row[3],'orientation':row[4],'area':row[5],'level':row[6],'decoration':row[8],'price':row[2]}
            writer.writerow(new_rows)