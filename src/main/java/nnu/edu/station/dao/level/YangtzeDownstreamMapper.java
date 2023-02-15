package nnu.edu.station.dao.level;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/16:26
 * @Description:
 */
@Repository
public interface YangtzeDownstreamMapper {
    List<Map<String, Object>> getAllInfoByStation(@Param("station") String station);

    List<Map<String, Object>> getInfoByStationAndTime(@Param("station") String station, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<Map<String, Object>> getInfoByStationAndTimeAsc(@Param("station") String station, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<String> getStationName();

    String quireEarliestTime();

    String quireNowTime();
}
