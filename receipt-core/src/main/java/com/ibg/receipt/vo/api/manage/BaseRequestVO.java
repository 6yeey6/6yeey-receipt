package com.ibg.receipt.vo.api.manage;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2020/5/28 9:43 <br>
 * @description BaseVO <br>
 */
@Data
public class BaseRequestVO {
    /**
     * 操作员ID
     */
    @NotNull
    private String operateId;
    /**
     * 操作员名称
     */
    @NotNull
    private String operateName;
}
