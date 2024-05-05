package nnu.edu.station.common.config;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.common.utils.ProcessUtil;
import nnu.edu.station.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Value("${yangtzeDownstreamDb}")
    String yangtzeDownstreamDb;

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

    @Value("${zhejiangDb}")
    String zhejiangDb;

    @Value("${anhuilog}")
    String anhuiLog;

    @Value("${anhuijson}")
    String anhuiJson;

    @Value("${anhuiDb}")
    String anhuiDb;

    @Value("${nodescriptpath}")
    String nodeScriptPath;

    @Value("${jiangsulog}")
    String jiangsuLog;

    @Value("${jiangsujson}")
    String jiangsuJson;

    @Value("${jiangsuDb}")
    String jiangsuDb;

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

    @Value("${hubeiDb}")
    String hubeiDb;

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
            commands.add(python);
            commands.add(pythonDir + "yangtze_downstream.py");
            commands.add(yangtzeDownstreamStationJson);
            commands.add(yangtzeDownstreamDb);
            commands.add(waterLevelLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();
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
            commands.add(zhejiangDb);
            commands.add(zhejiangLog);
            commands.add(zhejiangJson);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();

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
            commands.add(anhuiDb);
            commands.add(anhuiLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
            start.waitFor();
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
            commands.add(jiangsuDb);
            commands.add(pngPath);
            commands.add(jiangsuLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
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
            commands.add(python);
            commands.add(pythonDir + "hubei.py");
            commands.add(hubeiJson);
            commands.add(hubeiDb);
            commands.add(hubeiLog);
            Process start = ProcessUtil.exeProcess(commands);
            ProcessUtil.readProcessOutput(start.getInputStream(), System.out);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void executeSaveAllData() {
        LocalDate currentDate = LocalDate.now();  // 获取当前日期
        LocalDate previousDate = currentDate.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String endTime = currentDate.format(formatter) + " 00:00:00";
        String startTime = previousDate.format(formatter) + " 00:00:01";
        JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
        for (int i = 0; i < jsonArray.size(); i++) {
            String station = jsonArray.getJSONObject(i).getString("name");
            String name = jsonArray.getJSONObject(i).getString("name_en");
            String[] keys = jsonArray.getJSONObject(i).getJSONArray("keys").toArray(String.class);
            List<Map<String, Object>> data;
            switch (jsonArray.getJSONObject(i).getString("type")) {
                case "yangtze":
                    data = yangtzeDownstreamMapper.getInfoByStationAndTime(station, startTime, endTime);
                    break;
                case "jiangsu":
                    data = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                    break;
                case "anhui":
                    data = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                    break;
                case "zhejiang":
                    data = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                    break;
                default:
                    data = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
                    break;
            }
            String str = "";
            for (Map<String, Object> map : data) {
                str = str + map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
            }
            FileUtil.addFileContent(str, singleFileDir + name + ".txt");
        }
    }

}
