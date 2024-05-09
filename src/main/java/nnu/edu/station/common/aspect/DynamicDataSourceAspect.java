package nnu.edu.station.common.aspect;

import lombok.extern.slf4j.Slf4j;
import nnu.edu.station.common.config.DataSourceContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2024/05/05/15:39
 * @Description:
 */
@Order(1)
@Aspect
@Component
@Slf4j
public class DynamicDataSourceAspect {
    @Before("execution(* nnu.edu.station.dao.YangtzeDownstreamMapper.*(..))")
    public void switchYangtzeDownstream() {
        if (!DataSourceContextHolder.getDataSourceKey().equals("yangtzeDownstream")) {
            DataSourceContextHolder.setDataSourceKey("yangtzeDownstream");
        }
    }

    @Before("execution(* nnu.edu.station.dao.ZhejiangMapper.*(..))")
    public void switchZhejiang() {
        if (!DataSourceContextHolder.getDataSourceKey().equals("zhejiang")) {
            DataSourceContextHolder.setDataSourceKey("zhejiang");
        }
    }

    @Before("execution(* nnu.edu.station.dao.JiangsuMapper.*(..))")
    public void switchJiangsu() {
        if (!DataSourceContextHolder.getDataSourceKey().equals("jiangsu")) {
            DataSourceContextHolder.setDataSourceKey("jiangsu");
        }
    }

    @Before("execution(* nnu.edu.station.dao.AnhuiMapper.*(..))")
    public void switchAnhui() {
        if (!DataSourceContextHolder.getDataSourceKey().equals("anhui")) {
            DataSourceContextHolder.setDataSourceKey("anhui");
        }
    }


    @Before("execution(* nnu.edu.station.dao.HubeiMapper.*(..))")
    public void switchHubei() {
        if (!DataSourceContextHolder.getDataSourceKey().equals("hubei")) {
            DataSourceContextHolder.setDataSourceKey("hubei");
        }
    }
}
