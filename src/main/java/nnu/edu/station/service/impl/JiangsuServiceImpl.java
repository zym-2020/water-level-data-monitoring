package nnu.edu.station.service.impl;

import nnu.edu.station.dao.JiangsuMapper;
import nnu.edu.station.service.JiangsuService;
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
 * @Date: 2023/02/11/22:10
 * @Description:
 */
@Service
public class JiangsuServiceImpl implements JiangsuService {
    @Autowired
    JiangsuMapper jiangsuMapper;

    @Override
    public List<Map<String, Object>> getInfoByStation(String station) {
        return jiangsuMapper.getInfoByStation(station);
    }

    @Override
    public List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime) {
        return jiangsuMapper.getInfoByStationAndTime(station, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getDataBeforeTime(String station, Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = dateTime.format(formatter);
        return jiangsuMapper.getDataBeforeTime(station, dateString);
    }
}
