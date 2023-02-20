import sys
import time
import requests
import sqlite3
import json


def flow_util(flow_str):
    if flow_str == '':
        return None
    else:
        return flow_str.replace('<br>', ' ')


def execute(json_path, db_path):
    f = open(json_path, 'r', encoding='utf-8')
    json_data = json.load(f)
    f.close()

    conn = sqlite3.connect(db_path)
    cur = conn.cursor()

    url = "http://113.57.190.228:8001/Web/Report/GetRiverData"
    date = time.strftime('%Y-%m-%d %H', time.localtime()) + ":00"
    param = {
        "date": date
    }
    res = requests.get(url, param)
    if res.status_code == 200:
        result_list = res.json()['rows']
        for item in result_list:
            for station in json_data:
                if item['STNM'] == station['name']:
                    t = date + ":00"
                    name = item['STNM']
                    water_level = item['Z'] if item['Z'] != '' else None
                    flow = flow_util(item['Q'])
                    cur.execute("select * from hubei_station where time = ? and  station = ?", (t, name))
                    res_list = cur.fetchall()
                    if len(res_list) == 0:
                        cur.execute("insert into hubei_station values(?,?,?,?)", (t, name, water_level, flow))
                        conn.commit()
                elif item['STNM1'] == station['name']:
                    t = date + ":00"
                    name = item['STNM1']
                    water_level = item['Z1'] if item['Z1'] != '' else None
                    flow = flow_util(item['Q1'])
                    cur.execute("select * from hubei_station where time = ? and  station = ?", (t, name))
                    res_list = cur.fetchall()
                    if len(res_list) == 0:
                        cur.execute("insert into hubei_station values(?,?,?,?)", (t, name, water_level, flow))
                        conn.commit()
    cur.close()
    conn.close()


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