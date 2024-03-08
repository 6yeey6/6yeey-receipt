package com.ibg.receipt.job.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskTimerRequestVo  {

    /** 主键 */
    private Long id;

    /** 任务类名 */
    private String taskClass;

    /** TaskStatus状态 */
    private String taskStatus;

    /** 分页页数（可空） */
    private Integer pageNo;

    /** 分页大小（可空） */
    private Integer pageSize;

}