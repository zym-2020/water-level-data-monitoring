const WaterSecurity = require("./waterSecurity");
const request = require("request");
const fs = require("fs");
const sqlite = require("sqlite3").verbose();

const waterSecurity = new WaterSecurity();

// 时间字符串格式化
const dateFormat = (date, format) => {
  let dateObj = new Date(Date.parse(date));
  let fmt = format || "yyyy-MM-dd hh:mm:ss";
  //author: meizz
  var o = {
    "M+": dateObj.getMonth() + 1, //月份
    "d+": dateObj.getDate(), //日
    "h+": dateObj.getHours(), //小时
    "m+": dateObj.getMinutes(), //分
    "s+": dateObj.getSeconds(), //秒
    "q+": Math.floor((dateObj.getMonth() + 3) / 3), //季度
    S: dateObj.getMilliseconds(), //毫秒
  };
  if (/(y+)/.test(fmt))
    fmt = fmt.replace(
      RegExp.$1,
      (dateObj.getFullYear() + "").substr(4 - RegExp.$1.length)
    );
  for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt))
      fmt = fmt.replace(
        RegExp.$1,
        RegExp.$1.length == 1
          ? o[k].toString()
          : ("00" + o[k].toString()).substr(("" + o[k].toString()).length)
      );
  return fmt;
};

// 插入数据库
const insertData = (dbpath, waterLevelList, flowList, stationName) => {
  const db = new sqlite.Database(dbpath, (err) => {
    if (err) throw err;
  });
  for (let i = 0; i < waterLevelList.length; i++) {
    db.all(
      `select * from anhui_station where time = '${
        waterLevelList[i].TM + ":00"
      }' and station = '${stationName}'`,
      (err, row) => {
        if (err) throw err;
        else {
          if (row.length == 0) {
            db.run(
              `insert into anhui_station values('${
                waterLevelList[i].TM + ":00"
              }', '${stationName}', ${waterLevelList[i].Z}, null)`,
              (err) => {
                if (err) throw err;
              }
            );
          }
        }
      }
    );
  }
  for (let i = 0; i < flowList.length; i++) {
    db.all(
      `select * from anhui_station where time = '${
        flowList[i].TM + ":00"
      }' and station = '${stationName}'`,
      (err, row) => {
        if (err) throw err;
        else {
          if (row.length == 0) {
            db.run(
              `insert into anhui_station values('${
                flowList[i].TM + ":00"
              }', '${stationName}', null, ${flowList[i].Q})`,
              (err) => {
                if (err) throw err;
              }
            );
          } else {
            db.run(
              `update anhui_station set flow = ${flowList[i].Q} where time = '${
                flowList[i].TM + ":00"
              }' and station = '${stationName}'`,
              (err) => {
                if (err) throw err;
              }
            );
          }
        }
      }
    );
  }
  db.close();
};

// 访问接口并插入数据库
const getInfo = (dbpath, formData, stationName) => {
  request.post(
    {
      url: "http://61.191.22.196:5566/AHSXX/service/PublicBusinessHandler.ashx",
      form: formData,
    },
    async (err, res, body) => {
      if (!err && res.statusCode == 200) {
        const waterLevelList = JSON.parse(
          waterSecurity.decode(JSON.parse(body).data)
        ).data_sw;
        const flowList = JSON.parse(
          waterSecurity.decode(JSON.parse(body).data)
        ).data_q;

        insertData(dbpath, waterLevelList, flowList, stationName);
      }
    }
  );
};

const execute = (jsonPath, dbpath) => {
  const data = JSON.parse(fs.readFileSync(jsonPath, "utf8"));
  for (let i = 0; i < data.length; i++) {
    const startDate = new Date();
    const endDate = new Date();
    endDate.setTime(startDate.getTime() + 3600000);
    const formData = {
      name: waterSecurity.encode(data[i].name),
      stcd: waterSecurity.encode(data[i].stcd),
      waterEncode: waterSecurity.encode("true"),
      zxstcd: data[i].zxstcd == "" ? "" : waterSecurity.encode(data[i].zxstcd),
      sttp: data[i].sttp == "" ? "" : waterSecurity.encode(data[i].sttp),
      zxsttp: data[i].zxsttp == "" ? "" : waterSecurity.encode(data[i].zxsttp),
      random: Math.random(),
      btime: waterSecurity.encode(dateFormat(startDate, "yyyyMMddhh") + "00"),
      etime: waterSecurity.encode(dateFormat(endDate, "yyyyMMddhh") + "00"),
    };
    getInfo(dbpath, formData, data[i].station);
  }
};

const param = process.argv;
try {
  execute(param[2], param[3]);
} catch (e) {
  fs.writeFile(param[4], e, () => {});
}

