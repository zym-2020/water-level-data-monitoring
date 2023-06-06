from decimal import Decimal
import matplotlib.pyplot as plt
import xlrd  # 导入库
import pandas as pd
import numpy as np
import time
import statsmodels.api as sm
import statsmodels.formula.api as smf
from statsmodels.sandbox.regression.predstd import wls_prediction_std

def read_waterl_Excel(path):

    frame = pd.read_excel(path,usecols=[0,1,2])  # 直接使用 read_excel() 方法读取
    dataf = frame.values  # 转为矩阵类型
    return dataf;

def read_q_Excel(path):

    frame = pd.read_excel(path,usecols=[0,1])
    dataq = frame.values
    return dataq;

def read_tide_range(path):

    frame = pd.read_excel(path,usecols=[0,1])
    datat = frame.values
    return datat;

def conver_time_now(time_now):
    str_t = str(time_now)
    hour = float(str_t[:2])
    min = float(str_t[3:5])
    time = hour*60+min
    return time;

def get_mean_water_level(dataf):
    length = len(dataf)
    meanL = []
    timeL = []
    # print((dataf))
    datenow = ''  # 当前的数据日期
    sum_t = 0  # 临时潮位总和
    num_t = 0  # 临时高低潮位数量
    time_last = 0  # 每天的最后一个时刻
    dif1 = 0  # 当天数据与前一天的最近时刻差
    dif2 = 0  # 当天数据与后一天的最近时刻差
    # timeArray = time.strptime(dataf[0][0], "%Y-%m-%d %H:%M:%S")
    # # 转换为时间戳:
    # timeStamp = int(time.mktime(timeArray))
    for i in range(0, length):
        if (i == 0):
            datenow = str(dataf[0][0])
        if (datenow == str(dataf[i][0])):
            sum_t = sum_t + float(dataf[i][1])
            num_t = num_t + 1
            time_last = dataf[i][2]
        if (datenow != str(dataf[i][0])):
            timeL.append(datenow)
            if (num_t == 4):
                meanL.append(sum_t / 4)
                sum_t = float(dataf[i][1])
                num_t = 1
            if (num_t == 3):
                dif2 = conver_time_now(dataf[i][2]) + (24 * 60) - conver_time_now(time_last)
                # if(dif2==(24*60)):
                #     dif2 = 0
                if (dif1 > dif2):
                    sum_t = sum_t + float(dataf[i - 2][1])
                else:
                    sum_t = sum_t + float(dataf[i][1])
                dif1 = dif2
                meanL.append(sum_t / 4)
                sum_t = float(dataf[i][1])
                num_t = 1
            datenow = str(dataf[i][0])
        if (i == length - 1):
            timeL.append(datenow)
            if (num_t == 4):
                meanL.append(sum_t / 4)
            if (num_t == 3):
                sum_t = sum_t + float(dataf[i - 1][1])
                meanL.append(sum_t / 4)
    # list = [[] for i in range(len(timeL))]
    list = [timeL,meanL]
    return list;

def get_flow(dataq):

    timeq = []
    flow = []
    for i in range(0,len(dataq)):
        timeq.append(str(dataq[i][0]))
        flow.append(float(dataq[i][1]))
    list = [timeq,flow]
    return list;

def q_res(flow_q,mean_water_level):
    x = flow_q[1][0:-1]  # 径流日期选择
    x = np.array(x)
    x = x / 10000
    y = mean_water_level[1][1:]
    X = np.column_stack((x, x ** 2))  # 方程各系数
    X = sm.add_constant(X)  # 回归方程添加一列

    # 建立回归方程
    modles = sm.OLS(y, X)
    res = modles.fit()  # 最小二乘法
    Bata = res.params  # 取系数
    print(Bata, res.summary())  # 结果

    prstd, ivLow, ivUp = wls_prediction_std(res)  # 返回标准偏差和置信区间
    Y = res.fittedvalues  # 预测值
    fig, ax = plt.subplots(figsize=(8, 6))
    ax.plot(x, y, 'o', label='data')  # 原始数据
    ax.plot(x, Y, 'r--', label='test')  # 拟合数据
    ax.plot(x, ivUp, '--',color='orange', label="ConfInt")  # 置信区间 上届
    ax.plot(x, ivLow, '--',color='orange')  # 置信区间 下届
    ax.legend(loc='best')
    plt.show();

def get_tide_range(datat):
    tide_range = []
    time_t = []
    max = 0
    min = 100
    for i in range(0,len(datat)):
        if(i==0):
            datenow = str(datat[i][0])
        if(datenow==str(datat[i][0])):
            if(max<float(datat[i][1])):
                max = float(datat[i][1])
            if(min>float(datat[i][1])):
                min = float(datat[i][1])
        if(datenow!=str(datat[i][0])):
            time_t.append(datenow)
            datenow = str(datat[i][0])
            tide_range.append(max - min)
            max = float(datat[i][1])
            min = float(datat[i][1])
        if(i==len(datat)-1):
            time_t.append(datenow)
            tide_range.append(round((max - min),2))

    list = [time_t,tide_range]
    return list

def t_q_res(flow_q,mean_water_level,tide_range):
    q = flow_q[1][0:-1]  # 径流日期选择
    q = np.array(q)
    q = q / 10000

    R = tide_range[1][0:-1]  # 潮差日期选择
    R = np.array(R)

    h = mean_water_level[1][1:]  # 水位日期选择

    # 创建数据
    data = pd.DataFrame(
        {'Y': h, 'Q': q, 'Q2': q**2, 'R': R ,'R2': R**2, 'R3':R**3})

    # 建立回归方程
    #  Ridge Regression 岭回归交叉验证可以用作对病态数据的拟合
    modles = smf.ols(formula='Y ~ Q + Q2 + R',data=data)
    res = modles.fit()  # 最小二乘法拟合结果
    Bata = res.params  # 取系数
    # print(Bata, res.summary())  # 结果
    H = res.fittedvalues  # 预测值
    # print(H)

    # fig = plt.figure()
    # ax = fig.add_subplot(111, projection='3d')  # ax = Axes3D(fig)
    # ax.scatter(q, R, h, c='b',)
    # ax.plot(q, R, H, c='r',)
    # ax.set_xlabel('X Label')
    # ax.set_ylabel('Y Label')
    # ax.set_zlabel('Z Label')
    # plt.show()

    # print(Bata)
    return Bata;

def verification_f(mean_water_level,flow_q,tide_range,param):
    q = flow_q[1][0:-1]
    q = np.array(q)
    Q = q / 10000
    R = tide_range[1][0:-1]
    R = np.array(R)

    y_f = param[0] + param[1]*Q + param[2]*(Q**2) + param[3]*R

    x1= range(0,364)
    y1= mean_water_level[1][1:]
    y2= 1.6916  + 1.3594*Q - 0.0390*(Q**2)
    y3= 0.993872 + 1.341204*Q - 0.037288*(Q**2) + 0.272800*R
    plt.plot(x1, y1, color='red', linestyle="-.",label='data')
    # plt.plot(x1, y2, color='green', label='q')

    plt.plot(x1, y_f, color='blue',  label='q_t')

    plt.plot(x1, y_f-y1, color='black',  label='dev')

    plt.title("mean water level")
    plt.legend()
    plt.xlabel("time/day")
    plt.ylabel("mean water level/m")
    plt.show();


def water_pre_year(flow_q,tide_range,param):
    q = flow_q[1][:]
    q = np.array(q)
    Q = q / 10000
    R = tide_range[1][:]
    R = np.array(R)
    y_f = param[0] + param[1]*Q + param[2]*(Q**2) + param[3]*R
    return y_f;

def warter_pre_hour(warter_pre):
    warter_pre_h = []
    for i in range(0,len(warter_pre)*24):
        warter_pre_h.append(warter_pre[int(i/24)])
    return warter_pre_h;

# lis=[[] for i in range(5)]
# for i in range(5):
# lis.append([])

if __name__ == '__main__':

    path_wl = r'../tide/nanjing.xlsx'
    path_q = '../tide/datong.xlsx'
    path_t = '../tide/baozhen.xlsx'
    dataf = read_waterl_Excel(path_wl)
    dataq = read_q_Excel(path_q)
    datat = read_tide_range(path_t)
    flow_q = get_flow(dataq)  # 获取流量数据
    mean_water_level = get_mean_water_level(dataf)  # 只考虑径流，获取平均水位数据
    tide_range = get_tide_range(datat)  # 获取日平均潮差

    # q_res(flow_q,mean_water_level)  # 考虑径流的回归分析

    param = t_q_res(flow_q,mean_water_level,tide_range) # 同时考虑径流和潮差的回归分析

    verification_f(mean_water_level,flow_q,tide_range,param)  # 日平均水位预测模型验证

    # 返回预测水位
    warter_pre = water_pre_year(flow_q,tide_range,param)
    # 365天预测逐时水位
    warter_pre_h = warter_pre_hour(warter_pre)
