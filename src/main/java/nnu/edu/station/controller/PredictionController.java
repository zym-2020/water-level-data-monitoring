package nnu.edu.station.controller;

import nnu.edu.station.common.result.JsonResult;
import nnu.edu.station.common.result.ResultUtils;
import nnu.edu.station.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/05/25/22:19
 * @Description:
 */
@RestController
@RequestMapping("/prediction")
public class PredictionController {
    @Autowired
    PredictionService predictionService;

    @RequestMapping(value = "/getPrediction/{stationName}", method = RequestMethod.GET)
    public JsonResult getPrediction(@PathVariable String stationName) {
        return ResultUtils.success(predictionService.getPrediction(stationName));
    }

    @RequestMapping(value = "/getAllPrediction", method = RequestMethod.GET)
    public JsonResult getAllPrediction() {
        return ResultUtils.success(predictionService.getAllPrediction());
    }
}
