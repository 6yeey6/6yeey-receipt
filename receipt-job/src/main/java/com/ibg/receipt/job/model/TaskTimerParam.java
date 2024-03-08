package com.ibg.receipt.job.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.job.constants.ParamType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务定时器参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task_timer_param")
public class TaskTimerParam extends BaseModel {

    private static final long serialVersionUID = -6460779547969200134L;

    /** 任务定时器 */
    @Column(name = "task_timer_id", nullable = false)
    private Long taskTimerId;

    /** 参数名称 */
    @Column(name = "param_name", nullable = false)
    private String paramName;

    /** 参数类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "param_type", nullable = false)
    private ParamType paramType;

    /** 参数值 */
    @Column(name = "param_value", nullable = false)
    private String paramValue;

}
