package com.ibg.receipt.api.config;

import com.ibg.receipt.db.DataSourceRouter;
import com.ibg.receipt.db.DataSourceTypeEnum;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ibg.receipt")
public class FundDbConfig {

    @Primary
    @Bean(name = "fundMasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource fundMasterDataSource() {
        return   DataSourceBuilder.create().type(BasicDataSource.class).build();
    }

    @Bean(name = "fundSlaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource fundSlaveDataSource() {
        return  DataSourceBuilder.create().type(BasicDataSource.class).build();
    }


    @Bean(name = "fundDataSource")
    public DataSource fundDataSource(@Qualifier("fundMasterDataSource") DataSource masterDataSource,
                                     @Qualifier("fundSlaveDataSource") DataSource slaveDataSource) {
        DataSourceRouter dataSource = new DataSourceRouter();
        Map<Object, Object> targetDataSourcesMap = new HashMap<Object, Object>();
        targetDataSourcesMap.put(DataSourceTypeEnum.MASTER, masterDataSource);
//        targetDataSourcesMap.put(DataSourceTypeEnum.SLAVE, slaveDataSource);
        dataSource.setTargetDataSources(targetDataSourcesMap);
        dataSource.setDefaultTargetDataSource(masterDataSource);
        return dataSource;
    }

    @Bean(name = "fundJdbcTemplate")
    public JdbcTemplate fundJdbcTemplate(@Qualifier("fundDataSource") DataSource fundDataSource) {
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(fundDataSource);
        return template;
    }

    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("fundJdbcTemplate") JdbcTemplate fundJdbcTemplate) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(fundJdbcTemplate);
        return template;
    }

}
