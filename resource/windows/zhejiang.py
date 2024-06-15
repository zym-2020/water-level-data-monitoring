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


# 爬取一次数据
def get_info(url, station_type):
    result = []
    option = webdriver.EdgeOptions()
    option.add_argument("headless")
    driver = webdriver.Edge(options=option)
    driver.get(url)
    year = datetime.now().year
    time_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_2   el-table__cell']/div")
    # rain_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_3   el-table__cell']/div")
    water_lever_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_4   el-table__cell']/div")
    # volume_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_5   el-table__cell']/div")
    # out_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_6   el-table__cell']/div")
    # enter_arr = driver.find_elements(By.XPATH, "//td[@class='el-table_1_column_7   el-table__cell']/div")
    js = 'document.getElementsByClassName("el-table--fit")[0].style.height="1000px"'
    driver.execute_script(js)
    for i in range(len(time_arr)):
        result.append({
            "time": None if time_arr[i].text == '-' else str(year) + '-' + time_arr[i].text + ":00",
            # "rainfall": rain_arr[i].text,
            "waterLevel": water_lever_arr[i].text,
            # "capacity": volume_arr[i].text,
            # "output": out_arr[i].text,
            # "input": enter_arr[i].text
        })
    driver.close()
    return result


# 数据入库
def save_data(db_path, station_type, station_name, data_list):
    conn = sqlite3.connect(db_path)
    cur = conn.cursor()
    for item in data_list:
        cur.execute("insert or ignore into zhejiang_station values(?,?,?,?,?,?,?)",
                        (item['time'], station_name,
                         None,
                         item['waterLevel'] if item['waterLevel'] != '-' else None,
                         None,
                         None,
                         None))
            
    conn.commit()
    cur.close()
    conn.close()


def execute(json_path, db_path):
    with open(json_path, 'r', encoding='utf-8') as f:
        json_data = json.load(f)
        count = 0
        for item in json_data:
            url = 'https://sqfb.slt.zj.gov.cn:30050/nuxtsyq/new/MarkInfo?zh=' + item['zh'] + '&zm=' + item['zm'] + '&day=1'
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
