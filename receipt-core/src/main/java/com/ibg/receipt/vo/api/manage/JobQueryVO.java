package com.ibg.receipt.vo.api.manage;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.StringUtils;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chenhao <br>
 * @version 1.0 <br>
 * @date 2020/5/27 16:11 <br>
 * @description JobQueryVO <br>
 */
@Data
public class JobQueryVO {
    /**
     * jobId列表
     */
    private Long jobId;
    /**
     * 业务key
     */
    private String businessKey;
    /**
     * job任务编码
     */
    private String jobMachineStatus;
    /**
     * job状态
     */
    private Integer jobStatus;
    /**
     * job业务状态
     */
    private Integer businessStatus;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 页数
     */
    @NotNull
    private Integer pageNum;
    /**
     * 每页大小
     */
    @NotNull
    private Integer pageSize;

    public void checkParam() {
        Assert.notBlank(this.beginTime, "开始时间");
        Assert.notBlank(this.endTime, "结束时间");
        try {
            DateUtils.parseDate(this.beginTime, DateUtils.DATE_TIME_FORMAT_PATTERN);
        } catch (Exception e) {
            throw new ServiceException("开始时间格式为:yyyy-MM-dd HH:mm:ss");
        }
        try {
            DateUtils.parseDate(this.endTime, DateUtils.DATE_TIME_FORMAT_PATTERN);
        } catch (Exception e) {
            throw new ServiceException("结束时间格式为:yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtils.isNotBlank(jobMachineStatus) && (null == jobStatus)) {
            throw new ServiceException("job状态必选");
        }
    }
}
