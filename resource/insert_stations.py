# 将station_name.json插入sqlite
import json
import sqlite3


def execute(json_path, db_path):
    f = open(json_path, 'r', encoding='utf-8')
    data = f.read()
    print(data)
    json_data = json.loads(data)
    f.close()
    # print(json_data)


execute('station_name.json', '')
