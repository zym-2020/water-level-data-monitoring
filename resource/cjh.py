import sys

from selenium import webdriver
from selenium.webdriver.common.by import By
import sqlite3
from datetime import datetime
import time


def execute(db_path, log_path):
    try:
        option = webdriver.EdgeOptions()
        option.add_argument("headless")
        driver = webdriver.Edge(options=option)
        driver.get('http://xy.cjh.com.cn/')
        jiujiang = driver.find_elements(By.XPATH, "//tbody[@id='sssq'][1]/tr[2]/td")
        datong = driver.find_elements(By.XPATH, "//tbody[@id='sssq'][1]/tr[7]/td")

        conn = sqlite3.connect(db_path)
        cur = conn.cursor()

        year = str(datetime.now().year)
        month = datetime.now().month
        if datetime.now().month < 10:
            month = '0' + str(month)
        day = jiujiang[1].text[:2]
        hour = jiujiang[1].text[4:6]
        now_time = year + "-" + month + "-" + day + " " + hour + ":00:00"
        cur.execute("select * from yangtze_downstream where time = ? and station = ?", (now_time, jiujiang[0].text))
        res_list = cur.fetchall()
        if len(res_list) == 0:
            cur.execute("insert into yangtze_downstream values(?, ?, ?, ?)",
                        (now_time, jiujiang[0].text, jiujiang[2].text if jiujiang[2].text != '-' else None, jiujiang[3].text if jiujiang[3].text != '-' else None))

        cur.execute("select * from yangtze_downstream where time = ? and station = ?", (now_time, datong[0].text))
        res_list = cur.fetchall()
        if len(res_list) == 0:
            cur.execute("insert into yangtze_downstream values(?, ?, ?, ?)",
                        (now_time, datong[0].text, datong[2].text if datong[2].text != '-' else None, datong[3].text if datong[3].text != '-' else None))
        conn.commit()
        cur.close()
        conn.close()
        driver.close()
    except Exception as e:
        file = open(log_path, 'a', encoding="utf-8")
        file.write(time.strftime('%Y-%m-%d %H:%M', time.localtime()) + "\t" + str(e) + "\n")
        file.close()


if __name__ == '__main__':
    db_path = sys.argv[1]
    log_path = sys.argv[2]
    execute(db_path, log_path)


