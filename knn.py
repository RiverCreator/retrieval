#-*-encoding:gbk-*-
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
import sklearn
import matplotlib.pyplot as plt
import numpy as np
quantile_list = [0, .5, 1]
house_data=pd.read_csv("handled_data_split_quantile3.csv",encoding='gbk')
x=house_data[['house_type','orientation','area','level','decoration']]
y=house_data['price']#.quantile(quantile_list)
#print(y)
# list_y=list(y)
# list_y.sort()
# list_x=[]
# for i in range(len(list_y)):
#      list_x.append(i)
# plt.bar(list_x,list_y)
# plt.xlabel('id')
# plt.ylabel('price')
# plt.savefig(fname='statistics2.jpg',format='jpg',dpi=300)
# plt.show()

x_train, x_test, y_train, y_test = train_test_split(x, y, random_state=22,train_size=0.8)
print(len(x_test),len(x_train))
transfer = StandardScaler()

x_train = transfer.fit_transform(x_train)
x_test = transfer.transform(x_test)

estimator = KNeighborsClassifier()

param_grid = {"n_neighbors": [1, 3, 5, 7, 9]}
estimator = sklearn.model_selection.GridSearchCV(estimator, param_grid=param_grid, cv=5)

estimator.fit(x_train, y_train.astype('int'))

score = estimator.score(x_test, y_test.astype('int'))
print("accurrancy:\n", score)

y_predict = estimator.predict(x_test)
print("predictions:\n", y_predict)
print("compare:\n", y_predict == y_test)

print("best result:\n", estimator.best_score_)
print("best estimator:\n", estimator.best_estimator_)
print("estimator results:\n",estimator.cv_results_)
print(estimator.cv_results_['split0_test_score'])#k=1
#3 best k=1 acc=0.7497142857142857 train_size=0.8 'split0_test_score': array([0.72714286, 0.68428571, 0.69285714, 0.69142857, 0.68428571]), 'split1_test_score': array([0.73571429, 0.70714286, 0.70142857, 0.70285714, 0.70142857]), 'split2_test_score': array([0.74285714, 0.69571429, 0.71      , 0.71285714, 0.68714286]), 'split3_test_score': array([0.73714286, 0.71      , 0.68428571, 0.68714286, 0.69285714]), 'split4_test_score': array([0.70386266, 0.66666667, 0.69241774, 0.6795422 , 0.68526466]), 'mean_test_score': array([0.72934396, 0.6927619 , 0.69619783, 0.69476558, 0.69019579]), 'std_test_score': array([0.01369782, 0.01590904, 0.00877734, 0.0117794 , 0.00635332])
#5 best_k=1 acc=0.6148571428571429 train_size=0.8 'split0_test_score': array([0.56714286, 0.49857143, 0.49714286, 0.51142857, 0.5       ]), 'split1_test_score': array([0.58571429, 0.51428571, 0.51142857, 0.51428571, 0.51428571]), 'split2_test_score': array([0.63428571, 0.53428571, 0.53142857, 0.51857143, 0.50857143]), 'split3_test_score': array([0.59285714, 0.51571429, 0.51142857, 0.51      , 0.52      ]), 'split4_test_score': array([0.58369099, 0.51502146, 0.52217454, 0.49499285, 0.50643777]), 'mean_test_score': array([0.5927382 , 0.51557572, 0.51472062, 0.50985571, 0.50985898]), 'std_test_score': array([0.0224166 , 0.01132431, 0.01153577, 0.00798734, 0.00682639])
#7 best_k=1 acc=0.5371428571428571 train_size=0.8 'split0_test_score': array([0.51428571, 0.40714286, 0.41571429, 0.41571429, 0.40571429]), 'split1_test_score': array([0.52857143, 0.42714286, 0.44142857, 0.42857143, 0.42285714]), 'split2_test_score': array([0.53857143, 0.41714286, 0.43      , 0.42142857, 0.41571429]), 'split3_test_score': array([0.51857143, 0.43714286, 0.42428571, 0.42142857, 0.42142857]), 'split4_test_score': array([0.50357654, 0.40343348, 0.42632332, 0.40343348, 0.40915594]), 'mean_test_score': array([0.52071531, 0.41840098, 0.42755038, 0.41811527, 0.41497404]), 'std_test_score': array([0.01200194, 0.01249026, 0.00837689, 0.0083977 , 0.00669247])


#分位数划分
# 0.6331428571428571 acc=0.6331428571428571 train-size=0.8 'split0_test_score': array([0.62428571, 0.55571429, 0.55714286, 0.57285714, 0.56      ]), 'split1_test_score': array([0.65142857, 0.57571429, 0.58714286, 0.58428571, 0.57714286]), 'split2_test_score': array([0.65857143, 0.57428571, 0.59428571, 0.57714286, 0.55714286]), 'split3_test_score': array([0.62285714, 0.55714286, 0.54571429, 0.55857143, 0.56714286]), 'split4_test_score': array([0.60944206, 0.55078684, 0.54506438, 0.5379113 , 0.53505007]), 'mean_test_score': array([0.63331698, 0.5627288 , 0.56587002, 0.56615369, 0.55929573]), 'std_test_score': array([0.01858387, 0.01024892, 0.02085798, 0.01642919, 0.01395049])
# acc=[0.7497142857142857,0.6148571428571429,0.5371428571428571]
# split=[3,5,7]
# plt.plot(split,acc)
# plt.xlabel('划分数')
# plt.ylabel('准确率')
# plt.title('不同划分的准确率图')
# new_ticks = np.linspace(3, 7, 3)

# plt.rcParams['font.sans-serif']=['SimHei']
# plt.xticks(new_ticks)
# plt.savefig(fname='split_acc.jpg')
# plt.show()