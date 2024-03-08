package com.ibg.receipt.job.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskTimerListResponseVo implements Serializable {

    private static final long serialVersionUID = 7563496466531274997L;

    /** 列表 */
    private List<TaskTimerVo> taskTimerList;

    /** 总数 */
    private Long total;

    /** 页数 */
    private Integer pageNo;

    /** 数量 */
    private Integer pageSize;

}
