package nnu.edu.station.service.impl;

import com.alibaba.fastjson2.JSONObject;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.service.PredictionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/05/25/22:20
 * @Description:
 */
@Service
public class PredictionServiceImpl implements PredictionService {
    @Value("${predictionPath}")
    String predictionPath;

    @Override
    public JSONObject getPrediction(String stationName) {
        String path = predictionPath + stationName + "/result.json";
        return FileUtil.readJsonObjectFile(path);
    }
}
