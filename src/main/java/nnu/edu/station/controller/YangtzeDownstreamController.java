package nnu.edu.station.controller;

import nnu.edu.station.common.result.JsonResult;
import nnu.edu.station.common.result.ResultUtils;
import nnu.edu.station.service.YangtzeDownstreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/16:36
 * @Description:
 */
@RestController
@RequestMapping("/YangtzeDownstream")
public class YangtzeDownstreamController {
    @Autowired
    YangtzeDownstreamService yangtzeDownstreamService;

    @RequestMapping(value = "/getAllInfoByStation/{station}", method = RequestMethod.GET)
    public JsonResult getAllInfoByStation(@PathVariable String station) {
        return ResultUtils.success(yangtzeDownstreamService.getAllInfoByStation(station));
    }

    @RequestMapping(value = "/getInfoByStationAndTime/{station}/{startTime}/{endTime}", method = RequestMethod.GET)
    public JsonResult getInfoByStationAndTime(@PathVariable String station, @PathVariable String startTime, @PathVariable String endTime) {
        return ResultUtils.success(yangtzeDownstreamService.getInfoByStationAndTime(station, startTime, endTime));
    }

}
