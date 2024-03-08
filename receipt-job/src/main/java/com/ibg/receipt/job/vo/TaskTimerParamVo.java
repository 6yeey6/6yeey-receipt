package com.ibg.receipt.job.vo;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TaskTimerParamVo {

    /** 主键 */
    private Long id;

    /** 任务定时器 */
    private Long taskTimerId;

    /** 参数名称 */
    private String paramName;

    /** 参数类型 */
    private String paramType;

    /** 参数值 */
    private String paramValue;

    /** 创建时间 */
    private Date createTime;

    /** 修改时间 */
    private Date updateTime;

}