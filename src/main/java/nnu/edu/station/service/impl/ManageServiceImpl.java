package nnu.edu.station.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.dao.level.*;
import nnu.edu.station.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/13/9:51
 * @Description:
 */
@Service
@Slf4j
public class ManageServiceImpl implements ManageService {
    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Autowired
    JiangsuMapper jiangsuMapper;

    @Autowired
    AnhuiMapper anhuiMapper;

    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Autowired
    HubeiMapper hubeiMapper;

    @Value("${stationnamejson}")
    String stationNameJson;

    @Value("${basedir}")
    String baseDir;

    @Value("${data-path}")
    String utcDataPath;

    @Value("${singleFileDir}")
    String singleFileDir;

    @Override
    public String quireEarliestTime(String type) {
        if (type.equals("anhui")) {
            return anhuiMapper.quireEarliestTime();
        } else if (type.equals("jiangsu")) {
            return jiangsuMapper.quireEarliestTime();
        } else if (type.equals("zhejiang")) {
            return zhejiangMapper.quireEarliestTime();
        } else if (type.equals("downstream")) {
            return yangtzeDownstreamMapper.quireEarliestTime();
        } else if (type.equals("hubei")) {
            return hubeiMapper.quireEarliestTime();
        } else {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public String quireNowTime(String type) {
        if (type.equals("anhui")) {
            return anhuiMapper.quireNowTime();
        } else if (type.equals("jiangsu")) {
            return jiangsuMapper.quireNowTime();
        } else if (type.equals("zhejiang")) {
            return zhejiangMapper.quireNowTime();
        } else if (type.equals("downstream")) {
            return yangtzeDownstreamMapper.quireNowTime();
        } else if (type.equals("hubei")) {
            return hubeiMapper.quireNowTime();
        } else {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheAll(JSONObject jsonObject) {
        int flag = jsonObject.getIntValue("flag");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            String time = jsonObject.getString("time");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime, endTime;
            if (flag == 1) {
                startTime = sdf.format(cal.getTime()) + " 08:00:00";
                endTime = time + " 07:00:00";
            } else if (flag == 2) {
                startTime = sdf.format(cal.getTime()) + " 00:00:00";
                endTime = sdf.format(cal.getTime()) + " 23:59:00";
            } else {
                throw new MyException(-99, "参数flag错误");
            }

            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            List<String> addresses = new ArrayList<>();

            /**
            * @Description:长江下游文件缓存
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
                if (name.equals("")) {
                    System.out.println(station);
                }
                List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
            }

            /**
            * @Description:安徽文件缓存
            * @Author: Yiming
            * @Date: 2023/2/13
            */
            List<String> ahStationList = anhuiMapper.getStationName();
            for (String station : ahStationList) {
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
                if (name.equals("")) {
                    System.out.println(station);
                }
                List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
            }

            /**
            * @Description:江苏文件缓存
            * @Author: Yiming
            * @Date: 2023/2/13
            */
            List<String> jsStationList = jiangsuMapper.getStationName();
            for (String station : jsStationList) {
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
                if (name.equals("")) {
                    System.out.println(station);
                }
                List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
            }

            /**
            * @Description:浙江文件缓存
            * @Author: Yiming
            * @Date: 2023/2/13
            */
            List<String> zjStationList = zhejiangMapper.getStationName();
            for (String station : zjStationList) {
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
                if (name.equals("")) {
                    System.out.println(station);
                }
                List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
            }

            /**
            * @Description:湖北文件缓存
            * @Author: Yiming
            * @Date: 2023/2/19
            */
            List<String> hbStationList = hubeiMapper.getStationName();
            for (String station : hbStationList) {
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
                if (name.equals("")) {
                    System.out.println(station);
                }
                List<Map<String, Object>> infoList = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);
            }


            for (int i = 0; i < jsonArray.size(); i++) {
                if (flag == 1) {
                    String des = baseDir + jsonArray.getJSONObject(i).getString("name_en") + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + jsonArray.getJSONObject(i).getString("name_en") + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + jsonArray.getJSONObject(i).getString("name_en") + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + jsonArray.getJSONObject(i).getString("name_en") + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }

            }

            if (flag == 1) {
                String destination = baseDir + "all.zip";
                FileUtil.compressFile(destination, addresses);
            } else {
                String destination = utcDataPath + "allUTC+8.zip";
                FileUtil.compressFile(destination, addresses);
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheByType(JSONObject jsonObject) {
        int flag = jsonObject.getIntValue("flag");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            String type = jsonObject.getString("type");
            String time = jsonObject.getString("time");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime, endTime;
            if (flag == 1) {
                startTime = sdf.format(cal.getTime()) + " 08:00:00";
                endTime = time + " 07:00:00";
            } else if (flag == 2) {
                startTime = sdf.format(cal.getTime()) + " 00:00:00";
                endTime = sdf.format(cal.getTime()) + " 23:59:00";
            } else {
                throw new MyException(-99, "参数flag错误");
            }
            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            List<String> addresses = new ArrayList<>();

            if (type.equals("downstream")) {
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

                    String fileAddress;
                    if (flag == 1) {
                        fileAddress = baseDir + name + time + ".txt";
                    } else {
                        fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                    }
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                    if (flag == 1) {
                        String des = baseDir + name + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(baseDir + name + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    } else {
                        String des = utcDataPath + name + "UTC+8" + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    }
                }
            } else if (type.equals("anhui")) {
                List<String> ahStationList = anhuiMapper.getStationName();
                for (String station : ahStationList) {
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
                    String fileAddress;
                    if (flag == 1) {
                        fileAddress = baseDir + name + time + ".txt";
                    } else {
                        fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                    }
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                    if (flag == 1) {
                        String des = baseDir + name + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(baseDir + name + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    } else {
                        String des = utcDataPath + name + "UTC+8" + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    }
                }
            } else if (type.equals("jiangsu")) {
                List<String> ahStationList = jiangsuMapper.getStationName();
                for (String station : ahStationList) {
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
                    String fileAddress;
                    if (flag == 1) {
                        fileAddress = baseDir + name + time + ".txt";
                    } else {
                        fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                    }
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                    if (flag == 1) {
                        String des = baseDir + name + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(baseDir + name + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    } else {
                        String des = utcDataPath + name + "UTC+8" + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    }
                }
            } else if (type.equals("zhejiang")) {
                List<String> ahStationList = zhejiangMapper.getStationName();
                for (String station : ahStationList) {
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
                    String fileAddress;
                    if (flag == 1) {
                        fileAddress = baseDir + name + time + ".txt";
                    } else {
                        fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                    }
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                    if (flag == 1) {
                        String des = baseDir + name + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(baseDir + name + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    } else {
                        String des = utcDataPath + name + "UTC+8" + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    }
                }
            } else if (type.equals("hubei")) {
                List<String> ahStationList = hubeiMapper.getStationName();
                for (String station : ahStationList) {
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
                    String fileAddress;
                    if (flag == 1) {
                        fileAddress = baseDir + name + time + ".txt";
                    } else {
                        fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                    }
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                    if (flag == 1) {
                        String des = baseDir + name + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(baseDir + name + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    } else {
                        String des = utcDataPath + name + "UTC+8" + ".zip";
                        List<String> addressList = new ArrayList<>();
                        addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                        FileUtil.compressFile(des, addressList);
                    }
                }
            } else {
                throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
            }

            if (flag == 1) {
                String destination = baseDir + "all.zip";
                FileUtil.compressFile(destination, addresses);
            } else {
                String destination = utcDataPath + "allUTC+8.zip";
                FileUtil.compressFile(destination, addresses);
            }

        } catch (Exception e) {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheByStation(JSONObject jsonObject) {
        int flag = jsonObject.getIntValue("flag");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            String type = jsonObject.getString("type");
            String time = jsonObject.getString("time");
            String station = jsonObject.getString("station");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime, endTime;
            if (flag == 1) {
                startTime = sdf.format(cal.getTime()) + " 08:00:00";
                endTime = time + " 07:00:00";
            } else if (flag == 2) {
                startTime = sdf.format(cal.getTime()) + " 00:00:00";
                endTime = sdf.format(cal.getTime()) + " 23:59:00";
            } else {
                throw new MyException(-99, "参数flag错误");
            }

            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            List<String> addresses = new ArrayList<>();

            if (type.equals("downstream")) {
                List<String> keys = new ArrayList<>();
                List<String> keys_cn = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    }
                }
                List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                if (flag == 1) {
                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + name + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + name + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("anhui")) {
                List<String> keys = new ArrayList<>();
                List<String> keys_cn = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    }
                }
                List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                if (flag == 1) {
                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + name + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + name + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("jiangsu")) {
                List<String> keys = new ArrayList<>();
                List<String> keys_cn = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    }
                }
                List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                if (flag == 1) {
                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + name + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + name + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("zhejiang")) {
                List<String> keys = new ArrayList<>();
                List<String> keys_cn = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    }
                }
                List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                if (flag == 1) {
                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + name + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + name + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("hubei")) {
                List<String> keys = new ArrayList<>();
                List<String> keys_cn = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        keys_cn = (jsonArray.getObject(i, JSONObject.class)).getObject("keys_cn", List.class);
                    }
                }
                List<Map<String, Object>> infoList = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress;
                if (flag == 1) {
                    fileAddress = baseDir + name + time + ".txt";
                } else {
                    fileAddress = utcDataPath + name + "UTC+8" + time + ".txt";
                }
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys, keys_cn);

                if (flag == 1) {
                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(baseDir + name + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                } else {
                    String des = utcDataPath + name + "UTC+8" + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(utcDataPath + name + "UTC+8" + time + ".txt");
                    FileUtil.compressFile(des, addressList);
                }
            } else {
                throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
            }

            if (flag == 1) {
                String destination = baseDir + "all.zip";
                FileUtil.compressFile(destination, addresses);
            } else {
                String destination = utcDataPath + "allUTC+8.zip";
                FileUtil.compressFile(destination, addresses);
            }
        } catch (Exception e) {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void updateAllSingleFile(JSONObject jsonObject) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = jsonObject.getString("time") + " 23:59:00";
        try {

            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            for (int i = 0; i < jsonArray.size(); i++) {
                String station = (jsonArray.getObject(i, JSONObject.class)).getString("name");
                String name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                String type = (jsonArray.getObject(i, JSONObject.class)).getString("type");
                List<String> keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                String startTime = format.format(new Date((jsonArray.getObject(i, JSONObject.class)).getList("startTime", Long.class).get(0)));
                List<Map<String, Object>> infoList;
                if (type.equals("yangtze")) {
                    infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
                } else if (type.equals("anhui")) {
                    infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                } else if (type.equals("zhejiang")) {
                    infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                } else if (type.equals("jiangsu")) {
                    infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                } else if (type.equals("hubei")) {
                    infoList = hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
                } else {
                    throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
                }
                String content = "";
                for (Map<String, Object> map : infoList) {
                    content = content + map.get("time");
                    for (String key : keys) {
                        content = content + "\t" + map.get(key);
                    }
                    content += "\n";
                }
                FileUtil.writeFile(content, singleFileDir + name + ".txt");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }
}
