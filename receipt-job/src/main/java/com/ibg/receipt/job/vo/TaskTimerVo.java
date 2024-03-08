package com.ibg.receipt.job.vo;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskTimerVo     {

    /** 主键 */
    private Long id;

    /** 任务名称 */
    private String taskName;

    /** 任务类名 */
    private String taskClass;

    /** 任务类型 */
    private String taskType;

    /** 任务状态 */
    private String taskStatus;

    /** 参数值 */
    private String paramValue;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

}
