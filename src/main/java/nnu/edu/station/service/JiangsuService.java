package nnu.edu.station.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/11/22:10
 * @Description:
 */
public interface JiangsuService {
    List<Map<String, Object>> getInfoByStation(String station);

    List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime);
}
