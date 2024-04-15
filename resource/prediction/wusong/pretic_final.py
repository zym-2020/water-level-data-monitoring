from Water_Level import *
from t_tide import *

def version_01():
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

    param = t_q_res(flow_q, mean_water_level, tide_range)  # 同时考虑径流和潮差的回归分析

    verification_f(mean_water_level, flow_q, tide_range, param)  # 日平均水位预测模型验证

    # 返回预测水位
    warter_pre = water_pre_year(flow_q, tide_range, param)
    # 365天预测逐时水位
    warter_pre_h = warter_pre_hour(warter_pre)

    dataf_h = time_everyday(dataf)

    hour_water, firsttime, finaltime = interpolation(dataf_h)
    tfit_e = tt.t_tide(hour_water)
    tide_water = tfit_e['xout']

    plt.plot(tide_water, color='blue', label='tide')
    plt.title("tide water level")
    plt.legend()
    plt.xlabel("time/hour")
    plt.ylabel("water level/m")
    # plt.xlim(100, 2000)
    plt.show()

    # final
    final_pre_water = []
    for s in range(firsttime, finaltime + 1):
        final_pre_water.append(tide_water[s - firsttime] + warter_pre_h[s])

    plt.plot(final_pre_water, color='black', label='final_water')
    plt.title("final water level")
    plt.legend()
    plt.xlabel("time/hour")
    plt.ylabel("water level/m")
    # plt.xlim(100, 200)
    plt.show()

if __name__ == '__main__':

    version_01()

