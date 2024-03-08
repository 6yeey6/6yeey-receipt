package com.ibg.receipt.job.dao;

import java.util.List;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.job.model.TaskTimerParam;

public interface TaskTimerParamRepository extends BaseRepository<TaskTimerParam> {

    List<TaskTimerParam> findByTaskTimerId(Long taskTimerId);

}
