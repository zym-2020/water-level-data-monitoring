package nnu.edu.station.service;

import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/13/9:51
 * @Description:
 */
public interface ManageService {
    String quireEarliestTime(String type);

    String quireNowTime(String type);

    void cacheAll(JSONObject jsonObject);

    void cacheByType(JSONObject jsonObject);

    void cacheByStation(JSONObject jsonObject);

    void updateAllSingleFile(JSONObject jsonObject);
}
