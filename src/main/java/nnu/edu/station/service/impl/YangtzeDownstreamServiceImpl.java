package nnu.edu.station.service.impl;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.dao.level.YangtzeDownstreamMapper;
import nnu.edu.station.service.YangtzeDownstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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
}
