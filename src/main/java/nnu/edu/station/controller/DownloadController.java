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

    @RequestMapping(value = "/downloadOne1/{fileName}", method = RequestMethod.GET)
    public void downloadOne(@PathVariable String fileName, HttpServletResponse response) {
        downloadService.downloadOne(fileName, response, 1);
    }

    @RequestMapping(value = "/downloadAll1", method = RequestMethod.GET)
    public void downloadAll(HttpServletResponse response) {
        downloadService.downloadAll(response, 1);
    }

    @RequestMapping(value = "/downloadByStationAndTime1/{station}/{startTime}/{endTime}", method = RequestMethod.GET)
    public void downloadByStationAndTime(@PathVariable String station, @PathVariable String startTime, @PathVariable String endTime, HttpServletResponse response) {
        downloadService.downloadByStationAndTime(station, startTime, endTime, response, 1);
    }

    @RequestMapping(value = "/downloadAllByStation1/{station}", method = RequestMethod.GET)
    public void downloadAllByStation(@PathVariable String station, HttpServletResponse response) {
        downloadService.downloadAllByStation(station, response, 1);
    }

    @RequestMapping(value = "/downloadOne2/{fileName}", method = RequestMethod.GET)
    public void downloadOne2(@PathVariable String fileName, HttpServletResponse response) {
        downloadService.downloadOne(fileName, response, 2);
    }

    @RequestMapping(value = "/downloadAll2", method = RequestMethod.GET)
    public void downloadAll2(HttpServletResponse response) {
        downloadService.downloadAll(response, 2);
    }

    @RequestMapping(value = "/downloadByStationAndTime2/{station}/{startTime}/{endTime}", method = RequestMethod.GET)
    public void downloadByStationAndTime2(@PathVariable String station, @PathVariable String startTime, @PathVariable String endTime, HttpServletResponse response) {
        downloadService.downloadByStationAndTime(station, startTime, endTime, response, 2);
    }

    @RequestMapping(value = "/downloadAllByStation2/{station}", method = RequestMethod.GET)
    public void downloadAllByStation2(@PathVariable String station, HttpServletResponse response) {
        downloadService.downloadAllByStation(station, response, 2);
    }

    @RequestMapping(value = "/downloadAllBySingleFile/{stationName}", method = RequestMethod.GET)
    public void downloadAllBySingleFile(@PathVariable String stationName, HttpServletResponse response) {
        downloadService.downloadAllBySingleFile(stationName, response);
    }

    @RequestMapping(value = "/downloadByStation/{station}", method = RequestMethod.GET)
    public void downloadByStation(@PathVariable String station, HttpServletResponse response) {
        downloadService.downloadByStation(station, response);
    }
}
