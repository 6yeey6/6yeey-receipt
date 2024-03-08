package com.ibg.receipt.vo.api.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询父类
 *
 * @author zhou
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageVo {
    /** 页码 */
    private Integer pageNum;
    /** 页数 */
    private Integer pageSize;

}
