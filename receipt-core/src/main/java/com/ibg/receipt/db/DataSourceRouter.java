package com.ibg.receipt.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSourceRouter extends AbstractRoutingDataSource {

    private final Logger logger = LoggerFactory.getLogger(DataSourceRouter.class);

    @Override
    protected Object determineCurrentLookupKey() {
        try {
            return com.ibg.receipt.db.DataSourceHolder.getDataSourceType();
        } catch (Exception e) {
            logger.error("get dataSource type from router error", e);
            return DataSourceTypeEnum.MASTER;
        }
    }

}
