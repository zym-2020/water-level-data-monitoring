package nnu.edu.station.common.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2024/05/05/15:28
 * @Description:
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private Map<Object, Object> dataSources = new HashMap<>();
    /**
     * 获取当前数据源的键
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceKey();
    }

    /**
     * 获取当前数据源
     */
    @Override
    protected DataSource determineTargetDataSource() {
        return super.determineTargetDataSource();
    }

    /**
     * 设置默认数据源
     *
     * @param defaultDataSource
     */
    public void setDefaultDataSource(Object defaultDataSource) {
        super.setDefaultTargetDataSource(defaultDataSource);
    }


    /**
     * 设置数据源
     *
     * @param dataSources
     */
    public void setDataSources(Map<Object, Object> dataSources) {
        this.dataSources = dataSources;
        super.setTargetDataSources(dataSources);
        // 保存数据源的key
        DataSourceContextHolder.addDataSourceKeys(dataSources.keySet());
    }

    /**
     * 追加数据源
     *
     * @param key
     * @param dataSource
     */
    public void addDataSource(String key, DataSource dataSource) {
        this.dataSources.put(key, dataSource);
        super.setTargetDataSources(dataSources);
        // 保存数据源的key
        DataSourceContextHolder.addDataSourceKey(key);
        // 加载新的数据源
        super.afterPropertiesSet();
    }
}
