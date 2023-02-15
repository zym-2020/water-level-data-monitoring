package nnu.edu.station.common.exception;

import lombok.Data;
import nnu.edu.station.common.result.ResultEnum;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2023/02/07/16:18
 * @Description:
 */
@Data
public class MyException extends RuntimeException{
    Integer code;
    String msg;

    public MyException() {
        super();
    }

    public MyException(ResultEnum resultEnum) {
        super();
        this.code = resultEnum.getCode();
        this.msg = resultEnum.getMsg();
    }

    public MyException(Integer code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }
}
