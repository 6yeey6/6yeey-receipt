package com.ibg.receipt.vo.api.nuonuo.constants;

public class NuoNuoConstants {

    /**
     * 诺诺获取accessToken 30天
     */
    public static final String FUND_NUONUO_ACCESSTOKEN = "fund:nuonuo:accessToken:";

    /**
     * 开票申请方法名
     */
    public static final String RECEIPT_APPLY_METHOD = "nuonuo.ElectronInvoice.requestBillingNew";

    /**
     * 开票结果查询方法名
     */
    public static final String RECEIPT_QUERY_METHOD  = "nuonuo.ElectronInvoice.queryInvoiceResult";

    /**
     * 开票重试接口方法名
     */
    public static final String RECEIPT_RETRY_METHOD  = "nuonuo.ElectronInvoice.reInvoice";

    /**
     * 响应成功code
     */
    public static final String CODE_SUCCESS = "E0000";


    /**
     * 诺税通saas请求开具发票接口
     */
    public static final String NEW_RECEIPT_APPLY_METHOD = "nuonuo.OpeMplatform.requestBillingNew";


    /**
     * 诺税通saas发票详情查询接口
     */
    public static final String NEW_RECEIPT_QUERY_METHOD = "nuonuo.OpeMplatform.queryInvoiceResult";
}
