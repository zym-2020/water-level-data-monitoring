package nnu.edu.station.service.impl;

import nnu.edu.station.dao.level.ZhejiangMapper;
import nnu.edu.station.service.ZhejiangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Map<String, Object>> mapList = zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
        return zhejiangMapper.getInfoByStationAndTime(station, startTime, endTime);
    }
}
