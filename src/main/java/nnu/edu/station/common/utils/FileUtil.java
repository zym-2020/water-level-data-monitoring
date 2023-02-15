package nnu.edu.station.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import net.lingala.zip4j.ZipFile;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/08/14:36
 * @Description:
 */
public class FileUtil {
    public static void compressFile(String destination, List<String> addresses) {
        ZipFile zipFile = new ZipFile(destination);
        try {
            List<File> files = new ArrayList<>();
            for (String address : addresses) {
                files.add(new File(address));
            }
            zipFile.addFiles(files);
        } catch (Exception e) {
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

    public static JSONArray readJsonArrayFile(String path) {
        try {
            InputStreamReader fReader = new InputStreamReader(new FileInputStream(path),"UTF-8");
            BufferedReader br = new BufferedReader(fReader);
            String jsonString = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                jsonString += line;
            }
            fReader.close();
            br.close();
            return JSON.parseArray(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

    public static JSONObject readJsonObjectFile(String path) {
        try {
            InputStreamReader fReader = new InputStreamReader(new FileInputStream(path),"UTF-8");
            BufferedReader br = new BufferedReader(fReader);
            String jsonString = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                jsonString += line;
            }
            fReader.close();
            br.close();
            return JSON.parseObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

    /**
    * @Description:文本文件缓存
    * @Author: Yiming
    * @Date: 2023/2/13
    */
    public static void saveFile(List<Map<String, Object>> list, String address, List<String> keys) {
        try {
            FileWriter fileWriter = new FileWriter(address);
            for (Map<String, Object> map : list) {
                String str = map.get("time").toString();
                for (String key : keys) {
                    str = str + "\t" + map.get(key);
                }
                str += "\n";
                fileWriter.write(str);
            }
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        }
    }

}