package nnu.edu.station.service.impl;

import nnu.edu.station.dao.level.AnhuiMapper;
import nnu.edu.station.service.AnhuiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:20
 * @Description:
 */
@Service
public class AnhuiServiceImpl implements AnhuiService {
    @Autowired
    AnhuiMapper anhuiMapper;

    @Override
    public List<Map<String, Object>> getInfoByStation(String station) {
        return anhuiMapper.getInfoByStation(station);
    }

    @Override
    public List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime) {
        return anhuiMapper.getInfoByStationAndTime(station, startTime, endTime);
    }
}
