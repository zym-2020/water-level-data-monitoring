package nnu.edu.station.service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/16:29
 * @Description:
 */
public interface YangtzeDownstreamService {
    List<Map<String, Object>> getAllInfoByStation(String station);

    List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime);

}
