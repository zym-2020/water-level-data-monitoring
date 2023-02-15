import sys

import requests
import sqlite3
import json
import time
import os
import img_detect


def execute(json_path, db_path, png_path):
    with open(json_path, 'r', encoding='utf-8') as f:
        json_data = json.load(f)
        result_list = []
        f.close()
        for item in json_data:
            t = int(round(time.time() * 1000))
            param = {
                "t": t
            }
            res = requests.get(item['url'], param)
            if res.status_code == 200:
                img = res.content
                file = open(png_path + item['name_en'] + ".png", "wb")
                file.write(img)
                file.close()

        conn = sqlite3.connect(db_path)
        cur = conn.cursor()

        for item in json_data:
            if os.path.exists(png_path + item['name_en'] + ".png"):
                res_item_list = img_detect.execute(png_path + item['name_en'] + ".png", item['name'])
                for i in range(len(res_item_list)):
                    result_list.append(res_item_list[i])

        for i in range(len(result_list)):
            cur.execute("select * from jiangsu_station where time = ? and station = ?", (result_list[i]['time'], result_list[i]['station']))
            result = cur.fetchall()
            if len(result) == 0:
                cur.execute("insert into jiangsu_station values(?,?,?)", (result_list[i]['time'], result_list[i]['station'], result_list[i]['value']))
        conn.commit()
        cur.close()
        conn.close()


if __name__ == '__main__':
    json_path = sys.argv[1]
    db_path = sys.argv[2]
    png_path = sys.argv[3]
    log_path = sys.argv[4]
    try:
        execute(json_path, db_path, png_path)
    except Exception as e:
        file = open(log_path, 'a', encoding="utf-8")
        file.write(time.strftime('%Y-%m-%d %H:%M', time.localtime()) + "\t" + str(e) + "\n")
        file.close()
