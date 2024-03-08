package com.ibg.receipt.job.service;

import java.util.List;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.job.model.TaskTimer;
import com.ibg.receipt.job.vo.TaskTimerListResponseVo;
import com.ibg.receipt.job.vo.TaskTimerRequestVo;

public interface TaskTimerService extends BaseService<TaskTimer> {

    TaskTimer loadByTaskClass(String taskClass);

    List<TaskTimer> findRunningTaskTimer();

    List<TaskTimer> findStoppingTaskTimer();

    TaskTimerListResponseVo findTaskTimerList(TaskTimerRequestVo queryVo);

    void updateTaskTimerStatus(TaskTimerRequestVo requestVo);

}
