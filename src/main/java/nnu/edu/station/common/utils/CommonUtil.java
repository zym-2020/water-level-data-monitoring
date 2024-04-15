package nnu.edu.station.common.utils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/10/01/21:42
 * @Description:
 */
public class CommonUtil {
    public static String getTableName(String time) {
        String year = time.substring(0, 4);
        int month = Integer.parseInt(time.substring(5, 7));
        if (month >= 1 && month <= 6) {
            return year + "01";
        } else {
            return year + "07";
        }
    }
}
