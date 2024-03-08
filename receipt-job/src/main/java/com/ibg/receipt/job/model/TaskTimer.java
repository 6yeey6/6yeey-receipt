package com.ibg.receipt.job.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.job.constants.TaskStatus;
import com.ibg.receipt.job.constants.TaskType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务定时器
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task_timer")
public class TaskTimer extends BaseModel {

    private static final long serialVersionUID = 7667634308595888626L;

    /** 任务名称 */
    @Column(name = "task_name", nullable = false, unique = true)
    private String taskName;

    /** 任务类名 */
    @Column(name = "task_class", nullable = false, unique = true)
    private String taskClass;

    /** 任务类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    /** 任务状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskStatus taskStatus;

    /** 备注 */
    @Column(name = "remark")
    private String remark;

    /** 任务参数 */
    @Column(name = "data_param")
    private String dataParam;
}
