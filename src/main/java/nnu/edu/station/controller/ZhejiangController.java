package nnu.edu.station.controller;

import nnu.edu.station.common.result.JsonResult;
import nnu.edu.station.common.result.ResultUtils;
import nnu.edu.station.service.ZhejiangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:06
 * @Description:
 */
@RestController
@RequestMapping("/zhejiang")
public class ZhejiangController {
    @Autowired
    ZhejiangService zhejiangService;

    @RequestMapping(value = "/getInfoByStation/{station}", method = RequestMethod.GET)
    public JsonResult getInfoByStation(@PathVariable String station) {
        return ResultUtils.success(zhejiangService.getInfoByStation(station));
    }

    @RequestMapping(value = "/getInfoByStationAndTime/{station}/{startTime}/{endTime}", method = RequestMethod.GET)
    public JsonResult getInfoByStationAndTime(@PathVariable String station, @PathVariable String startTime, @PathVariable String endTime) {
        return ResultUtils.success(zhejiangService.getInfoByStationAndTime(station, startTime, endTime));
    }

    @RequestMapping(value = "/getDataBeforeTime/{station}/{timestamp}", method = RequestMethod.GET)
    public JsonResult getDataBeforeTime(@PathVariable String station, @PathVariable Long timestamp) {
        return ResultUtils.success(zhejiangService.getDataBeforeTime(station, timestamp));
    }
}
