package nnu.edu.station.common.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.dao.level.AnhuiMapper;
import nnu.edu.station.dao.level.JiangsuMapper;
import nnu.edu.station.dao.level.YangtzeDownstreamMapper;
import nnu.edu.station.dao.level.ZhejiangMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Autowired
    JiangsuMapper jiangsuMapper;

    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Autowired
    AnhuiMapper anhuiMapper;


    @Scheduled(cron = "0 30,55 * * * ?")
    public void executePython() {
        try {
            /**
            * @Description:长江下游网定时任务
            * @Author: Yiming
            * @Date: 2023/2/8
            */
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add(python);
            commands.add(pythonDir + "cjh.py");
            commands.add(waterLevelDb);
            commands.add(waterLevelLog);
            processBuilder.command(commands);
            processBuilder.start();

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
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add(python);
            commands.add(pythonDir + "zhejiang.py");
            commands.add(waterLevelDb);
            commands.add(zhejiangLog);
            commands.add(zhejiangJson);
            processBuilder.command(commands);
            processBuilder.start();

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
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add("node");
            commands.add(nodeScriptPath + "index.js");
            commands.add(anhuiJson);
            commands.add(waterLevelDb);
            commands.add(anhuiLog);
            processBuilder.command(commands);
            processBuilder.start();

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
            ProcessBuilder processBuilder = new ProcessBuilder();
            List<String> commands = new ArrayList<>();
            commands.add(python);
            commands.add(pythonDir + "jiangsu.py");
            commands.add(jiangsuJson);
            commands.add(waterLevelDb);
            commands.add(pngPath);
            commands.add(jiangsuLog);
            processBuilder.command(commands);
            processBuilder.start();
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
        * @Description:
        * @Author: Yiming
        * @Date: 2023/2/13
        */
        List<String> downstreamStationList = yangtzeDownstreamMapper.getStationName();
        for (String station : downstreamStationList) {
            String name = "";
            List<String> keys = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                }
            }
            List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys);
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
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                }
            }
            List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys);
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
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                }
            }
            List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys);
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
            for (int i = 0; i < jsonArray.size(); i++) {
                if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                    name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                    keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                }
            }
            List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
            String fileAddress = baseDir + name + day + ".txt";
            addresses.add(fileAddress);
            FileUtil.saveFile(infoList, fileAddress, keys);
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


}
