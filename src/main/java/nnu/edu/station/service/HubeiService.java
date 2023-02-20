package nnu.edu.station.service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/19/21:22
 * @Description:
 */
public interface HubeiService {
    List<Map<String, Object>> getInfoByStation(String station);

    List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime);
}
