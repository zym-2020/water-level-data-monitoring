import json
import sys

from selenium import webdriver
from time import sleep
from selenium.webdriver.common.by import By
import sqlite3
from datetime import datetime
import time


# 水库 时间、雨量、水位、库容、人工出库流量、人工入库流量6列表格
# 河道 时间、雨量、水位、人工出库流量、人工入库流量5列表格
# 堰闸 时间、雨量、水位、人工出库流量、人工入库流量5列表格
# 潮汐 时间、雨量、水位、人工出库流量、人工入库流量5列表格
def test():
    option = webdriver.EdgeOptions()
    option.add_argument("headless")
    driver = webdriver.Edge(options=option)
    url1 = 'https://sqfb.zjsq.net.cn:8089/nuxtsyq/new/MarkInfo?zh=70702895&zm=%E5%B3%99%E5%8D%97&day=1'
    url2 = 'https://sqfb.zjsq.net.cn:8089/nuxtsyq/new/MarkInfo?zh=63010950&zm=%E8%B5%B5%E6%B9%BE%E6%B0%B4%E5%BA%93&day=1'
    # 潮汐，河道，堰闸
    # 河道、堰闸的基础信息与潮汐不同
    driver.get(url1)

    # 河道、堰闸基础信息
    # key_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][2]/div[contains(@class, 'name-label')]")
    # value_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][2]/div[contains(@class, 'val-label')]")
    # for i in range(len(key_arr)):
    #     print(key_arr[i].text, value_arr[i].text)

    # 潮汐基础信息
    key_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][3]/div[contains(@class, 'name-label')]")
    value_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][3]/div[contains(@class, 'val-label')]")
    for i in range(len(key_arr)):
        print(key_arr[i].text, value_arr[i].text)

    time_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_2   el-table__cell']/div")
    rain_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_3   el-table__cell']/div")
    water_lever_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_4   el-table__cell']/div")
    out_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_5   el-table__cell']/div")
    enter_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_6   el-table__cell']/div")
    print(len(time_arr))
    js = 'document.getElementsByClassName("el-table--fit")[0].style.height="800px"'
    driver.execute_script(js)
    for i in range(len(time_arr)):
        print(time_arr[i].text, rain_arr[i].text, water_lever_arr[i].text, out_arr[i].text, enter_arr[i].text)

    driver.close()

    # 水库
    driver.get(url2)

    # 水库的信息
    key_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][1]/div[contains(@class, 'name-label')]")
    value_arr = driver.find_elements(By.XPATH, "//div[@class='listTb'][1]/div[contains(@class, 'val-label')]")
    for i in range(len(key_arr)):
        print(key_arr[i].text, value_arr[i].text)

    time_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_2   el-table__cell']/div")
    rain_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_3   el-table__cell']/div")
    water_lever_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_4   el-table__cell']/div")
    volume_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_5   el-table__cell']/div")
    out_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_6   el-table__cell']/div")
    enter_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_7   el-table__cell']/div")
    print(len(time_arr))
    js = 'document.getElementsByClassName("el-table--fit")[0].style.height="800px"'
    driver.execute_script(js)
    for i in range(len(time_arr)):
        print(time_arr[i].text, rain_arr[i].text, water_lever_arr[i].text, volume_arr[i].text, out_arr[i].text, enter_arr[i].text)

    driver.close()


# 获取所以站点的位置及zh，zm
def get_location_info():
    with open('./point.json', 'r', encoding='utf-8') as f:
        json_data = json.load(f)
        # print(json_data['qt'])
        print(len(json_data['qt']))
        f.close()


# 爬取一次数据
def get_info(url, station_type):
    result = []
    option = webdriver.EdgeOptions()
    option.add_argument("headless")
    driver = webdriver.Edge(options=option)
    driver.get(url)
    year = datetime.now().year
    if station_type == 'RR':
        time_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_2   el-table__cell']/div")
        rain_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_3   el-table__cell']/div")
        water_lever_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_4   el-table__cell']/div")
        volume_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_5   el-table__cell']/div")
        out_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_6   el-table__cell']/div")
        enter_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_7   el-table__cell']/div")
        js = 'document.getElementsByClassName("el-table--fit")[0].style.height="800px"'
        driver.execute_script(js)
        for i in range(len(time_arr)):
            result.append({
                "time": None if time_arr[i].text == '-' else str(year) + '-' + time_arr[i].text + ":00",
                "rainfall": rain_arr[i].text,
                "waterLevel": water_lever_arr[i].text,
                "capacity": volume_arr[i].text,
                "output": out_arr[i].text,
                "input": enter_arr[i].text
            })
        driver.close()
    else:
        time_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_2   el-table__cell']/div")
        rain_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_3   el-table__cell']/div")
        water_lever_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_4   el-table__cell']/div")
        out_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_5   el-table__cell']/div")
        enter_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_6   el-table__cell']/div")
        js = 'document.getElementsByClassName("el-table--fit")[0].style.height="800px"'
        driver.execute_script(js)
        for i in range(len(time_arr)):
            result.append({
                "time": None if time_arr[i].text == '-' else str(year) + '-' + time_arr[i].text + ":00",
                "rainfall": rain_arr[i].text,
                "waterLevel": water_lever_arr[i].text,
                "output": out_arr[i].text,
                "input": enter_arr[i].text
            })
        driver.close()
    return result


# 数据入库
def save_data(db_path, station_type, station_name, data_list):
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    for item in data_list:
        capacity = item['capacity'] if station_type == 'RR' else None
        cur.execute("select * from zhejiang_station where time = ? and station = ?", (item['time'], station_name))
        res_list = cur.fetchall()
        if len(res_list) == 0 and item['time'] is not None:

            cur.execute("insert into zhejiang_station values(?,?,?,?,?,?,?)",
                        (item['time'], station_name,
                         item['rainfall'] if item['rainfall'] != '-' else None,
                         item['waterLevel'] if item['waterLevel'] != '-' else None,
                         item['output'] if item['output'] != '-' else None,
                         item['input'] if item['input'] != '-' else None,
                         capacity if capacity != '-' else None))
    conn.commit()
    cur.close()
    conn.close()


def execute(json_path, db_path):
    with open(json_path, 'r', encoding='utf-8') as f:
        json_data = json.load(f)
        count = 0
        for item in json_data:
            url = 'https://sqfb.zjsq.net.cn:8089/nuxtsyq/new/MarkInfo?zh=' + item['zh'] + '&zm=' + item['zm'] + '&day=1'
            station_type = item['zl']
            result_list = get_info(url, station_type)
            save_data(db_path, station_type, item['zm'], result_list)
            count += 1
            print(count)
            sleep(5)
        f.close()


if __name__ == '__main__':
    db_path = sys.argv[1]
    log_path = sys.argv[2]
    json_path = sys.argv[3]
    try:
        execute(json_path, db_path)
    except Exception as e:
        file = open(log_path, 'a', encoding="utf-8")
        file.write(time.strftime('%Y-%m-%d %H:%M', time.localtime()) + "\t" + str(e) + "\n")
        file.close()
