package com.ibg.receipt.base.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询分页列表基础vo
 */
@Data
@AllArgsConstructor(staticName = "getInstance")
@NoArgsConstructor
public class PageInfo<T> {

    /** 总数 */
    private Long total;

    /** 页数 */
    private Integer pageNum;

    /** 数量 */
    private Integer pageSize;

    /** 信息 */
    private List<T> list;

}
