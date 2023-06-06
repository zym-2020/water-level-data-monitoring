import json
import sys

from selenium import webdriver
from selenium.webdriver.common.by import By
import sqlite3
from datetime import datetime
import time


def execute(json_path, db_path):
    f = open(json_path, 'r', encoding='utf-8')
    json_data = json.load(f)
    f.close()
    option = webdriver.EdgeOptions()
    option.add_argument("headless")
    driver = webdriver.Edge(options=option)
    driver.get('http://xy.cjh.com.cn/')

    conn = sqlite3.connect(db_path)
    cur = conn.cursor()

    for item in json_data:
        element = driver.find_elements(By.XPATH, item['xpath'])
        year = str(datetime.now().year)
        month = datetime.now().month
        if datetime.now().month < 10:
            month = '0' + str(month)
        day = element[1].text[:2]
        hour = element[1].text[4:6]
        now_time = year + "-" + month + "-" + day + " " + hour + ":00:00"
        cur.execute("select * from yangtze_downstream where time = ? and station = ?", (now_time, element[0].text if element[0].text != '南京' else '南京水文站'))
        res_list = cur.fetchall()
        if len(res_list) == 0:
            cur.execute("insert into yangtze_downstream values(?, ?, ?, ?)",
                        (now_time, element[0].text if element[0].text != '南京' else '南京水文站', element[2].text if element[2].text != '-' else None,
                         element[3].text if element[3].text != '-' else None))
            conn.commit()
    cur.close()
    conn.close()
    driver.close()


if __name__ == '__main__':
    json_param = sys.argv[1]
    db_param = sys.argv[2]
    log_param = sys.argv[3]

    try:
        execute(json_param, db_param)
    except Exception as e:
        file = open(log_param, "w", encoding="utf-8")
        file.write(time.strftime('%Y-%m-%d %H:%M', time.localtime()) + "\t" + str(e) + "\n")
        file.close()