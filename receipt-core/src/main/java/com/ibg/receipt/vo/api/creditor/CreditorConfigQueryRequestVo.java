package com.ibg.receipt.vo.api.creditor;

import cn.hutool.core.util.ObjectUtil;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.enums.business.ReceiptItemCodeBase;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.base.BasePageVo;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/24 20:04
 */
@Data
public class CreditorConfigQueryRequestVo extends BasePageVo {

    private List<String> creditors;

    private List<CreditorEnum> creditorEnums;

    public void check() {

        if (creditors != null && creditors.size() > 0) {
            //creditorEnum = Assert.enumNotValid(CreditorEnum.class, creditor, "主体");
            creditorEnums = creditors.stream().map(item -> Assert.enumNotValid(CreditorEnum.class, item,
                    "主体")).collect(Collectors.toList());
        }
    }
}
