import copy
import time
import matplotlib.pyplot as plt
import ttide as tt
import pandas as pd
from scipy import interpolate

def read_waterl_Excel(path):

    frame = pd.read_excel(path,usecols=[0,1,2])  # 直接使用 read_excel() 方法读取
    dataf = frame.values  # 转为矩阵类型
    return dataf;

def conver_time_now(time_now):
    str_t = str(time_now)
    hour = float(str_t[:2])
    min = float(str_t[3:5])
    time = (hour*60+min)*60
    return time;

def time_everyday(dataf):
    array = copy.deepcopy(dataf)
    inital_time = time.mktime(time.strptime(str(array[0][0])[:-3],"%Y-%m-%d %H:%M"))
    for i in range(0,len(array)):
        t = time.strptime(str(array[i][0])[:-8] + str(array[i][2])[:5], "%Y-%m-%d %H:%M")
        array[i][0] = int((time.mktime(t)-inital_time)/60)

    return array[:,0:2]

def interpolation(dataf_h):

    f = interpolate.interp1d(dataf_h[:,0], dataf_h[:,1], kind=1)
    xnew = []
    for i in range(dataf_h[0][0], dataf_h[-1][0]):
        if(i%60==0):
            xnew.append(i)
    # xnew = np.arange(dataf_h[0][0], dataf_h[-1][0], 1)
    y = f(xnew)
    x = []
    for j in range(0,len(xnew)):
        x.append(xnew[j]/60)

    plt.plot(x,y,color='red',label='interpolation')
    plt.title("Hourly mean water level")
    plt.legend()
    plt.xlabel("time/hour")
    plt.ylabel("water level/m")
    # plt.xlim(0, 20)
    plt.show()

    return y,int(x[0]),int(x[-1]);

if __name__ =='__main__':

    # t = np.arange(1001)
    # m2_freq = 2 * np.pi / 12.42
    # elev = 5 * np.cos(m2_freq * t)
    # print(type(elev))

    path_wl = r'../tide/nanjing.xlsx'
    dataf = read_waterl_Excel(path_wl)
    # water_test_d = str(dataf[0][0])
    # water_test_h = str(dataf[0][2])
    # # a = len(water_test_h)
    # # b = len(water_test_d)
    #
    # t = water_test_d[:-8] + water_test_h[:5]
    # a = time.strptime(t, "%Y-%m-%d %H:%M")

    dataf_h = time_everyday(dataf)

    water_hour,firsttime,finaltime = interpolation(dataf_h)

    tfit_e = tt.t_tide(water_hour)
    tide_water = tfit_e['xout']
    plt.plot(tide_water, color='blue', label='tide')
    plt.title("tide water level")
    plt.legend()
    plt.xlabel("time/hour")
    plt.ylabel("water level/m")
    # plt.xlim(100, 2000)
    plt.show()


