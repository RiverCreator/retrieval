#-*-encoding:gbk-*-
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

house_data=pd.read_csv("handled_data_split_quantile.csv",encoding='gbk')
y=house_data['price']
list_y=list(y)
sum_y=[0,0,0,0]
for i in list_y:
    sum_y[i-2]+=1
list_x=[2,3,4,5]
plt.bar(list_x,sum_y)
plt.xlabel('类别')
plt.ylabel('个数')
plt.title('数据分布')
new_ticks = np.linspace(2, 5, 4)

plt.rcParams['font.sans-serif']=['SimHei']
plt.xticks(new_ticks)
plt.savefig(fname='split_quantile_statistics.jpg')
plt.show()

#准确率
acc=[0.7497142857142857,0.6148571428571429,0.5371428571428571]
split=[3,5,7]
plt.plot(split,acc)
plt.xlabel('划分数')
plt.ylabel('准确率')
plt.title('不同划分的准确率图')
new_ticks = np.linspace(3, 7, 3)

plt.rcParams['font.sans-serif']=['SimHei']
plt.xticks(new_ticks)
plt.savefig(fname='split_acc.jpg')
plt.show()

acc=[0.8353770692826487,0.6331428571428571,0.5977142857142858]
split=['2','4','5']
plt.plot(split,acc)
plt.xlabel('划分数')
plt.ylabel('准确率')
plt.title('分位数不同划分的准确率图')
#new_ticks = np.linspace(3, 7, 3)

plt.rcParams['font.sans-serif']=['SimHei']
#plt.xticks(new_ticks)
plt.savefig(fname='split_acc_quantile.jpg')
plt.show()