package nnu.edu.station.service.impl;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.service.DownloadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/12/15:51
 * @Description:
 */
@Service
@Slf4j
public class DownloadServiceImpl implements DownloadService {
    @Value("${basedir}")
    String basedir;

    @Override
    public void downloadOne(String fileName, HttpServletResponse response) {
        File file = new File(basedir + fileName);
        if (!file.exists()) {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
        try {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            InputStream in = new FileInputStream(file);
            ServletOutputStream sos = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = in.read(b)) > 0) {
                sos.write(b, 0, len);
            }
            sos.flush();
            sos.close();
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

    @Override
    public void downloadAll(HttpServletResponse response) {
        File file = new File(basedir + "all.zip");
        if (!file.exists()) {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
        try {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("all.zip", "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            InputStream in = new FileInputStream(file);
            ServletOutputStream sos = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = in.read(b)) > 0) {
                sos.write(b, 0, len);
            }
            sos.flush();
            sos.close();
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

    @Override
    public void downloadByStationAndTime(String station, String startTime, String endTime, HttpServletResponse response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            List<String> addresses = new ArrayList<>();
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            while (startDate.getTime() <= endDate.getTime()) {
                File file = new File(basedir + station + sdf.format(startDate.getTime()) + ".txt");
                if (file.exists()) {
                    addresses.add(basedir + station + sdf.format(startDate.getTime()) + ".txt");
                }
                cal.add(Calendar.DATE, 1);
                startDate = cal.getTime();
            }

            FileUtil.compressFile(basedir + station + startTime + "--" + endTime + ".zip", addresses);
            File file = new File(basedir + station + startTime + "--" + endTime + ".zip");
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(station + startTime + "--" + endTime + ".zip", "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            InputStream in = new FileInputStream(file);
            ServletOutputStream sos = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = in.read(b)) > 0) {
                sos.write(b, 0, len);
            }
            sos.flush();
            sos.close();
            in.close();
            file.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(-99, "请检查日期及格式是否错误");
        }
    }

    @Override
    public void downloadAllByStation(String station, HttpServletResponse response) {
        File file = new File(basedir + station + ".zip");
        if (!file.exists()) {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
        try {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(station + ".zip", "UTF-8"));
            response.addHeader("Content-Length", "" + file.length());
            InputStream in = new FileInputStream(file);
            ServletOutputStream sos = response.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = in.read(b)) > 0) {
                sos.write(b, 0, len);
            }
            sos.flush();
            sos.close();
            in.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }
}
