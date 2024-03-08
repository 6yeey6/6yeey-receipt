package com.ibg.receipt.base.constant;

/**
 * @author yushijun
 * @date 2018/9/3
 */
public class Constants {

    public static final String SENTINEL_EXCEPTION_MSG = "为您匹配的放款机构出现系统服务异常，请稍后再试";

    public static final String JOB_PARAM_USER_KEY = "userKey";
    // 放款卡
    public static final String JOB_PARAM_GRAND_BANKCARD = "bankCard";
    // 文件id
    public static final String JOB_PARAM_FILE_ID = "fileId";
    // 资金方渠道号
    public static final String JOB_PARAM_FUNDER_CODE = "funderCode";
    // 担保方渠道号
    public static final String JOB_PARAM_GUARANTOR_CODE = "guarantorCode";
    //
    public static final String POLICY_UUID ="policyUUID";
    // 众安sftp 文件base64 key
    public static final String QUERY_RESULT_CONTENT = "content";
    // 同步众安借款时间
    public static final String LOAN_INSURE_LETTER_SYNC_DATE = "loanInsureLetterSyncTime";

    // 资金方还款状态
    public static final String JOB_PARAM_FUNDER_REPAY_STATUS = "funderRepayStatus";

    public static final String JOB_PARAM_REPAY_TIME = "repay_time";

    public static final String INSURE_LETTER_NO = "insureLetterNo";
    public static final String INSURE_LETTER_NO_OTHER = "insureLetterNoOther";


    public static final String BILLING_PATH = "billing";

    public static int SPLIT_SIZE = 1000;
    public static int FILE_SPLIT_SIZE = 5000;
    /** 担保方异步通知表ID */
    public static final String JOB_PARAM_GUARANTOR_NOTICE_ID = "guarantorNoticeId";

    /** 交互ID 系统做幂等校验 */
    public static final String JOB_PARAM_UUID = "uuid";
    /** 发送报文时间 */
    public static final String JOB_PARAM_TRANS_TIME = "transTime";
    /** 推送日期的字符串 yyyymmdd */
    public static final String JOB_PARAM_BATH_NUMBER = "bathNumber";

    /** 文件类型 */
    public static final String JOB_PARAM_FILE_TYPE = "fileType";
    /** 文件名称 */
    public static final String JOB_PARAM_FILE_PATH = "filePath";
    /** 文件大小 */
    public static final String JOB_PARAM_ZIP_SIZE = "zipSize";
    /** 文件记录行数 */
    public static final String JOB_PARAM_ROW_COUNT = "rowCount";
    /** 文件描述 */
    public static final String JOB_PARAM_DESCRPTION = "descrption";
    /** 资金平台系统中文件fundFileId */
    public static final String JOB_PARAM_FS_FILE_ID = "fsFileId";
    /** 人保sftp文件解析处理的批次号 */
    public static final String JOB_PARAM_BATCH_NO = "batchNo";
    /** 三方订单号 */
    public static final String THIRD_ORDER_ID = "thirdOrderId";

    /** 批次号 */
    public static final String BATCH_NO = "batchNo";
    /** 分隔符 */
    public static final String COMMON_SEPARATOR = "|";
    /** 还款方式-回购 */
    public static final String REPAYMENT_METHOD_REPURCHASE = "repurchase";

    public static final String SURRENDER_POLICY_NO = "surrenderPolicyNo";
    public static final String SURRENDER_REQUEST_UUID = "surrenderRequestUuid";

    /** 还款交易类型 */
    public static final String JOB_PARAM_REPAY_TRANS_TYPE = "repayTransType";

    /** 产品编号 */
    public static final String PRODUCT_CODE = "productCode";
    public static final String PARTNER_ID = "partnerId";

    /** 石嘴山人保SFTP文件根目录 */
    // picc/szs/upload/yyyymmdd
    public static final String ZSZ_BANK_PICC_FILE_ROOT_PATH = "/szs/upload/";

    /** 光大信托签章贷款合同SFTP文件根目录 */
    // /upload/contract/loan/${yyyyMMdd}/
    public static final String EBTRUST_SIGNED_FILE_ROOT_PATH = "/upload/contract/loan/%s/";

    /** 光大信托SFTP文件根目录 */
    public static final String EB_TRUST_FILE_ROOT_PATH = "/download/";
    public static final String EB_TRUST_SIGNATURE_FILE_ROOT_PATH = "/upload/";

    /** 壹账通人保SFTP文件根目录 */
    // picc/yzt/upload/yyyymmdd
    public static final String YZT_PICC_FILE_ROOT_PATH = "/yzt/upload/";

    /** 银行流水核对方式
     * ONLY_BY_FROM_CARD_ID 按照我方银行卡号匹配
     * ONLY_BY_TO_CARD_ID 按照对方银行卡号匹配
     * BY_BOTH_CARD_ID 按照双方银行卡号匹配
     * */

    public static final String ONLY_BY_FROM_CARD_ID = "onlyByFromCardId";

    public static final String ONLY_BY_TO_CARD_ID = "onlyByToCardId";

    public static final String BY_BOTH_CARD_ID = "byBothCardId";

    /** 对账报表差异类型 */
    public static final String BILL_DIFFERENCE_TYPE = "billDifferenceType";
    /** 对账报表差异时间 */
    public static final String BILL_DIFFERENCE_TIME = "billDifferenceTime";

    public static final String FUNDER_LPR_DATE = "lprDate";

    public static final String FUNDER_LPR_RATE = "lprRate";

    public static final String FUNDER_LPR_FLOAT_RATE = "lprFloatRate";

    public static final String FUNDER_LPR_RATE_CEILING = "lprRateCeiling";

    public static final String RETRY = "retry";

    public static final String DEMO_LPR_KEY = "fund:loan:demoLpr";


}
