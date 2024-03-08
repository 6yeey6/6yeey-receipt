package com.ibg.receipt.vo.api.manage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2020/5/28 18:02 <br>
 * @description JobVO <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobVO {
    /** job类型 */
    private Long id;

    /** job类型 */
    private String machineStatus;

    /** 父job ID */
    private Long lastJobId;

    /** job类型 */
    private String jobType;

    /** job状态 */
    private Byte jobStatus;

    /** 业务状态 */
    private Byte businessStatus;

    /** 业务key */
    private String businessKey;

    /** 执行次数 */
    private Integer executeTimes;

    /** 最大执行次数 */
    private Integer maxExecuteTimes;

    /** 上次出错原因 */
    private String lastError;

    /** job参数 */
    private String jobParam;

    /** job开始时间 */
    private Date jobStartTime;
}
