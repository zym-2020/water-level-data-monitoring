import datetime
from Water_Level import *
from t_tide import *
import time
import json
import requests
# 获取当前日期前一天
t = time.localtime()
Month = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
if (t.tm_mday == 1):
    mon = t.tm_mon - 1
    mday = Month[t.tm_mon - 2]
else:
    mon = t.tm_mon
    mday = t.tm_mday - 1
if (mon < 10):
    mon = '0' + str(mon)
else:
    mon = str(mon)
if (mday < 10):
    mday = '0' + str(mday)
else:
    mday = str(mday)

# 数据初始时间
initial_month = 2
initial_day = 20

# 上下游径流和潮差数据时间长度
time_start = '/2023-02-20%2000:00:00'
# time_end = '/2023-05-20%2000:00:00'
time_end = '/2023-'+ str(mon) + '-' + str(mday) + '%2000:00:00'

# 水位调和分析数据时间长度
time_start_wl = '/2023-02-20%2009:00:00'
# time_end_wl = '/2023-05-20%2023:00:00'
time_end_wl = '/2023-'+ str(mon) + '-' + str(mday) + '%2023:00:00'

url_xuliujing = 'https://geomodeling.njnu.edu.cn/waterLevel/jiangsu/getInfoByStationAndTime/徐六泾'

# 测试集潮位及径流，一天
def tideRange_flow(time1,time2,time_now):
    url_flow_datong = 'https://geomodeling.njnu.edu.cn/waterLevel/YangtzeDownstream/getInfoByStationAndTime/大通' + time1 + time2
    url_tideRange_sijiao = 'https://geomodeling.njnu.edu.cn/waterLevel/zhejiang/getInfoByStationAndTime/泗礁/' + time1 + time2
    requests.packages.urllib3.disable_warnings()
    res_datong = requests.get(url_flow_datong, verify=False).text
    json_dict = json.loads(res_datong)['data']
    # print(url_flow_datong)

    res_sijiao = requests.get(url_tideRange_sijiao, verify=False).text
    json_dict2 = json.loads(res_sijiao)['data']

    if(len(json_dict)==0):
        print('未找到%s径流数据' %time1[1:11])
        return [],[]
    if(len(json_dict2)==0):
        print('未找到%s潮位数据' %time1[1:11])
        return [],[]
    sum_flow = 0
    i = 0
    num_flow = 0
    while i<len(json_dict):
        if (json_dict[i]['flow']):
            sum_flow = sum_flow + int(json_dict[i]['flow'])
            num_flow = num_flow + 1
        i = i + 1
    if(num_flow!=0):
        mean_flow = sum_flow / num_flow
    else:
        # print( '0'+str(time_now)+'径流数据丢失' )
        raise RuntimeWarning('时间：20230'+str(time_now)+' 径流数据丢失，请重新核对')
    tide_range_max = -100
    tide_range_min = 100
    for j in range(0, len(json_dict2)):
        if (json_dict2[j]['waterLevel']):
            # print(json_dict2[j]['waterLevel'])
            if (json_dict2[j]['waterLevel'] > tide_range_max):
                tide_range_max = json_dict2[j]['waterLevel']
            if (json_dict2[j]['waterLevel'] < tide_range_min):
                tide_range_min = json_dict2[j]['waterLevel']
    if(tide_range_min ==100 or tide_range_max==-100):
        raise RuntimeWarning('时间：20230'+str(time_now)+' 水位数据丢失，请重新核对')
    else:
        tide_range = tide_range_max - tide_range_min

    return mean_flow,tide_range;

# 几个月水位 2.20开始
def wl_xuliujing():

    url_wl_xuliujing = url_xuliujing + time_start_wl + time_end_wl
    # print(url_wl_xuliujing)
    requests.packages.urllib3.disable_warnings()
    res_xuliujing = requests.get(url_wl_xuliujing, verify=False).text
    json_dict_z = json.loads(res_xuliujing)['data']

    return json_dict_z

# 过去几个月潮位和径流为训练集 2.20开始
def get_tr_mf(json_dict_z):

    url_flow_datong = 'https://geomodeling.njnu.edu.cn/waterLevel/YangtzeDownstream/getInfoByStationAndTime/大通' + time_start + time_end
    url_tideRange_sijiao = 'https://geomodeling.njnu.edu.cn/waterLevel/zhejiang/getInfoByStationAndTime/泗礁/' + time_start + time_end

    res_datong = requests.get(url_flow_datong, verify=False).text
    json_dict = json.loads(res_datong)['data']
    # print(json_dict[0])

    res_sijiao = requests.get(url_tideRange_sijiao, verify=False).text
    json_dict2 = json.loads(res_sijiao)['data']

    #
    mean_flow_3m = []
    sum_flow_d = 0
    time_current = json_dict[0]['time'][0:10]
    num_t_d = 0
    i = 0
    while i<len(json_dict):
        if (json_dict[i]['time'][0:10] == time_current):
            if (json_dict[i]['flow']):
                sum_flow_d = sum_flow_d + int(json_dict[i]['flow'])
                num_t_d = num_t_d + 1
        else:
            mean_flow = sum_flow_d / num_t_d
            sum_flow_d = 0
            num_t_d = 0
            time_current = json_dict[i]['time'][0:10]
            i = i - 1
            mean_flow_3m.append(mean_flow)
        if ((i == len(json_dict) - 1) and (num_t_d != 0)):
            mean_flow = sum_flow_d / num_t_d
            sum_flow_d = 0
            num_t_d = 0
            time_current = json_dict[i]['time'][0:10]
            mean_flow_3m.append(mean_flow)
        i = i + 1

    #
    tide_range_max = -100
    tide_range_min = 100
    tide_range_3m = []
    time_current2 = json_dict2[0]['time'][0:10]
    j = 0
    while j<len(json_dict2):
        if (json_dict2[j]['time'][0:10] == time_current2):
            if (json_dict2[j]['waterLevel']):
                if (json_dict2[j]['waterLevel'] > tide_range_max):
                    tide_range_max = json_dict2[j]['waterLevel']
                if (json_dict2[j]['waterLevel'] < tide_range_min):
                    tide_range_min = json_dict2[j]['waterLevel']
            else:
                pass
        else:
            tide_range = tide_range_max - tide_range_min
            tide_range_3m.append(tide_range)
            tide_range_max = -100
            tide_range_min = 100
            time_current2 = json_dict2[j]['time'][0:10]
            j = j - 1
        if ((j == len(json_dict2) - 1) and (tide_range_min != 100)):
            tide_range = tide_range_max - tide_range_min
            tide_range_3m.append(tide_range)
            tide_range_max = -100
            tide_range_min = 100
        j = j + 1

    #
    mean_wl_3m = []
    sum_wl_d = 0
    time_current_wl = json_dict_z[0]['time'][0:10]
    num_wl_d = 0
    k = 0
    while k< len(json_dict_z):
        if (json_dict_z[k]['time'][0:10] == time_current_wl):
            if (json_dict_z[k]['upstreamWaterLevel']):
                sum_wl_d = sum_wl_d + int(json_dict_z[k]['upstreamWaterLevel'])
                num_wl_d = num_wl_d + 1
        else:
            mean_wl = sum_wl_d / num_wl_d
            sum_wl_d = 0
            num_wl_d = 0
            time_current_wl = json_dict_z[k]['time'][0:10]
            k = k - 1
            mean_wl_3m.append(mean_wl)

        if ((k == len(json_dict_z) - 1) and (num_wl_d != 0)):
            mean_wl = sum_wl_d / num_wl_d
            sum_wl_d = 0
            num_wl_d = 0
            time_current_wl = json_dict_z[k]['time'][0:10]
            mean_wl_3m.append(mean_wl)
        k = k + 1
    # print(mean_wl_3m)
    return mean_flow_3m,tide_range_3m,mean_wl_3m;

# 获得回归参数
def get_param(mean_flow_3m,tide_range_3m,mean_wl_3m):
    h = np.array(mean_wl_3m)
    q = np.array(mean_flow_3m) / 10000
    R = np.array(tide_range_3m)

    # 创建数据
    data = pd.DataFrame(
        {'Y': h, 'Q': q, 'Q2': q ** 2, 'R': R, 'R2': R ** 2, 'R3': R ** 3})

    # 建立回归方程
    #  Ridge Regression 岭回归交叉验证可以用作对病态数据的拟合
    modles = smf.ols(formula='Y ~ Q + Q2 + R ', data=data)
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

# 得到测试日平均水位
def water_pre_year(mean_flow,tide_range,param):
    Q = mean_flow / 10000
    R = tide_range
    y_f = param[0] + param[1]*Q + param[2]*(Q**2) + param[3]*R
    warter_pre_24 = []
    for i in range(0,24):
        warter_pre_24.append(y_f)
    return np.array(warter_pre_24);

# 得到日平均水位训练集
def get_wl_eh(json_dict_z):
    water_l_h = []
    t = 0
    j_f = 1
    k_f = 1
    Month = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    for i in range(initial_month, 13):
        j = 1
        while j < Month[i - 1] + 1:
            if (i == initial_month and j_f == 1):
                j = initial_day
                j_f = 0
            if (j < 10):
                j_str = '0' + str(j)
            else:
                j_str = str(j)
            k = 0
            while k < 24:
                if (i == initial_month and j == initial_day and k_f == 1):
                    k = 9
                    k_f = 0
                if (k < 10):
                    k_str = '0' + str(k)
                else:
                    k_str = str(k)
                if(i<10):
                    i_str = '0' + str(i)
                else:
                    i_str = str(i)
                if (t < len(json_dict_z)):
                    # print(json_dict_z[t]['time'])
                    if ((json_dict_z[t]['time'][5:7] == i_str) and (json_dict_z[t]['time'][8:10] == j_str) and (
                            json_dict_z[t]['time'][11:13] == k_str)):
                        # print(json_dict_z[t]['time'])
                        if(json_dict_z[t]['upstreamWaterLevel'] is None):
                            water_l_h.append(np.nan)
                        else:
                            water_l_h.append(float(json_dict_z[t]['upstreamWaterLevel']))
                        t = t + 1
                    elif (((t + 1) < len(json_dict_z)) and (json_dict_z[t + 1]['time'][5:7] == i_str) and (
                                json_dict_z[t + 1]['time'][8:10] == j_str) and (
                                      json_dict_z[t + 1]['time'][11:13] == k_str)):
                        t = t + 1
                        if (json_dict_z[t]['upstreamWaterLevel'] is None):
                            water_l_h.append(np.nan)
                        else:
                            water_l_h.append(float(json_dict_z[t]['upstreamWaterLevel']))
                        t = t + 1
                    elif(json_dict_z[t]['time'][11:13] == json_dict_z[t - 1]['time'][11:13]):
                        while (json_dict_z[t]['time'][11:13] == json_dict_z[t - 1]['time'][11:13]):
                            t = t + 1
                        k = k - 1
                    else:
                        water_l_h.append(np.nan)
                k = k + 1
            j = j + 1
    # print(water_l_h)
    return np.array(water_l_h);

# 生成时间数组
def generate_time_series(start, end):
    current = datetime.datetime.strptime(start, '%Y-%m-%d %H:%M:%S')
    end_item = datetime.datetime.strptime(end, '%Y-%m-%d %H:%M:%S')
    time_delta = datetime.timedelta(hours=1)
    time_series = [current]
    while current < end_item:
        next = current + time_delta
        time_series.append(next)
        current = next
    return np.array(time_series);

# 获得测试数据，一天
def wl_xuliujing_test(time1,time2):
    url_wl_xuliujing = url_xuliujing + time1 + time2
    # print(url_wl_xuliujing)
    requests.packages.urllib3.disable_warnings()
    res_xuliujing = requests.get(url_wl_xuliujing, verify=False).text
    json_dict_z_test = json.loads(res_xuliujing)['data']
    xuliujing_test = []
    # print(json_dict_z_test)
    if(len(json_dict_z_test)==0):
        print('未找到%s测试水位数据' %time1[1:11])
        return []
    i = 0
    j = 0
    while i < 24:
        if(j==len(json_dict_z_test)-1):
            xuliujing_test.append(json_dict_z_test[j]['upstreamWaterLevel'])
            i = i + 1
        elif(i == int(json_dict_z_test[j]['time'][11:13])):
            xuliujing_test.append(json_dict_z_test[j]['upstreamWaterLevel'])
            i = i + 1
            j = j + 1
        elif(j==0):
            xuliujing_test.append(json_dict_z_test[0]['upstreamWaterLevel'])
            i = i + 1
        elif(i != int(json_dict_z_test[j]['time'][11:13]) and i == int(json_dict_z_test[j+1]['time'][11:13])):
            j = j + 1
            xuliujing_test.append(json_dict_z_test[j]['upstreamWaterLevel'])
            j = j + 1
            i = i + 1
        elif(json_dict_z_test[j]['time'][11:13] == json_dict_z_test[j-1]['time'][11:13]):
            while (json_dict_z_test[j]['time'][11:13] == json_dict_z_test[j - 1]['time'][11:13]):
                j = j + 1
        else:
            xuliujing_test.append((json_dict_z_test[j-1]['upstreamWaterLevel'] + json_dict_z_test[j]['upstreamWaterLevel'])/2)
            i = i + 1
    # print(xuliujing_test)
    if((i-j-1) > 0):
        print('%s 缺少%d项数据' %(json_dict_z_test[j]['time'][0:10],i-j-1))
    return xuliujing_test

# 主函数
def main_def(time_month,time_day,error_correction=[0]):

    # 获取南京水位数据
    json_dict_z = wl_xuliujing()
    # region 得到日均水位
    # 获得潮差和日均径流训练集
    mean_flow_3m, tide_range_3m, mean_wl_3m = get_tr_mf(json_dict_z)
    # 回归分析参数
    param_xuliujing = get_param(mean_flow_3m, tide_range_3m, mean_wl_3m)

    time_month_before, time_day_before = count_time_before(time_month, time_day)
    time1 = '/2023-' + str(time_month_before) + '-' + str(time_day_before) + '%2000:00:00'
    time2 = '/2023-' + str(time_month_before) + '-' + str(time_day_before) + '%2023:00:00'

    # 获得时间段内的上游平均水位和下游潮差
    mean_flow, tide_range = tideRange_flow(time1, time2,time_month_before*100+time_day_before)
    # 预测日均水位
    warter_pre = []
    if(mean_flow and tide_range):
        warter_pre = water_pre_year(mean_flow, tide_range, param_xuliujing)
        # 预测日均水位修正
        warter_pre = warter_pre - error_correction
    # endregion

    # region 得到潮波水位
    # 调和分析结果
    water_l_h = get_wl_eh(json_dict_z)
    tfit_e = tt.t_tide(water_l_h, dt=1, stime=datetime.datetime(2023, initial_month, initial_day, 9, 0, 0), )

    # 预测时间
    time_pre = generate_time_series('2023-' + str(time_month) + '-' + str(time_day) + ' 0:0:0',
                                    '2023-' + str(time_month) + '-' + str(time_day) + ' 23:0:0')
    # 预测潮波水位
    tide_pre = tt.t_predic(time_pre, tfit_e['nameu'], tfit_e['fu'], tfit_e['tidecon'])
    # endregion

    xuliujing_test = 0
    if(len(error_correction)<3):
        # 南京实测水位数据
        time_month, time_day = intToStr(time_month, time_day)
        wltest1 = '/2023-' + str(time_month) + '-' + str(time_day) + '%2000:00:00'
        wltest2 = '/2023-' + str(time_month) + '-' + str(time_day) + '%2023:00:00'
        xuliujing_test = wl_xuliujing_test(wltest1, wltest2)

    return tide_pre,warter_pre,xuliujing_test;

# 返回前一天日期
def count_time_before(time_month,time_day):
    Month = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    time_month = int(time_month)
    time_day = int(time_day)
    if (time_day == 1):
        time_month = time_month - 1
        time_day = Month[time_month - 2]
    else:
        time_month = time_month
        time_day = time_day - 1
    if(time_month<10):
        time_month_s = '0'+str(time_month)
    else:
        time_month_s = str(time_month)
    if(time_day<10):
        time_day_s = '0' + str(time_day)
    else:
        time_day_s = str(time_day)
    return time_month_s,time_day_s;

# 计算误差校正值
def correction(time_month,time_day):
    time_month, time_day = count_time_before(time_month, time_day)
    tide_pre, warter_pre, xuliujing_test = main_def(time_month, time_day)
    # error_correction =  np.sum(((tide_pre + warter_pre) - xuliujing_test), axis=0) / len(warter_pre)
    if(xuliujing_test):
        error_correction =  (tide_pre + warter_pre) - xuliujing_test
        return error_correction
    return [];

# 最终预测水位效果图
def plot_error(tide_pre,warter_pre,xuliujing_test,time):
    if(xuliujing_test==[]):
        return []
    plt.figure()
    plt.plot(tide_pre + warter_pre, marker='o',color='blue', label='predicted tide')
    # plt.plot(tide_pre , marker='*', label='tide_pre')
    # plt.plot( warter_pre, marker='o', label='warter_pre')
    plt.plot(xuliujing_test, marker='+', color='orange',label='true tide')
    plt.plot((tide_pre + warter_pre) - xuliujing_test, marker='.', color='red',label='error')
    plt.title(time + " xuliujing tide water level")
    plt.legend()
    # plt.legend(loc='upper left')
    plt.xlabel("time/hour")
    plt.ylabel("water level/m")
    plt.ylim(-2, 8)
    plt.show();
    error = (tide_pre + warter_pre) - xuliujing_test
    return error;

# 日期类型转换
def intToStr(time_month,time_day):
    time_month = int(time_month)
    time_day = int(time_day)
    if(time_month<10):
        time_month = '0' + str(time_month)
    else:
        time_month = str(time_month)
    if(time_day<10):
        time_day = '0' + str(time_day)
    else:
        time_day = str(time_day)
    return time_month,time_day;


if __name__ == '__main__':

    # 预测时间
    time_month = 6
    time_day= 20

    # 误差校正
    error_correction = correction(time_month,time_day)
    # tide_pre + warter_pre 为最终预测潮位结果
    tide_pre,warter_pre,_ = main_def(time_month, time_day, error_correction)

    # 实测水位数据
    time_month,time_day = intToStr(time_month,time_day)
    wltest1 = '/2023-' + str(time_month) + '-' + str(time_day) + '%2000:00:00'
    wltest2 = '/2023-' + str(time_month) + '-' + str(time_day) + '%2023:00:00'
    # xuliujing_test 为真实潮位检测
    xuliujing_test = wl_xuliujing_test(wltest1, wltest2)

    # 最终误差结果
    error_final = plot_error(tide_pre,warter_pre,xuliujing_test,time_month+time_day)
    # 平均误差
    mean_error = np.sum(error_final, axis=0) / len(error_final)






