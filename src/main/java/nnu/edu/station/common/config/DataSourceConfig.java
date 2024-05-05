package nnu.edu.station.common.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2024/05/05/15:30
 * @Description:
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {
    @Bean("yangtzeDownstream")
    @ConfigurationProperties("spring.datasource.yangtze-downstream")
    public DataSource yangtzeDownstreamSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("zhejiang")
    @ConfigurationProperties("spring.datasource.zhejiang")
    public DataSource zhejiangSource() { return DataSourceBuilder.create().build(); }

    @Bean("jiangsu")
    @ConfigurationProperties("spring.datasource.jiangsu")
    public DataSource jiangsuSource() { return DataSourceBuilder.create().build(); }

    @Bean("anhui")
    @ConfigurationProperties("spring.datasource.anhui")
    public DataSource anhuiSource() { return DataSourceBuilder.create().build(); }

    @Bean("hubei")
    @ConfigurationProperties("spring.datasource.hubei")
    public DataSource hubeiSource() { return DataSourceBuilder.create().build(); }

    /**
     * 自定义动态数据源
     *
     * @return
     */
    @Bean("dynamicDataSource")
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("yangtzeDownstream", yangtzeDownstreamSource());
        dataSourceMap.put("zhejiang", zhejiangSource());
        dataSourceMap.put("jiangsu", jiangsuSource());
        dataSourceMap.put("anhui", anhuiSource());
        dataSourceMap.put("hubei", hubeiSource());

        // 默认数据源
        dynamicDataSource.setDefaultDataSource(yangtzeDownstreamSource());
        // 动态数据源
        dynamicDataSource.setDataSources(dataSourceMap);

        return dynamicDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        // 配置自定义动态数据源
        sessionFactory.setDataSource(dynamicDataSource());
        // 开启驼峰转下划线设置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
        // 实体、Mapper类映射
//        sessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return sessionFactory;
    }

    /**
     * 开启动态数据源@Transactional注解事务管理的支持
     *
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
