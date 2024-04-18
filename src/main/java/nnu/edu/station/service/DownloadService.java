package nnu.edu.station.service;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:51
 * @Description:
 */
public interface DownloadService {
    void downloadOne(String fileName, HttpServletResponse response, int flag);

    void downloadAll(HttpServletResponse response, int flag);

    void downloadByStationAndTime(String station, String startTime, String endTime, HttpServletResponse response, int flag);

    void downloadAllByStation(String station, HttpServletResponse response, int flag);

    void downloadAllBySingleFile(String stationName, HttpServletResponse response);

    void downloadByStation(String station, HttpServletResponse response);

    void downloadNow(String type, String stationName, HttpServletResponse response) throws IOException;
}
