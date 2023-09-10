package nnu.edu.station.service.impl;

import com.alibaba.fastjson2.JSONObject;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.service.PredictionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        String path = Paths.get(predictionPath, stationName, "result.json").toString();
        return FileUtil.readJsonObjectFile(path);
    }

    @Override
    public List<JSONObject> getAllPrediction() {
        File file = new File(predictionPath);
        if (!file.exists()) {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
        String[] list = file.list();
        List<JSONObject> res = new ArrayList<>();
        for (String name : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("res", getPrediction(name));
            res.add(jsonObject);
        }
        return res;
    }
}
