package com.ibg.receipt.service.receipt.excel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class ReceiptOrderImportVo {

    private String uid;

    private String userName;

    private String email;

    private String mobile;

    private String userAddress;

    private String partnerLoanNo;

    private BigDecimal loanAmount;

    private String funderCode;

    private String loanPeriod;

    private Date startDate;

    private Date payOffDate;

    private String inRepayStartPeriod;

    private Date receiptDate;

    private String receiptStatus;

    private Date finishedDate;

    private BigDecimal receiptAmount;

    private String receiptChannel;

    private String operator;

    private String paySubject;

    private Map<String, BigDecimal> feeDetail;

}
