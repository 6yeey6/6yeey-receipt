package com.ibg.receipt.job.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskTimerParamRequestVo {

    /** 主键 */
    private Long id;

    /** 任务定时器 */
    private Long taskTimerId;

    /** 参数值 */
    private String paramValue;

    /** 分页页数（可空） */
    private Integer pageNo;

    /** 分页大小（可空） */
    private Integer pageSize;

}