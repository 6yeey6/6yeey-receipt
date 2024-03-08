package com.ibg.receipt.db;

public class DataSourceHolder {

    private static final ThreadLocal<DataSourceTypeEnum> dataSourceHolder = new ThreadLocal<DataSourceTypeEnum>();

    public static DataSourceTypeEnum getDataSourceType() {
        DataSourceTypeEnum dataSourceType = (DataSourceTypeEnum) dataSourceHolder.get();
        return dataSourceType;
    }

    public static void setDataSourceType(final DataSourceTypeEnum dataSourceType) {
        dataSourceHolder.set(dataSourceType);
    }

    public static void clearDataSourceType() {
        dataSourceHolder.remove();
    }
}
