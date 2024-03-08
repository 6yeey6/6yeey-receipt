package com.ibg.receipt.enums.job;

public enum JobMachineStatus {
    APPL_AUDIT("applAudit", "进件审核", "applAuditHandler"),
    QUERY_LOAN_IDS("receiptQueryLoanIds", "查询本地待开票订单任务", "receiptQueryLoanIdsHandler"),
    PULL_DATA_WAREHOUSE("receiptPullDataWareHouse", "查询数仓数据", "receiptPullDataWareHouseHandler"),
    SPILT_DATA_WAREHOUSE("receiptSplitDataWareHouse", "按订单纬度拆分数仓数据", "receiptSplitDataWareHouseHandler"),
    INIT_CHILD_ORDER("receiptInitChildOrder", "生成子单任务", "receiptInitChildOrderHandler"),

    /**
     * 上传URL成功关完
     */
    CHILD_ORDER_SUCCESS("receiptChildOrderSuccess","子单成功更新状态","receiptChildOrderSuccessHandler"),
    UPDATE_RECEIPT_ORDER("updateReceiptOrder","主单状态进度更新","updateReceiptOrderHandler"),

    /**
     * 定时开票job
     */
    WAIT_RECEIPT_QUERY("waitReceiptQuery", "待开票订单查询", "waitReceiptQueryHandler"),
    NUONUO_RECEIPT_APPLY("nuonuoReceiptApply", "诺诺开票申请", "nuonuoReceiptApplyHandler"),
    NUONUO_RECEIPT_QUERY("nuonuoReceiptQuery", "诺诺开票结果查询", "nuonuoReceiptQueryHandler"),

    /**
     * 上传完成任务-通知上传结果，发送上传明细文件
     */
    UPLOAD_FINISH("receiptUploadFinish","发票上传完成任务","receiptUploadFinishHandler"),
    /**
     * 发送邮件-内部-文件连接
     */
    SEND_INNER_EMAIL_URL("sendInnerEmailUrl","发送邮件-内部-文件连接","sendInnerEmailUrlHandler"),
    SEND_RECEIPT_EMAIL("sendReceiptEmail","发送邮件-发票附件","sendReceiptEmailHandler"),
    SEND_PASSWORD_EMAIL("sendPassword","发送邮件-文件密码","sendPasswordHandler"),

    /**
     * 推送诺诺全电开票的查询节点的子单
     */
    NUONUO_QUERY_RETRY("nuonuoQueryRetry","发送邮件-文件密码","nuonuoQueryRetryHandler"),
    ;

    private String status;
    private String desc;
    private String handler;

    JobMachineStatus(String status, String desc, String handler) {
        this.status = status;
        this.desc = desc;
        this.handler = handler;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHandler() {
        return handler;
    }

    public static JobMachineStatus getEnum(String val) {
        for (JobMachineStatus status : JobMachineStatus.values()) {
            if (status.getStatus().equals(val)) {
                return status;
            }
        }
        return null;
    }

    public static String getMessage(String val) {
        for (JobMachineStatus status : JobMachineStatus.values()) {
            if (status.getStatus().equals(val)) {
                return status.getDesc();
            }
        }
        return null;
    }
}
