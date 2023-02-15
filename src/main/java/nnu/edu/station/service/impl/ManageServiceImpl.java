package nnu.edu.station.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.dao.level.AnhuiMapper;
import nnu.edu.station.dao.level.JiangsuMapper;
import nnu.edu.station.dao.level.YangtzeDownstreamMapper;
import nnu.edu.station.dao.level.ZhejiangMapper;
import nnu.edu.station.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/13/9:51
 * @Description:
 */
@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Autowired
    JiangsuMapper jiangsuMapper;

    @Autowired
    AnhuiMapper anhuiMapper;

    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Value("${stationnamejson}")
    String stationNameJson;

    @Value("${basedir}")
    String baseDir;

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
        } else {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheAll(JSONObject jsonObject) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            String time = jsonObject.getString("time");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime = sdf.format(cal.getTime()) + " 08:00:00";
            String endTime = time + " 07:00:00";
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
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);
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
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);
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
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);
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
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);
            }


            for (int i = 0; i < jsonArray.size(); i++) {
                String des = baseDir + jsonArray.getJSONObject(i).getString("name_en") + ".zip";
                List<String> addressList = new ArrayList<>();
                addressList.add(baseDir + jsonArray.getJSONObject(i).getString("name_en") + time + ".txt");
                FileUtil.compressFile(des, addressList);
            }

            String destination = baseDir + "all.zip";
            FileUtil.compressFile(destination, addresses);

        } catch (Exception e) {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheByType(JSONObject jsonObject) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            String type = jsonObject.getString("type");
            String time = jsonObject.getString("time");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime = sdf.format(cal.getTime()) + " 08:00:00";
            String endTime = time + " 07:00:00";
            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            List<String> addresses = new ArrayList<>();

            if (type.equals("downstream")) {
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
                    String fileAddress = baseDir + name + time + ".txt";
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys);

                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(fileAddress);
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("anhui")) {
                List<String> ahStationList = anhuiMapper.getStationName();
                for (String station : ahStationList) {
                    String name = "";
                    List<String> keys = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                            name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                            keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        }
                    }
                    List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                    String fileAddress = baseDir + name + time + ".txt";
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys);

                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(fileAddress);
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("jiangsu")) {
                List<String> jsStationList = jiangsuMapper.getStationName();
                for (String station : jsStationList) {
                    String name = "";
                    List<String> keys = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                            name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                            keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        }
                    }
                    List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                    String fileAddress = baseDir + name + time + ".txt";
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys);

                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(fileAddress);
                    FileUtil.compressFile(des, addressList);
                }
            } else if (type.equals("zhejiang")) {
                List<String> zjStationList = zhejiangMapper.getStationName();
                for (String station : zjStationList) {
                    String name = "";
                    List<String> keys = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                            name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                            keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                        }
                    }
                    List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                    String fileAddress = baseDir + name + time + ".txt";
                    addresses.add(fileAddress);
                    FileUtil.saveFile(infoList, fileAddress, keys);

                    String des = baseDir + name + ".zip";
                    List<String> addressList = new ArrayList<>();
                    addressList.add(fileAddress);
                    FileUtil.compressFile(des, addressList);
                }
            } else {
                throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
            }

            String destination = baseDir + "all.zip";
            FileUtil.compressFile(destination, addresses);

        } catch (Exception e) {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }

    @Override
    public void cacheByStation(JSONObject jsonObject) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            String type = jsonObject.getString("type");
            String time = jsonObject.getString("time");
            String station = jsonObject.getString("station");
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(time));
            cal.add(Calendar.DATE, -1);
            String startTime = sdf.format(cal.getTime()) + " 08:00:00";
            String endTime = time + " 07:00:00";

            JSONArray jsonArray = FileUtil.readJsonArrayFile(stationNameJson);
            List<String> addresses = new ArrayList<>();

            if (type.equals("downstream")) {
                List<String> keys = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = yangtzeDownstreamMapper.getInfoByStationAndTimeAsc(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);

                String des = baseDir + name + ".zip";
                List<String> addressList = new ArrayList<>();
                addressList.add(fileAddress);
                FileUtil.compressFile(des, addressList);
            } else if (type.equals("anhui")) {
                List<String> keys = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);

                String des = baseDir + name + ".zip";
                List<String> addressList = new ArrayList<>();
                addressList.add(fileAddress);
                FileUtil.compressFile(des, addressList);
            } else if (type.equals("jiangsu")) {
                List<String> keys = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);

                String des = baseDir + name + ".zip";
                List<String> addressList = new ArrayList<>();
                addressList.add(fileAddress);
                FileUtil.compressFile(des, addressList);
            } else if (type.equals("zhejiang")) {
                List<String> keys = new ArrayList<>();
                String name = "";
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (station.equals((jsonArray.getObject(i, JSONObject.class)).getString("name"))) {
                        name = (jsonArray.getObject(i, JSONObject.class)).getString("name_en");
                        keys = (jsonArray.getObject(i, JSONObject.class)).getObject("keys", List.class);
                    }
                }
                List<Map<String, Object>> infoList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
                String fileAddress = baseDir + name + time + ".txt";
                addresses.add(fileAddress);
                FileUtil.saveFile(infoList, fileAddress, keys);

                String des = baseDir + name + ".zip";
                List<String> addressList = new ArrayList<>();
                addressList.add(fileAddress);
                FileUtil.compressFile(des, addressList);
            } else {
                throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
            }

            String destination = baseDir + "all.zip";
            FileUtil.compressFile(destination, addresses);
        } catch (Exception e) {
            throw new MyException(ResultEnum.QUERY_TYPE_ERROR);
        }
    }
}
