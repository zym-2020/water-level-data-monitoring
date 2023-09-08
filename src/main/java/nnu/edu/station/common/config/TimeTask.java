package nnu.edu.station.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.common.utils.PredictionUtil;
import nnu.edu.station.common.utils.ProcessUtil;
import nnu.edu.station.dao.level.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/17:00
 * @Description:
 */
@Component
@Slf4j
public class TimeTask {
    @Value("${pythondir}")
    String pythonDir;

    @Value("${python}")
    String python;

    @Value("${waterleveldb}")
    String waterLevelDb;

    @Value("${waterlevellog}")
    String waterLevelLog;

    @Value("${yangtzeDownstreamStationJson}")
    String yangtzeDownstreamStationJson;

    @Value("${basedir}")
    String baseDir;

    @Value("${zhejianglog}")
    String zhejiangLog;

    @Value("${zhejiangjson}")
    String zhejiangJson;

    @Value("${anhuilog}")
    String anhuiLog;

    @Value("${anhuijson}")
    String anhuiJson;

    @Value("${nodescriptpath}")
    String nodeScriptPath;

    @Value("${jiangsulog}")
    String jiangsuLog;

    @Value("${jiangsujson}")
    String jiangsuJson;

    @Value("${pngpath}")
    String pngPath;

    @Value("${stationnamejson}")
    String stationNameJson;

    @Value("${predictionPath}")
    String predictionPath;

    @Value("${hubeiLog}")
    String hubeiLog;

    @Value("${hubeiJson}")
    String hubeiJson;

    @Value("${data-path}")
    String utcDataPath;

    @Value("${singleFileDir}")
    String singleFileDir;

    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Autowired
    JiangsuMapper jiangsuMapper;

    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Autowired
    AnhuiMapper anhuiMapper;

    @Autowired
    HubeiMapper hubeiMapper;


    @Scheduled(cron = "0 30 * * * ?")
    public void executePython() {
        try {
            /**
            * @Description:长江下游网定时任务
            * @Author: Yiming
            * @Date: 2023/2/8
            */
            List<String> commands = new ArrayList<>();
            commands.add("cmd");
            commands.add("/c");
            commands.add(python + " " + pythonDir + "yangtze_downstream.py " + yangtzeDownstreamStationJson + " " + waterLevelDb + " " + waterLevelLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();
            Map<String, List<JSONObject>> map = PredictionUtil.getPredictionStationList(stationNameJson);
            List<JSONObject> list = map.get("yangtze");
            for(int i = 0; i < list.size(); i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH");
                String model = Paths.get(predictionPath, list.get(i).getString("name_en"), "encapsulation.py").toString();
                String output = Paths.get(predictionPath, list.get(i).getString("name_en"), "result.json").toString();;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                String timeParam = simpleDateFormat.format(calendar.getTime()) + ":00:00";
                List<String> c = new ArrayList<>();
                c.add("cmd");
                c.add("/c");
                c.add(python + " " + model + " " + timeParam + " " + output);
                ProcessUtil.readProcessOutput(ProcessUtil.exeProcess(c).getInputStream(), System.out);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void executePythonZhejiang() {
        try {
            /**
             * @Description:浙江网定时任务
             * @Author: Yiming
             * @Date: 2023/2/8
             */
            List<String> commands = new ArrayList<>();
            commands.add(python);
            commands.add(pythonDir + "zhejiang.py");
            commands.add(waterLevelDb);
            commands.add(zhejiangLog);
            commands.add(zhejiangJson);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();
            Map<String, List<JSONObject>> map = PredictionUtil.getPredictionStationList(stationNameJson);
            List<JSONObject> list = map.get("zhejiang");
            for(int i = 0; i < list.size(); i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH");
                String model = Paths.get(predictionPath, list.get(i).getString("name_en"), "encapsulation.py").toString();
                String output = Paths.get(predictionPath, list.get(i).getString("name_en"), "result.json").toString();;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                String timeParam = simpleDateFormat.format(calendar.getTime()) + ":00:00";
                List<String> c = new ArrayList<>();
                c.add("cmd");
                c.add("/c");
                c.add(python + " " + model + " " + timeParam + " " + output);
                ProcessUtil.readProcessOutput(ProcessUtil.exeProcess(c).getInputStream(), System.out);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void executeNodeAnhui() {
        try {
            /**
             * @Description:安徽水情网定时任务
             * @Author: Yiming
             * @Date: 2023/2/8
             */
            List<String> commands = new ArrayList<>();
            commands.add("node");
            commands.add(nodeScriptPath + "index.js");
            commands.add(anhuiJson);
            commands.add(waterLevelDb);
            commands.add(anhuiLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();
            Map<String, List<JSONObject>> map = PredictionUtil.getPredictionStationList(stationNameJson);
            List<JSONObject> list = map.get("anhui");
            for(int i = 0; i < list.size(); i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH");
                String model = Paths.get(predictionPath, list.get(i).getString("name_en"), "encapsulation.py").toString();
                String output = Paths.get(predictionPath, list.get(i).getString("name_en"), "result.json").toString();;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                String timeParam = simpleDateFormat.format(calendar.getTime()) + ":00:00";
                List<String> c = new ArrayList<>();
                c.add("cmd");
                c.add("/c");
                c.add(python + " " + model + " " + timeParam + " " + output);
                ProcessUtil.readProcessOutput(ProcessUtil.exeProcess(c).getInputStream(), System.out);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void executePythonJiangsu() {
        try {
            /**
             * @Description:江苏水情网定时任务
             * @Author: Yiming
             * @Date: 2023/2/8
             */
            List<String> commands = new ArrayList<>();
            commands.add(python);
            commands.add(pythonDir + "jiangsu.py");
            commands.add(jiangsuJson);
            commands.add(waterLevelDb);
            commands.add(pngPath);
            commands.add(jiangsuLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    @Scheduled(cron = "0 30 * * * ?")
    public void predictJiangsu() {
        try {
            /**
             * @Description:江苏预报
             * @Author: Yiming
             * @Date: 2023/2/8
             */
            Map<String, List<JSONObject>> map = PredictionUtil.getPredictionStationList(stationNameJson);
            List<JSONObject> list = map.get("jiangsu");
            for(int i = 0; i < list.size(); i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH");
                String model = Paths.get(predictionPath, list.get(i).getString("name_en"), "encapsulation.py").toString();
                String output = Paths.get(predictionPath, list.get(i).getString("name_en"), "result.json").toString();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                String timeParam = simpleDateFormat.format(calendar.getTime()) + ":00:00";
                List<String> c = new ArrayList<>();
                c.add("cmd");
                c.add("/c");
                c.add(python + " " + model + " " + timeParam + " " + output);
                ProcessUtil.readProcessOutput(ProcessUtil.exeProcess(c).getInputStream(), System.out);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 30 * * * ?")
    public void executePythonHubei() {
        try {
            /**
             * @Description:湖北水情网定时任务
             * @Author: Yiming
             * @Date: 2023/2/8
             */
            List<String> commands = new ArrayList<>();
            commands.add("cmd");
            commands.add("/c");
            commands.add(python + " " + pythonDir + "hubei.py " + hubeiJson + " " + waterLevelDb + " " + hubeiLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void executeSaveText() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(date);
        String endTime = format.format(cal.getTime()) + " 07:00:00";
        String day = format.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String startTime = format.format(cal.getTime()) + " 08:00:00";

        JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
        List<String> addresses = new ArrayList<>();

        /**
        * @Description:长江下游水情
        * @Author: Yiming
        * @Date: 2023/2/13
        */
        List<String> downstreamStationList = yangtzeDownstreamMapper.getStationName();
        for (String station : downstreamStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                }
            }
            List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
        }

        /**
        * @Description:江苏文件保存
        * @Author: Yiming
        * @Date: 2023/2/11
        */
        List<String> stationList = jiangsuMapper.getStationName();
        for(String station : stationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                }
            }
            List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
        }

        /**
        * @Description:浙江文件保存
        * @Author: Yiming
        * @Date: 2023/2/12
        */
        List<String> zjStationList = zhejiangMapper.getStationName();
        for(String station : zjStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                }
            }
            List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
        }

        /**
        * @Description:安徽文件保存
        * @Author: Yiming
        * @Date: 2023/2/12
        */
        List<String> ahStationList = anhuiMapper.getStationName();
        for(String station : ahStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                }
            }
            List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
        }

        /**
         * @Description:湖北文件保存
         * @Author: Yiming
         * @Date: 2023/2/19
         */
        List<String> hbStationList = hubeiMapper.getStationName();
        for(String station : hbStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                }
            }
            List<Map<String, Object>> infoList = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
        }


        for(int i = 0; i < jsonArray.size(); i++) {
            String des = baseDir + jsonArray.getJSONObject(i).getString("name_en") + ".zip";
            List<String> addressList = new ArrayList<>();
            addressList.add(baseDir + jsonArray.getJSONObject(i).getString("name_en") + day + ".txt");
            FileUtil.compressFile(des, addressList);
        }

        String destination = baseDir + "all.zip";
        FileUtil.compressFile(destination, addresses);
    }


    @Scheduled(cron = "0 30 0 * * ?")
    public void executeSaveText2() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(date);
        String day = format.format(cal.getTime());
        cal.add(Calendar.DATE, -1);
        String endTime = format.format(cal.getTime()) + " 23:59:00";
        String startTime = format.format(cal.getTime()) + " 00:00:00";

        JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
        List<String> addresses = new ArrayList<>();

        /**
         * @Description:长江下游水情
         * @Author: Yiming
         * @Date: 2023/2/13
         */
        List<String> downstreamStationList = yangtzeDownstreamMapper.getStationName();
        for (String station : downstreamStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    break;
                }
            }
            List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
            String fileAddress = utcDataPath + name + "UTC+8" + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

            String str = "";
            for (Map<String, Object> map : infoList) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }

        /**
         * @Description:江苏文件保存
         * @Author: Yiming
         * @Date: 2023/2/11
         */
        List<String> stationList = jiangsuMapper.getStationName();
        for(String station : stationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    break;
                }
            }
            List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = utcDataPath + name + "UTC+8" + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

            String str = "";
            for (Map<String, Object> map : infoList) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }

        /**
         * @Description:浙江文件保存
         * @Author: Yiming
         * @Date: 2023/2/12
         */
        List<String> zjStationList = zhejiangMapper.getStationName();
        for(String station : zjStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    break;
                }
            }
            List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = utcDataPath + name + "UTC+8" + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

            String str = "";
            for (Map<String, Object> map : infoList) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }

        /**
         * @Description:安徽文件保存
         * @Author: Yiming
         * @Date: 2023/2/12
         */
        List<String> ahStationList = anhuiMapper.getStationName();
        for(String station : ahStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    break;
                }
            }
            List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = utcDataPath + name + "UTC+8" + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

            String str = "";
            for (Map<String, Object> map : infoList) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }

        /**
        * @Description:湖北文件保存
        * @Author: Yiming
        * @Date: 2023/2/19
        */
        List<String> hbStationList = hubeiMapper.getStationName();
        for(String station : hbStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            List<String> keys_cn = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    break;
                }
            }
            List<Map<String, Object>> infoList = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = utcDataPath + name + "UTC+8" + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

            String str = "";
            for (Map<String, Object> map : infoList) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }



        for(int i = 0; i < jsonArray.size(); i++) {
            String des = utcDataPath + jsonArray.getJSONObject(i).getString("name_en") + "UTC+8" + ".zip";
            List<String> addressList = new ArrayList<>();
            addressList.add(utcDataPath + jsonArray.getJSONObject(i).getString("name_en") + "UTC+8" + day + ".txt");
            FileUtil.compressFile(des, addressList);
        }

        String destination = utcDataPath + "allUTC+8.zip";
        FileUtil.compressFile(destination, addresses);

    }

}
