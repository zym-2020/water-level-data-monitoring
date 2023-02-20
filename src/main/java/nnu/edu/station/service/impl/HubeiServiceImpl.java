package nnu.edu.station.service.impl;

import nnu.edu.station.dao.level.HubeiMapper;
import nnu.edu.station.service.HubeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/19/21:23
 * @Description:
 */
@Service
public class HubeiServiceImpl implements HubeiService {
    @Autowired
    HubeiMapper hubeiMapper;

    @Override
    public List<Map<String, Object>> getInfoByStation(String station) {
        return hubeiMapper.getInfoByStation(station);
    }

    @Override
    public List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime) {
        return hubeiMapper.getInfoByStationAndTime(station, startTime, endTime);
    }
}
