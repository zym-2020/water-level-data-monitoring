package nnu.edu.station.service.impl;

import nnu.edu.station.dao.HubeiMapper;
import nnu.edu.station.service.HubeiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Override
    public List<Map<String, Object>> getDataBeforeTime(String station, Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = dateTime.format(formatter);
        return hubeiMapper.getDataBeforeTime(station, dateString);
    }
}
