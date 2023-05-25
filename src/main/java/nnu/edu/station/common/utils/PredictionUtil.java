package nnu.edu.station.common.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/05/25/20:01
 * @Description:
 */
public class PredictionUtil {
    public static Map<String, List<JSONObject>> getPredictionStationList(String stationJson) {
        JSONArray jsonArray = FileUtil.readJsonArrayFile(stationJson);
        Map<String, List<JSONObject>> map = new HashMap<>();

        List<JSONObject> yangtze = new ArrayList<>();
        List<JSONObject> jiangsu = new ArrayList<>();
        List<JSONObject> zhejiang = new ArrayList<>();
        List<JSONObject> anhui = new ArrayList<>();
        List<JSONObject> hubei = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.getBoolean("prediction")) {
                switch (jsonObject.getString("type")) {
                    case "yangtze":
                        yangtze.add(jsonObject);
                        break;
                    case "jiangsu":
                        jiangsu.add(jsonObject);
                        break;
                    case "zhejiang":
                        zhejiang.add(jsonObject);
                    case "anhui":
                        anhui.add(jsonObject);
                    case "hubei":
                        hubei.add(jsonObject);
                }
            }
        }
        map.put("yangtze", yangtze);
        map.put("jiangsu", jiangsu);
        map.put("zhejiang", zhejiang);
        map.put("anhui", anhui);
        map.put("hubei", hubei);
        return map;
    }
}
