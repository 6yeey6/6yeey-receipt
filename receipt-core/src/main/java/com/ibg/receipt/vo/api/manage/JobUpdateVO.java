package com.ibg.receipt.vo.api.manage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 * @author liuye07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobUpdateVO{
    /**
     * jobId
     */
    @NotNull
    private Long jobId;
    /**
     * 修改后job参数
     */
    private String jobParam;
    /**
     * 修改后job下次开始时间
     */
    private Date jobStartTime;
    /**
     * 修改后Job状态
     */
    private Integer jobStatus;
    /**
     * 修改后job业务状态
     */
    private Integer businessStatus;
    /**
     * 原始Job状态
     */
    @NotNull
    private Integer oldJobStatus;
    /**
     * 原始job业务状态
     */
    @NotNull
    private Integer oldBusinessStatus;
    /**
     * 原始job参数（暂无用处，仅作为日志记录---建野要求）
     */
    private String oldParam;
}

