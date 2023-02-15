package nnu.edu.station.service;

import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:51
 * @Description:
 */
public interface DownloadService {
    void downloadOne(String fileName, HttpServletResponse response);

    void downloadAll(HttpServletResponse response);

    void downloadByStationAndTime(String station, String startTime, String endTime, HttpServletResponse response);

    void downloadAllByStation(String station, HttpServletResponse response);
}
