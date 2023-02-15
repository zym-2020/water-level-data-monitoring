package nnu.edu.station.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:20
 * @Description:
 */
public interface AnhuiService {
    List<Map<String, Object>> getInfoByStation(String station);

    List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime);
}
