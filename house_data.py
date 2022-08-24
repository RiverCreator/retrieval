#-*-encoding:gbk-*-
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
#a��b��c��
#handle data
dict_orientation={'��':1,'��':2,'����':3,'��':4,'����':6,'��':8,'����':9,'����':12}
dict_decoration={'ë��':1,'��װ��':2,'����װ��':3,'��װ��':4,'��װ��':5}
dict_level={'�Ͳ�':1,'�в�':2,'�߲�':3}
#price ����  3��0-200 200-500 500above   5��0-150  150-250 250-350 350-500 500����  7��0-100 100-150 150-200 200-300 300-400 400-500 500above
#��λ������  
#price_split_dict={'17.5-150.0':0,'150.0-210.0':0,'249.0-440':0,'440.0-above':0}
with open("new_data.csv","r",encoding='gbk',errors='ignore') as f:
    with open("handled_data_split_quantile3.csv","w",encoding='gbk',errors='ignore',newline='') as hd_file:
        reader =csv.reader(f)
        fieldnames=['house_type','orientation','area','level','decoration','price']
        writer = csv.DictWriter(hd_file, fieldnames=fieldnames)
        writer.writeheader()
        for row in reader:
            if(len(row)==0 or row[1]=='name'):
                continue
            room=int(row[3].split('��')[0])
            hall=int(row[3].split('��')[0].split('��')[1])
            toilet=int(row[3].split('��')[1].split('��')[0])
            house_type=room*5+hall*3+toilet*2
            if(row[4]=='�ϱ�'):
                orientation=dict_orientation['��']
            elif(row[4]=='����'):
                orientation=dict_orientation['��']
            else:
                orientation=dict_orientation[row[4]]
            area=row[5]
            level=dict_level[row[6]]
            decoration=dict_decoration[row[8]]
            price=float(row[2])
            price_type=1
            if(price>=17.5 and price<249.0):
                price_type=1
            else:
                price_type=2
            # elif(price>=17.5 and price<150.0):
            #     price_type=2
            #     #price_split_dict['17.5-166.0']+=1
            # elif(price>=150.0 and price<210.0):
            #     price_type=3
            #     #price_split_dict['166.0-249.0']+=1
            # elif(price>=210.0 and price<310.0):
            #     price_type=4
            #     #price_split_dict['249.0-440']+=1
            # elif(price>=310.0 and price<508.0):
            #     price_type=5
            #     #price_split_dict['440.0-above']+=1
            # elif(price>=508.0):
            #     price_type=6

            new_rows={'house_type':house_type,'orientation':orientation,'area':area,'level':level,'decoration':decoration,'price':price_type}
            writer.writerow(new_rows)

