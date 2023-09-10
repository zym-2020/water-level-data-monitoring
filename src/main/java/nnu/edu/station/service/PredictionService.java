package nnu.edu.station.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/05/25/22:20
 * @Description:
 */
public interface PredictionService {
    JSONObject getPrediction(String stationName);

    List<JSONObject> getAllPrediction();
}
