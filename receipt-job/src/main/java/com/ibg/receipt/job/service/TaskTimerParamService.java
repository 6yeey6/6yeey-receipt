package com.ibg.receipt.job.service;

import java.util.List;
import java.util.Map;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.job.constants.ParamType;
import com.ibg.receipt.job.model.TaskTimerParam;
import com.ibg.receipt.job.vo.TaskTimerParamRequestVo;
import com.ibg.receipt.job.vo.TaskTimerParamVo;

public interface TaskTimerParamService extends BaseService<TaskTimerParam> {

    Map<ParamType, String> findParamMapping(Long taskTimerId);

    List<TaskTimerParamVo> findTaskTimerParamByTaskTimerId(TaskTimerParamRequestVo messageVo);

    void updateTaskTimerParam(TaskTimerParamRequestVo requestVo);

}
