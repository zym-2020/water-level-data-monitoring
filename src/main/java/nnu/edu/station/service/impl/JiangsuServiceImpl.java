package nnu.edu.station.service.impl;

import nnu.edu.station.dao.level.JiangsuMapper;
import nnu.edu.station.service.JiangsuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
