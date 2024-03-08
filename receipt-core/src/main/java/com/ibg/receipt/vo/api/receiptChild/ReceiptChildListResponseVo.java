package com.ibg.receipt.vo.api.receiptChild;

import com.ibg.receipt.enums.business.OrganizationEnum;
import com.ibg.receipt.vo.api.base.BasePageVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 子单列表查询响应vo
 *
 * @author zhou
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptChildListResponseVo extends BasePageVo {
    private Long id;
    private String loanId;
    private String uid;
    private String receiptOrderKey;
    private String receiptChildOrderKey;
    private Byte status;
    private String creditor;
    private String operatorName;

    private String account;

    private String userName;

    private String userPid;

    private String pidValid;

    private String address;

    private String bankCard;

    private String bankName;

    private String funderLoanKey;

    private String trustName;

    private String fundStatus;

    private String fundCode;

    private String fundName;

    private BigDecimal loanAmount;

    private Integer period;

    private Integer inRepayPeriod;

    private Date loanTime;

    private Date payoffTime;

    private Integer hisOverdueday;

    private BigDecimal repayAmount;

    private String repayStatus;

    private String receiptItemCode;

    /**
     * 资金项映射名
     */
    private String receiptItemName;

    private String creditorName;

    private BigDecimal receiptAmount;




    private String needRepayDetail;

    private String loanContractPath;

    private String insureLetterPath;

    private String guaranteeServiceContractPath;

    private String gatherAuthLetter;

    private String idCardFrontPath;

    private String idCardBackPath;

    private String funderLoanKeyCyOld;

    private String receiptUrl;

    private Date updateTime;

    private String email;

    private String organization;

    private Integer priorityLevel;

    private Byte sendStatus;

    private Date sendTime;

    private Date finishTime;

    private String partnerUserId;

    private Boolean canDownload;


    /**
     * 增加的额外费用项
     */
    private BigDecimal repayInterest;

    private BigDecimal repayFunderOverdueInterest;

    private BigDecimal repayMgmtFee;

    private BigDecimal repayOverdueInterest;

    private BigDecimal repayOverdueMgmtFee;

    private BigDecimal repayInRepayFee;

    private BigDecimal repayGuaranteeFee;

    private BigDecimal repayCommutation;

    private String itemExtInfo;

}
