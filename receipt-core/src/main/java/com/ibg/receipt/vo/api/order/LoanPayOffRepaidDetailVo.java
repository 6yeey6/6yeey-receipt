package com.ibg.receipt.vo.api.order;

import java.math.BigDecimal;
import java.util.Date;

import com.ibg.receipt.vo.api.fee.FeeVo;
import lombok.Data;

@Data
public class LoanPayOffRepaidDetailVo {

    private String loanKey;

    private String funderName;

    private String userName;

    private String funderCode;

    private BigDecimal loanAmount;

    private String loanStatus;

    private Date startDate;

    private FeeVo repaidFee;

    private String partnerLoanNo;

    private Date payOffDate;


}
