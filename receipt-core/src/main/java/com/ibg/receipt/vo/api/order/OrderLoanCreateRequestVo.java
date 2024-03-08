package com.ibg.receipt.vo.api.order;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibg.receipt.base.exception.Assert;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.enums.business.PriorityLevelEnum;
import com.ibg.receipt.enums.business.UserSource;
import com.ibg.receipt.util.EncryptUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 贷款单创建
 * @author zhangjilong
 */
@Data
public class OrderLoanCreateRequestVo {

    private List<LoanList> loanNos;

    private String partnerUserId;

    private String userName;

    private String createUser;

    private String email;

    private String source;

    private Integer priorityLevel;

    @Data
    public static class LoanList {
        /**
         * 序号
         */
        @JsonProperty("loanNo")
        private String loanNo;
        /**
         * 合同号
         */
        @JsonProperty("partnerUserId")
        private String partnerUserId;

    }

    public void checkParams(String source) {
        Assert.notEmpty(loanNos, "进件号列表");
        //Assert.notBlank(partnerUserId, "用户标识");
        Assert.notBlank(userName, "用户姓名");
        Assert.notBlank(email, "邮箱");
        Assert.notNull(priorityLevel, "紧急程度");

        if (PriorityLevelEnum.getPriorityLevel(this.priorityLevel) == null) {
            throw ServiceException.exception(CodeConstants.C_10101001, "紧急程度枚举转换异常");
        }
        UserSource userSource = Assert.enumNotValid(UserSource.class, source, "用户来源");
        String tegex = "[a-zA-Z0-9_]+@\\w+(\\.com|\\.cn){1}";
        //业务系统走下面的解析
        if (userSource != UserSource.BUS_SYSTEM) {
            //校验邮箱格式
            boolean flag = email.matches(tegex);
            if (!flag) {
                throw ServiceException.exception(CodeConstants.C_10101016, "邮箱格式有误，请输入正确格式!");
            }
            //业务系统解析邮箱
        }else{
            String emailStr = EncryptUtil.getDecoded(email);
            boolean flag = emailStr.matches(tegex);
            if (!flag) {
                throw ServiceException.exception(CodeConstants.C_10101016, "邮箱格式有误，请输入正确格式!");
            }
        }

    }

}
