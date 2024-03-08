package com.ibg.receipt.model.job;

import com.ibg.receipt.base.model.BaseModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Job extends BaseModel {

    private static final long serialVersionUID = -8663312102692138576L;

    /**
     * job类型
     */
    @Column(name = "machine_status", nullable = false)
    private String machineStatus;

    /**
     * 父job ID
     */
    @Column(name = "last_job_id")
    private Long lastJobId;

    /**
     * job类型
     */
    @Column(name = "job_type", nullable = false)
    private String jobType;

    /**
     * job状态
     */
    @Column(name = "job_status", nullable = false)
    private Byte jobStatus;

    /**
     * 业务状态
     */
    @Column(name = "business_status", nullable = false)
    private Byte businessStatus;

    /**
     * 业务key
     */
    @Column(name = "business_key", nullable = false)
    private String businessKey;

    /**
     * 执行次数
     */
    @Column(name = "execute_times")
    private Integer executeTimes;

    /**
     * 最大执行次数
     */
    @Column(name = "max_execute_times")
    private Integer maxExecuteTimes;

    /**
     * 上次出错原因
     */
    @Column(name = "last_error")
    private String lastError;

    /**
     * job参数
     */
    @Column(name = "job_param")
    private String jobParam;

    /**
     * job开始时间
     */
    @Column(name = "start_time")
    private Date jobStartTime;
}
