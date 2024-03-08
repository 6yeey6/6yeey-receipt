package com.ibg.receipt.vo.api.manage;

import com.ibg.receipt.enums.job.JobMachineStatus;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author tianxinwen
 */
@Data
public class JobAddVO extends BaseRequestVO{
    @NotNull(message = "给定枚举不存在")
    private JobMachineStatus jobMachineStatus;
    @NotEmpty(message = "业务Key不能为空")
    private String businessKey;
    private String jobParam;
    @Min(value = 1, message = "必须为正整数")
    private String lastJobId;
    private Date jobStartTime;
}
