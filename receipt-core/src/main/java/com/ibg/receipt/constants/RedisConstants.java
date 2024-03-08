package com.ibg.receipt.constants;

public class RedisConstants {
    /** 绑卡发短信锁 */
    public static final String BIND_SEND_SMS_LOCK = "fund:card:bind:sendSms:{0}:{1}";

    /** 绑卡发短信频率限制 60秒 */
    public static final int BIND_SEND_SMS_FREQUENCY_LIMIT = 130;
    /** 放款锁 */
    public static final String LOAN_PAYMENT_LOCK = "fund:loan:payment:{0}";
    /** 放款频率限制 2分钟 */
    public static final int LOAN_PAYMENT_FREQUENCY_LIMIT = 130;

    /** 对账文件锁 */
    public static final String BILLING_CHECK_LOCK = "fund:billing:check:";

    /** 绑卡发短信验证时间 600秒 */
    public static final int BIND_VALIDATE_FREQUENCY_LIMIT = 600;

    /** 绑卡验证分布式锁key */
    public static final String BIND_VALIDATE_DISTRIBUTE_LOCK = "fund:card:bind:distribute:lock:{0}:{1}";

    /** 绑卡验证失效验证key */
    public static final String BIND_VALIDATE_LOCK = "fund:card:bind:validate:";

    /** 退保并发key */
    public static final String FUND_SURRENDER = "fund:surrender:{0}:{1}";

    /** 对账文件下载锁 */
    public static final String BILLING_CHECK_DOWNLOAD_LOCK = "fund:billing:download:";

    /** 对账文件解析锁 */
    public static final String BILLING_CHECK_ANALYSIS_LOCK = "fund:billing:analysis:";

    /** 对账文件报表 */
    public static final String BILLING_CHECK_REPORT_KEY = "fund:billing:report:";

    /** 保费 */
    public static final String LOAN_GUARANTEE_FEE = "fund:loan:guaranteeFee:{0}";

    /** 划扣保费失败 */
    public static final String LOAN_AUDIT_SURREND = "fund:loan:audit:surrend:";

    /** 云信理赔 */
    public static final String YN_TRUST_CLAIMS_KEY = "fund:yntrust:claims:key:{0}";

    /** 云信理赔时间 */
    public static final String YN_TRUST_CLAIMS_DEALING = "fund:yntrust:claims:dealing:{0}";

    /** 渤海信托理赔 */
    public static final String BH_TRUST_CLAIMS_KEY = "fund:bhtrust:claims:key:{0}";

    /** 渤海信托理赔时间 */
    public static final String BH_TRUST_CLAIMS_DEALING = "fund:bhtrust:claims:dealing:{0}";

    /** 石嘴山-集成理赔 */
    public static final String SZS_BANK_CLAIMS_KEY = "fund:szsbank:claims:key:{0}";

    /** 山东信徒理赔 */
    public static final String SD_TRUST_CLAIMS_KEY = "fund:sdtrust:claims:key:{0}";

    /** 山东信托理赔时间 */
    public static final String SD_TRUST_CLAIMS_DEALING = "fund:sdtrust:claims:dealing:{0}";

    /** 农行-人保文件处理 */
    public static final String PICC_ABC_DOWNLOAD_FILE_KEY = "fund:file:picc:abc:{0}:{1}";

    /** 逾期罚息同步完成标识 */
    public static final String OVERDUE_INTEREST_SYNC_DONE = "fund:loan:overdue_sync_done";

    /** FUND_ROUTER_STATISTIC 的hash key */
    public static final String FUND_ROUTER_STATISTIC_KEY = "{0}:{1}";

    public static final String FUND_ROUTE_CACHE_INITIALIZER_LOCK = "fund:router:cache:initializer";

    /** 山东信托lpr */
    public static final String FUNDER_SD_TRUST_LPR = "fund:loan:sdTrustLpr";
    /** 金美信lpr */
    public static final String FUNDER_JMX_LPR = "fund:loan:jmxLpr";

    /** 渤海信托补签协议上传Key，用于阻塞借款合同上传job，防止冲突 */
    public static final String BH_TRUST_DOWNLOAD_KEY = "fund:bhtrust:fileUploadFlag:{0}:{1}";

    /** 渤海信托补签协议上传Key，用于阻塞借款合同上传job，防止冲突 */
    public static final String LH_BANK_DOWNLOAD_KEY = "fund:lhbank:fileUploadFlag:{0}:{1}";

    /** 以借款为维度增加还款限制 */
    public static final String REPAY_LIMIT_LOAN_RANGE = "repay:limit:loan:";

    /** 以借款为维度单次还款限制6小时 */
    public static final int REPAY_LIMIT_LOAN_RANGE_TIME = 6;

    /** 渤海信托可贷余额 */
    public static final String FUNDER_BHTRUST_ABLE_BALANCE = "fund:bhTrust:ableBalance";
    /** 蓝海银行（渤海信托）可贷余额 */
    public static final String FUNDER_BHXT_LHBANK_ABLE_BALANCE = "fund:lhBank:ableBalance";
    /** 山东信托可贷余额 */
    public static final String FUNDER_SDTRUST_ABLE_BALANCE = "fund:sdTrust:ableBalance";
    /** 西藏信托可贷余额 */
    public static final String FUNDER_XZTRUST_ABLE_BALANCE = "fund:xzTrust:ableBalance";
    /** 龙江银行贷后请求文件处理 */
    public static final String FUNDER_LJBANK_AFTER_LOAN_APPLY_FILE = "fund:ljbank:file:apply:{0}";
    /** 龙江银行贷后结束文件处理 */
    public static final String FUNDER_LJBANK_AFTER_LOAN_FINISH_FILE = "fund:ljbank:file:finish:{0}";
    /** 蒙商消金lpr */
    public static final String FUNDER_BYCF_LPR = "fund:loan:bycfLpr";
    /** 易一代(平顶山)lpr */
    public static final String FUNDER_PDSB_LPR = "fund:loan:pdsbLpr";
    /** 爱建信托lpr */
    public static final String FUNDER_AJ_TRUST_LPR = "fund:loan:ajtrustLpr";
    /** 爱建信托计划余额 */
    public static final String AJ_TRUST_BALANCE = "fund:ajtrust:balance";
    /** 国民信托计划余额 */
    public static final String GM_TRUST_BALANCE = "fund:gmtrust:balance";
    /** 全互-国通信托计划余额 */
    public static final String QH_GT_TRUST_BALANCE = "fund:qhgttrust:balance";
    /** 外贸信托计划余额 */
    public static final String FOTIC_TRUST_BALANCE = "fund:fotictrust:balance";
    /** 外贸罚息通知资方 */
    public static final String FOTIC_TRUST_OVERDUE_INTEREST_TOFUND  = "fund:fotictrust:oitf:{0}";
    /** 南京银行lpr */
    public static final String FUNDER_NJ_BANK_LPR = "fund:loan:njbankLpr";

    /** 百信银行lpr */
    public static final String FUNDER_BX_BANK_LPR = "fund:loan:bxbankLpr";

    /** 国民信托日终文件_还款计划生成标记 */
    public static final String FUNDER_GMTRUST_REPAY_PLAN_LIST = "gmtrust:repayplanlist:{0}";
    /** 国民信托日终文件_还款信息生成标记 */
    public static final String FUNDER_GMTRUST_REPAY_INFO_LIST = "gmtrust:repayinfolist:{0}";
    /** 国民信托日终文件_放款信息生成标记 */
    public static final String FUNDER_GMTRUST_LOAN_INFO_LIST = "gmtrust:loaninfolist:{0}";

    /** 长银消金lpr */
    public static final String FUNDER_CYCF_LPR = "fund:loan:cycfLpr";
    /** 长银消金授信异常计数器 */
    public static final String FUNDER_CYCF_CREDIT_APPLY_ERROR_TIMES = "fund:cycf:credit:apply:{0}:{1}";
    /** 长银消金授信查询异常计数器 */
    public static final String FUNDER_CYCF_CREDIT_APPLY_QUERY_ERROR_TIMES = "fund:cycf:credit:apply:query:{0}:{1}";
    /** 长银消金还款信息流同步异常计数器 */
    public static final String FUNDER_CYCF_REPAY_APPLY_ERROR_TIMES = "fund:cycf:repay:apply:{0}:{1}";
    /**
     * 龙江提前结清还款试算结果缓存
     * {0}:日期（yyyyMMdd）
     * {1}:loanKey
     */
    public static final String FUNDER_LJBANK_REPAY_TRY_CAL = "fund:ljbank:repay:trycal:{0}:{1}";
    /** 龙江逾期loan缓存
     *  */
    public static final String FUNDER_LJBANK_OVERDUE_LOANS_KEY = "fund:ljbank:overdue:loans:{0}";

    /** 以进件编号为维度增加放款限制 */
    public static final String LOAN_LIMIT_APPLICATION_RANGE = "loan:limit:application:";

    /** 文件上传锁 */
    public static final String FUND_APPLICATIONNO_FILETYPE_LOCK = "fund:applicationno:filetype:{0}:{1}";

    /**
     * 长银消金代偿回购回盘文件下载锁
     */
    public static final String FUNDER_CYCF_AFTER_LOAN_APPLY_FILE = "fund:cycf:file:apply:{0}";

    /** 长银消金代偿 */
    public static final String CYCF_ADVANCE_REPAYPLANKEY_KEY = "fund:cycf:advance:key:{0}";

    /** 长银消金直连代偿 */
    public static final String CYCF_ZL_ADVANCE_REPAYPLANKEY_KEY = "fund:cycfzl:advance:key:{0}";

    /** 长银消金回购 */
    public static final String CYCF_REPURCHASE_REPAYMENT_KEY = "fund:cycf:repurchase:key:{0}";
    /** 长银消金直连回购 */
    public static final String CYCF_ZL_REPURCHASE_REPAYMENT_KEY = "fund:cycfzl:repurchase:key:{0}";

    /** 龙江审核报头寸不足 */
    public static final String FUNDER_LJBANK_AUDIT_NOTICE_TIMESTAMP = "fund:ljbank:audit:notice:timestamp:{0}";

    /** 甘肃银行代偿 */
    public static final String GSBANK_ADVANCE_REPAYPLANKEY_KEY = "fund:gsbank:advance:key:{0}";
    /** 甘肃银行回购 */
    public static final String GSBANK_REPURCHASE_LOANKEY_KEY = "fund:gsbank:repurchase:key:{0}";
    /** 甘肃银行贷后结束文件处理 */
    public static final String FUND_GSBANK_AFTER_LOAN_FILE = "fund:gsbank:after:loan:{0}";

    /** 长银消金批量同步担保信息key */
    public static final String CYCF_BATCH_SYNC_GUARANTEE_INFO_KEY = "fund:cycf:sync:guarantee:key:{0}:{1}";

    /** 盛银消金lpr */
    public static final String FUNDER_SYCF_BANK_LPR = "fund:loan:sycfLpr";
    /** 天山银行lpr */
    public static final String FUNDER_TS_BANK_LPR = "fund:loan:tsbankLpr";

    /** 天山银行放款查询key */
    public static final String TS_BANK_LOAN_QUERY_KEY = "fund:tsbank:loan:query:{0}";

    /** 亿联银行银行放款查询key */
    public static final String YL_BANK_LOAN_QUERY_KEY = "fund:ylbank:loan:query:{0}";

    /** 亿联银行批扣文件获取key */
        public static final String YL_BANK_AUDUCT_FILE_KEY = "fund:ylbank:auduct:file:key:{0}";

    /** 亿联银行银行支用查询key */
    public static final String YL_BANK_PAY_QUERY_KEY = "fund:ylbank:pay:query:{0}";

    /** 亿联银行银行进件查询key */
    public static final String YL_BANK_APPL_QUERY_KEY = "fund:ylbank:appl:query:{0}";

    /** 亿联银行银行还款查询key */
    public static final String YL_BANK_REPAY_QUERY_KEY = "fund:ylbank:repay:query:{0}";

    /** 众安保险-大秦丝路进件审核结果查询*/
    public static final String ZABX_SP_AUDIT_QUERY_KEY = "fund:zabx_sp:audit:query:{0}";

    /** 众安保险-大秦丝路放款查询key */
    public static final String ZABX_SP_LOAN_QUERY_KEY = "fund:zabx_sp:loan:query:{0}";


    /** 众安保险-大秦丝路放款查询key */
    public static final String ZABX_SP_REPAY_PLAN_QUERY_KEY = "fund:zabx_sp:repayplan:query:{0}";

    /** 天山银行进件审核查询key */
    public static final String TS_BANK_APPLICATION_QUERY_KEY = "fund:tsbank:application:query:{0}";
    /** 爱建审核查询限制key */
    public static final String AJ_TRUST_AUDIT_QUERY_LIMIT_KEY = "fund:ajtrust:audit:query:{0}";
    /** 爱建信托放款查询key */
    public static final String AJ_TRUST_LOAN_QUERY_KEY = "fund:ajtrust:loan:query:{0}";
    /** 爱建信托还款查询key */
    public static final String AJ_TRUST_REPAY_QUERY_KEY = "fund:ajtrust:repay:query:{0}";

    /** redis缓存标签的key格式 */
    public static final String REDIS_CACHE_ASPECT_KEY = "aspect:{0}:{1}";
    /** 石嘴山-资金预约查询 */
    public static final String SZS_BANK_FUND_QUERY_KEY = "fund:szsbank:fund:query:{0}";
    /** 苏宁消金提前结清还款试算结果缓存 */
    public static final String SNCF_REPAY_TRY_CAL_KEY = "fund:sncf:repay:trycal:{0}:{1}";
    /** 苏宁消金查询锁 */
    public static final String SNCF_QUERY_KEY = "fund:sncf:query:{0}:{1}";
    /** 振兴银行查询锁 */
    public static final String ZXBANK_QUERY_KEY = "fund:zxbank:query:{0}:{1}";

    /** 众安保险批扣请求限制锁 */
    public static final String ZABXSP_AUTO_DEDUCT_NOTIFY_KEY = "fund:zabxsp:autodeduct:notify:{0}:{1}";
     /** 新网批扣通知请求限制锁 */
    public static final String XWBANKNEW_AUTO_DEDUCT_NOTIFY_KEY = "fund:xwbanknew:autodeduct:notify:{0}:{1}";
     /** 众安保险批扣限制锁-批扣入账顺序 */
    public static final String ZABXSP_AUTO_DEDUCT_ACCOUNT_LIMIT_KEY = "fund:zabxsp:autodeduct:account:{0}:{1}";

    /** 齐商银行进件审核查询key */
    public static final String QS_BANK_APPLICATION_QUERY_KEY = "fund:qsbank:application:query:{0}";

    /** 齐商lpr */
    public static final String FUNDER_QSBANK_LPR = "fund:loan:qsbankLpr";
    /** 百信批扣通知请求限制锁 */
    public static final String BXBANKSP_AUTO_DEDUCT_NOTIFY_KEY = "fund:bxbanksp:autodeduct:notify:{0}:{1}";
    /** 百信批扣限制锁-批扣入账顺序 */
    public static final String BXBANKSP_AUTO_DEDUCT_ACCOUNT_LIMIT_KEY = "fund:bxbanksp:autodeduct:account:{0}:{1}:{2}";

    /**民生易贷 资方划扣入账锁 */
    public static final String MSYDSP_AUTO_DEDUCT_NOTIFY_KEY ="fund:msydsp:autodeduct:notify:{0}:{1}";

    /** 百信旧用户流程->新用户流程 */
    public static final String BXBANKSP_OLD_TO_NEW_USER_KEY = "fund:bxbanksp:audit:old:new:{0}";

    /**
     * 外贸放款文件上传编号
     */
    public static final String FUNDER_FOTIC_LOANFILE = "fund:fotic:loanFile:{0}";
    /** 众安保险适配level */
    public static final String ZABXSP_ADJUST_LEVEL_KEY = "fund:zabxsp:adjust:level:{0}";

    public static final String ZXBANK_LIQUID_KEY = "zxbank.need.liquidation.list";
    public static final String ZXBANK_XW_LIQUID_KEY = "zxbankxw.need.liquidation.list";

    public static final String ZXBANK_LIQUID_FLAG_KEY = "zxbank.liquidation.flag:{0}";
    public static final String ZXBANK_XW_LIQUID_FLAG_KEY = "zxbankxw.liquidation.flag:{0}";

    /** 振兴银行提前结清利息 */
    public static final String ZXBANK_INREPAY_INTEREST_KEY = "fund:zxbank:inrepay:{0}";

    public static final String LHZLBANK_USER_CHANNEL_KEY = "lhzl.userchannel.key:{1}";
    /** 蓝海直连支用查征企微通知锁*/
    public static final String LHZL_BANK_GRANT_PROCESSING_LOAN_KEY = "lhzl.grant.processing.loan.key:{0}";

    /**蓝海直连-放款回调锁 */
    public static final String LHZL_LOAN_NOTIFY_KEY ="fund:lhzl:loan:notify:{0}:{1}";

    /** 渠道清算 */
    public static final String FUNDER_DATE_LIQUIDATION = "fund.liquidation:{0}:{1}";

    /**
     * 结清文件申请锁
     */
    public static final String SETTLE_FILE_APPLY_KEY = "fund.file:settle:{0}";
    /**
     * 放款凭证文件申请锁
     */
    public static final String LOAN_PROVEMENT_FILE_APPLY_KEY = "fund.file:loanprovement:{0}";
    /**
     * 蓝海直连回购对账batchNo
     */
    public static final String LHZL_BANK_REPURCHASE_BILLING_KEY = "fund.lhzlbank.repurchase.key:{0}";
    /** 长银直连通知请求并发锁 */
    public static final String CYCLZL_NOTIFY_KEY = "fund:cyclzl:notify:{0}:{1}";

    /**
     * 东营文件通知限流key
     */
    public static final String DY_BANK_FILE_NOTICE_LIMIT = "fund.dybank.file.notice.limit";
    public static final String LZ_BANK_FILE_NOTICE_LIMIT = "fund.lzbank.file.notice.limit";

    /**
     * 中原消金审核文件上传key
     */
    public static final String ZYCF_AUDIT_FILE_UPLOAD_KEY = "fund:zyzf:audit:upload:{0}";

    /** 中原消金lpr */
    public static final String FUNDER_ZY_CF_LPR = "fund:loan:zycfLpr";

    public static final String XYCF_REPAY_COUNT = "fund.xycf.repay.count:{0}:{1}";

    /**
     * 苏宁众邦查询限流key
     */
    public static final String SN_ZB_AUDIT_QUERY_LIMIT = "fund:snzb:audit:query:{0}";
    public static final String SN_ZB_LOAN_QUERY_LIMIT = "fund:snzb:loan:query:{0}";
    public static final String SN_ZB_REPAY_PLAN_QUERY_LIMIT = "fund:snzb:repayplan:query:{0}";
    public static final String SN_ZB_REPAY_QUERY_LIMIT = "fund:snzb:repay:query:{0}";


    public static final String JMX_DEBT_REPAY_NOTICE_LIMIT = "fund:jxm:debt:repay:notice:{0}";


    /** 海尔消金lpr */
    public static final String FUNDER_HR_CF_LPR = "fund:loan:hrcfLpr";
    public static final String HR_CF_REPAY_QUERY_LIMIT = "fund:hrcf:repay:query:{0}";

    /**
     * 苏湘银行
     */
    public static final String FUNDER_SX_BANK_LPR = "fund:loan:sxBankLpr";

    /** 亿联银行代偿回购文件key */
    public static final String YLBANK_ADVANCE_REPURCHASE_FILE_KEY = "fund:ylbank:loan:after:{0}";

}
