package nnu.edu.station.service.impl;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;
import nnu.edu.station.common.utils.FileUtil;
import nnu.edu.station.dao.*;
import nnu.edu.station.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Value("${data-path}")
    String utcDataPath;

    @Value("${singleFileDir}")
    String singleFileDir;

    @Value("${tempDir}")
    String tempDir;

    @Autowired
    YangtzeDownstreamMapper yangtzeDownstreamMapper;

    @Autowired
    JiangsuMapper jiangsuMapper;

    @Autowired
    AnhuiMapper anhuiMapper;

    @Autowired
    ZhejiangMapper zhejiangMapper;

    @Autowired
    HubeiMapper hubeiMapper;

    @Override
    public void downloadOne(String fileName, HttpServletResponse response, int flag) {
        String path = "";
        if (flag == 1) {
            path = basedir + fileName;
        } else {
            path = utcDataPath + fileName;
        }
        File file = new File(path);
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
    public void downloadAll(HttpServletResponse response, int flag) {
        String path = "";
        if (flag == 1) {
            path = basedir + "all.zip";
        } else {
            path = utcDataPath + "all.zip";
        }
        File file = new File(path);
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
    public void downloadByStationAndTime(String station, String startTime, String endTime, HttpServletResponse response, int flag) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            List<String> addresses = new ArrayList<>();
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            while (startDate.getTime() <= endDate.getTime()) {
                String path = "";
                if (flag == 1) {
                    path = basedir + station + sdf.format(startDate.getTime()) + ".txt";
                } else {
                    path = utcDataPath + station + "UTC+8" + sdf.format(startDate.getTime()) + ".txt";
                }
                File file = new File(path);
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
    public void downloadAllByStation(String station, HttpServletResponse response, int flag) {
        String path = "";
        if (flag == 1) {
            path = basedir + station + ".zip";
        } else {
            path = utcDataPath + station + "UTC+8" + ".zip";
        }
        File file = new File(path);
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

    @Override
    public void downloadAllBySingleFile(String stationName, HttpServletResponse response) {
        String path = singleFileDir + stationName + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
        try {
            response.setContentType("application/octet-stream");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fileName = stationName + simpleDateFormat.format(new Date()) + ".txt";
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
    public void downloadByStation(String station, HttpServletResponse response) {
        String path1 = Paths.get(basedir, station + ".zip").toString();
        String path2 = Paths.get(utcDataPath, station + "UTC+8.zip").toString();
        List<String> addresses = new ArrayList<>();
        addresses.add(path1);
        addresses.add(path2);
        String id = UUID.randomUUID().toString();
        FileUtil.compressFile(Paths.get(tempDir, id + ".zip").toString(), addresses);
        File file = new File(Paths.get(tempDir, id + ".zip").toString());
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
            file.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void downloadNow(String type, String stationName, HttpServletResponse response) throws IOException {
        String tempPath = Paths.get(tempDir, stationName + ".txt").toString();
        List<Map<String, Object>> data;
        switch (type) {
            case "yangtze":
                data = yangtzeDownstreamMapper.getAllInfoByStation(stationName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath))) {
                    for (Map<String, Object> map : data) {
                        writer.write(map.get("time") + "\t" + map.get("flow") + "\t" + map.get("waterLevel") + "\n");
                    }
                } catch (IOException e) {
                    log.error("写入文件时出现错误：" + e.getMessage());
                }
                break;
            case "jiangsu":
                data = jiangsuMapper.getInfoByStation(stationName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath))) {
                    for (Map<String, Object> map : data) {
                        writer.write(map.get("time") + "\t" + map.get("upstreamWaterLevel") + "\t" + map.get("downstreamWaterLevel") + "\t" + map.get("flow") + "\n");
                    }
                } catch (IOException e) {
                    log.error("写入文件时出现错误：" + e.getMessage());
                }
                break;
            case "anhui":
                data = anhuiMapper.getInfoByStation(stationName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath))) {
                    for (Map<String, Object> map : data) {
                        writer.write(map.get("time") + "\t" + map.get("waterLevel") + "\t" + map.get("flow") + "\n");
                    }
                } catch (IOException e) {
                    log.error("写入文件时出现错误：" + e.getMessage());
                }
                break;
            case "zhejiang":
                data = zhejiangMapper.getInfoByStation(stationName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath))) {
                    for (Map<String, Object> map : data) {
                        writer.write(map.get("time") + "\t" + map.get("rainfall") + "\t" + map.get("waterLevel") + "\t" + map.get("input") + "\t" + map.get("output") + "\n");
                    }
                } catch (IOException e) {
                    log.error("写入文件时出现错误：" + e.getMessage());
                }
                break;
            default:
                data = hubeiMapper.getInfoByStation(stationName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempPath))) {
                    for (Map<String, Object> map : data) {
                        writer.write(map.get("time") + "\t" + map.get("waterLevel") + "\t" + map.get("flow") + "\n");
                    }
                } catch (IOException e) {
                    log.error("写入文件时出现错误：" + e.getMessage());
                }
                break;
        }
        File file = new File(tempPath);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(stationName + ".txt", "UTF-8"));
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

    }
}
