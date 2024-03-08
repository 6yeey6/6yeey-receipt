package com.ibg.receipt.vo.api.order;

import com.ibg.receipt.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 贷款单查询
 * @author zhangjilong
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLoanRequestVo {

    private String partnerUserId;

    private String idCard;

    private Long startDate;

    private Long endDate;

    public void checkParams() throws Exception {
        if (StringUtils.isBlank(partnerUserId) && StringUtils.isBlank(idCard)){
            throw new Exception("用户id和partnerUserId必传一个!");
        }
        //Assert.notBlank(partnerUserId, "用户id");
    }

}
