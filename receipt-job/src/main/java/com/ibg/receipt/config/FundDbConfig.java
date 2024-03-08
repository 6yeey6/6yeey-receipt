package com.ibg.receipt.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ibg.receipt.db.DataSourceRouter;
import com.ibg.receipt.db.DataSourceTypeEnum;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(entityManagerFactoryRef = "fundEntityManagerFactory", transactionManagerRef = "fundTransactionManager", basePackages = {
        "com.ibg.receipt" })
public class FundDbConfig {
    @Autowired
    private JpaProperties jpaProperties;


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

    @Bean
    public LocalContainerEntityManagerFactoryBean fundEntityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("fundDataSource") DataSource fundDataSource) {
        return builder.dataSource(fundDataSource)
            .properties(jpaProperties.getHibernateProperties(fundDataSource))
                .packages("com.ibg.receipt").build();
    }

    @Bean
    public EntityManager fundEntityManager(EntityManagerFactoryBuilder builder,
                                           @Qualifier("fundDataSource") DataSource fundDataSource) {
        return fundEntityManagerFactory(builder,fundDataSource).getObject().createEntityManager();
    }

    @Bean
    public JpaTransactionManager fundTransactionManager(EntityManagerFactoryBuilder builder,@Qualifier("fundDataSource") DataSource fundDataSource
                                                        ) {
        return new JpaTransactionManager(fundEntityManagerFactory(builder,fundDataSource).getObject());
    }

    /*
     * @Bean public DataSourceTransactionManager fundTransactionManager() {
     * return new DataSourceTransactionManager(fundDataSource()); }
     */

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
