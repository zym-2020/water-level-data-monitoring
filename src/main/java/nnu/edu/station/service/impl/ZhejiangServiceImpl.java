package nnu.edu.station.service.impl;

import nnu.edu.station.dao.ZhejiangMapper;
import nnu.edu.station.service.ZhejiangService;
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
 * @Date: 2023/02/12/15:05
 * @Description:
 */
@Service
public class ZhejiangServiceImpl implements ZhejiangService {
    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Override
    public List<Map<String, Object>> getInfoByStation(String station) {
        return zhejiangMapper.getInfoByStation(station);
    }

    @Override
    public List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime) {
        return zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getDataBeforeTime(String station, Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = dateTime.format(formatter);
        return zhejiangMapper.getDataBeforeTime(station, dateString);
    }
}
