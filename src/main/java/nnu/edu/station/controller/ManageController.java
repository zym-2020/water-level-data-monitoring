package nnu.edu.station.controller;

import com.alibaba.fastjson2.JSONObject;
import nnu.edu.station.common.result.JsonResult;
import nnu.edu.station.common.result.ResultUtils;
import nnu.edu.station.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/13/9:50
 * @Description:
 */
@RestController
@RequestMapping("/manage")
public class ManageController {
    @Autowired
    ManageService manageService;

    @RequestMapping(value = "/quireEarliestTime/{type}", method = RequestMethod.GET)
    public JsonResult quireEarliestTime(@PathVariable String type) {
        return ResultUtils.success(manageService.quireEarliestTime(type));
    }

    @RequestMapping(value = "/quireNowTime/{type}", method = RequestMethod.GET)
    public JsonResult quireNowTime(@PathVariable String type) {
        return ResultUtils.success(manageService.quireNowTime(type));
    }

    @RequestMapping(value = "/cacheAll", method = RequestMethod.POST)
    public JsonResult cacheAll(@RequestBody JSONObject jsonObject) {
        manageService.cacheAll(jsonObject);
        return ResultUtils.success();
    }

    @RequestMapping(value = "/cacheByType", method = RequestMethod.POST)
    public JsonResult cacheByType(@RequestBody JSONObject jsonObject) {
        manageService.cacheByType(jsonObject);
        return ResultUtils.success();
    }

    @RequestMapping(value = "/cacheByStation", method = RequestMethod.POST)
    public JsonResult cacheByStation(@RequestBody JSONObject jsonObject) {
        manageService.cacheByStation(jsonObject);
        return ResultUtils.success();
    }

    @RequestMapping(value = "/updateAllSingleFile", method = RequestMethod.POST)
    public JsonResult updateAllSingleFile(@RequestBody JSONObject jsonObject) {
        manageService.updateAllSingleFile(jsonObject);
        return ResultUtils.success();
    }
}
