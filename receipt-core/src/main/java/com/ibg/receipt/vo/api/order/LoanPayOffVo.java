package com.ibg.receipt.vo.api.order;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class LoanPayOffVo {

    private String loanKey;

    private String funderName;

    private BigDecimal loanAmount;

    private String loanStatus;

    private Date startDate;

    private String userName;

    private String partnerLoanNo;

    private Date payOffDate;

    private String partnerUserId;

    private Integer periods;
}
