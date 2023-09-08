package nnu.edu.station.common.utils;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.exception.MyException;
import nnu.edu.station.common.result.ResultEnum;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/09/08/9:06
 * @Description:
 */
@Slf4j
public class ProcessUtil {
    public static void readProcessOutput(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readProcessString(InputStream inputStream) {
        try {
            String str = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("GBK")));
            String line;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException(ResultEnum.DEFAULT_EXCEPTION);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Process exeProcess(List<String> commands) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        return processBuilder.start();
    }
}
