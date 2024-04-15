package nnu.edu.station.service.impl;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.dao.YangtzeDownstreamMapper;
import nnu.edu.station.service.YangtzeDownstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/16:30
 * @Description:
 */
@Service
@Slf4j
public class YangtzeDownstreamServiceImpl implements YangtzeDownstreamService {
    @Value("${basedir}")
    String basedir;

    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Override
    public List<Map<String, Object>> getAllInfoByStation(String station) {
        return yangtzeDownstreamMapper.getAllInfoByStation(station);
    }

    @Override
    public List<Map<String, Object>> getInfoByStationAndTime(String station, String startTime, String endTime) {
        return yangtzeDownstreamMapper.getInfoByStationAndTime(station, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getDataBeforeTime(String station, Long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = dateTime.format(formatter);
        return yangtzeDownstreamMapper.getDataBeforeTime(station, dateString);
    }
}
