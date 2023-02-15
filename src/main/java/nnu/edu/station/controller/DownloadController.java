package nnu.edu.station.controller;

import nnu.edu.station.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:50
 * @Description:
 */
@RestController
@RequestMapping("/download")
public class DownloadController {
    @Autowired
    DownloadService downloadService;

    @RequestMapping(value = "/downloadOne/{fileName}", method = RequestMethod.GET)
    public void downloadOne(@PathVariable String fileName, HttpServletResponse response) {
        downloadService.downloadOne(fileName, response);
    }

    @RequestMapping(value = "/downloadAll", method = RequestMethod.GET)
    public void downloadAll(HttpServletResponse response) {
        downloadService.downloadAll(response);
    }

    @RequestMapping(value = "/downloadByStationAndTime/{station}/{startTime}/{endTime}", method = RequestMethod.GET)
    public void downloadByStationAndTime(@PathVariable String station, @PathVariable String startTime, @PathVariable String endTime, HttpServletResponse response) {
        downloadService.downloadByStationAndTime(station, startTime, endTime, response);
    }

    @RequestMapping(value = "/downloadAllByStation/{station}", method = RequestMethod.GET)
    public void downloadAllByStation(@PathVariable String station, HttpServletResponse response) {
        downloadService.downloadAllByStation(station, response);
    }
}
