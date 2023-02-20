import sys

import requests
import sqlite3
import json
import time
import os
import img_detect


def param_util(column_list, res_arr, station):
    tuple_list = ['', station, '', '', '']
    for i in range(len(column_list)):
        if column_list[i]['key'] == 'time':
            tuple_list[0] = res_arr[i]
        elif column_list[i]['key'] == 'upstream_water_level':
            tuple_list[2] = float(res_arr[i]) if res_arr[i] != '' else None
        elif column_list[i]['key'] == 'downstream_water_level':
            tuple_list[3] = float(res_arr[i]) if res_arr[i] != '' else None
        elif column_list[i]['key'] == 'flow':
            tuple_list[4] = float(res_arr[i]) if res_arr[i] != '' else None
    res = tuple(tuple_list)
    return res


def execute(json_path, db_path, png_path):
    with open(json_path, 'r', encoding='utf-8') as f:
        json_data = json.load(f)

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
                result_list = img_detect.execute(png_path + item['name_en'] + ".png", item['range'])
                for i in range(len(result_list)):
                    cur.execute("select * from jiangsu_station where time = ? and station = ?",
                                (result_list[i][0], item['name']))
                    result = cur.fetchall()
                    if len(result) == 0:
                        tup = param_util(item['range'], result_list[i], item['name'])
                        cur.execute("insert into jiangsu_station values(?,?,?,?,?)", tup)
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

