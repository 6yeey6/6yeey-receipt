package com.ibg.receipt.service.receipt.excel;

import java.util.Arrays;
import java.util.List;

public class ReceiptOrderImportExcel {

    public static final String[] EXCEL_CONFIG = new String[] { "uid", "userName", "email", "mobile", "userAddress",
            "partnerLoanNo", "loanAmount", "funderCode", "loanPeriod", "startDate", "payOffDate", "inRepayStartPeriod", "receiptDate", "receiptStatus", "finishedDate", "receiptAmount",
            "receiptChannel", "operator", "paySubject", "repayInterest", "repayMgmtFee", "repayOverdueInterest",
            "repayOverdueMgmtFee", "repayFunderOverdueInterest", "repayGuaranteeFee", "repayCommutation",
            "repayInRepayFee", "repayOverdueGuaranteeFee", "repayGuaranteeDeposit" };

    /** 时间类型 */
    public final static List<String> DATE_FORMAT_LIST = Arrays.asList("receiptDate", "finishedDate", "startDate",
            "payOffDate");

    /** 时间类型不带时分秒 */
    public final static List<String> DATE_FORMAT_LIST_NO_MUNITE = Arrays.asList("receiptDate", "finishedDate");

    /** 金额类型 */
    public final static List<String> BIGDECIMAL_FORMAT_LIST = Arrays.asList("receiptAmount", "repayInterest",
            "repayMgmtFee", "repayOverdueInterest", "repayOverdueMgmtFee", "repayFunderOverdueInterest",
            "repayGuaranteeFee", "repayCommutation", "repayInRepayFee", "repayOverdueGuaranteeFee",
            "repayGuaranteeDeposit");

    /** 非资金项金额类型 */
    public final static List<String> BIGDECIMAL_FORMAT_EXCLUDE_CAPITAL_LIST = Arrays.asList("receiptAmount", "loanAmount");

    /** 可以为空 */
    public final static List<String> ALLOW_NULL_LIST = Arrays.asList("inRepayStartPeriod", "finishedDate");

    public final static List<String> ALLOW_NULL_DATE_LIST = Arrays.asList("payOffDate", "finishedDate");
}
