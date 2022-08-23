#-*-encoding:utf-8-*-
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
import sklearn
house_data=pd.read_csv("handled_data.csv",encoding='gbk')
x=house_data[['house_type','orientation','area','level','decoration']]
y=house_data['price']
x_train, x_test, y_train, y_test = train_test_split(x, y, random_state=22)

transfer = StandardScaler()

x_train = transfer.fit_transform(x_train)
x_test = transfer.transform(x_test)

estimator = KNeighborsClassifier()

param_grid = {"n_neighbors": [1, 3, 5, 7, 9]}
estimator = sklearn.model_selection.GridSearchCV(estimator, param_grid=param_grid, cv=5)

estimator.fit(x_train, y_train)

score = estimator.score(x_test, y_test)
print("accurrancy:\n", score)

y_predict = estimator.predict(x_test)
print("predictions:\n", y_predict)
print("compare:\n", y_predict == y_test)

print("best result:\n", estimator.best_score_)
print("best estimator:\n", estimator.best_estimator_)
print("estimator results:\n",estimator.cv_results_)
