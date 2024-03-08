package com.ibg.receipt.job.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.base.vo.CustomPageRequest;
import com.ibg.receipt.job.constants.ParamType;
import com.ibg.receipt.job.constants.TaskStatus;
import com.ibg.receipt.job.dao.TaskTimerRepository;
import com.ibg.receipt.job.model.TaskTimer;
import com.ibg.receipt.job.service.SysJobService;
import com.ibg.receipt.job.service.TaskTimerParamService;
import com.ibg.receipt.job.service.TaskTimerService;
import com.ibg.receipt.job.vo.TaskTimerListResponseVo;
import com.ibg.receipt.job.vo.TaskTimerRequestVo;
import com.ibg.receipt.job.vo.TaskTimerVo;
import com.ibg.receipt.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class TaskTimerServiceImpl extends BaseServiceImpl<TaskTimer, TaskTimerRepository> implements TaskTimerService {

    @Autowired
    private TaskTimerParamService taskTimerParamService;
    @Autowired
    @Lazy
    private SysJobService sysJobService;

    @Autowired
    @Override
    protected void setRepository(TaskTimerRepository repository) {
        super.repository = repository;
    }

    private TaskTimer getByTaskClass(String taskClass) {
        return repository.findByTaskClass(taskClass);
    }

    @Override
    public TaskTimer loadByTaskClass(String taskClass) {
        TaskTimer taskTimer = getByTaskClass(taskClass);
        Assert.notExist(taskTimer, "任务定时器");
        return taskTimer;
    }

    @Override
    public List<TaskTimer> findRunningTaskTimer() {
        return repository.findByTaskStatus(TaskStatus.RUNNING);
    }

    @Override
    public List<TaskTimer> findStoppingTaskTimer() {
        return repository.findByTaskStatus(TaskStatus.STOPPING);
    }

    @Override
    public TaskTimerListResponseVo findTaskTimerList(TaskTimerRequestVo queryVo) {
        TaskTimerListResponseVo resultVo = new TaskTimerListResponseVo();
        String taskClass = queryVo.getTaskClass();
        TaskStatus taskStatus = null;
        if (queryVo.getTaskStatus() != null) {
            taskStatus = Assert.enumNotValid(TaskStatus.class, queryVo.getTaskStatus(), "状态");
        }
        Integer pageNo = queryVo.getPageNo();
        Integer pageSize = queryVo.getPageSize();
        Page<TaskTimer> page = null;
        if (pageNo != null && pageSize != null) {
            if (taskClass != null && taskStatus != null) {
                page = repository.findByTaskClassAndTaskStatus(taskClass, taskStatus,
                        new CustomPageRequest(pageNo, pageSize));
            } else if (taskClass != null) {
                page = repository.findByTaskClass(taskClass, new CustomPageRequest(pageNo, pageSize));
            } else if (taskStatus != null) {
                page = repository.findByTaskStatus(taskStatus, new CustomPageRequest(pageNo, pageSize));
            } else {
                page = repository.findAll(new CustomPageRequest(pageNo, pageSize));
            }
        } else {
            List<TaskTimer> taskTimerList = null;
            if (taskClass != null && taskStatus != null) {
                taskTimerList = repository.findByTaskClassAndTaskStatus(taskClass, taskStatus);
            } else if (taskClass != null) {
                taskTimerList = new ArrayList<>();
                TaskTimer taskTimer = repository.findByTaskClass(taskClass);
                taskTimerList.add(taskTimer);
            } else if (taskStatus != null) {
                taskTimerList = repository.findByTaskStatus(taskStatus);
            } else {
                taskTimerList = repository.findAll();
            }
            if (CollectionUtils.isEmpty(taskTimerList)) {
                page = new PageImpl<>(Collections.emptyList());
            } else {
                page = new PageImpl<>(taskTimerList, new CustomPageRequest(1, taskTimerList.size()),
                        taskTimerList.size());
            }
        }
        List<TaskTimerVo> list = new ArrayList<TaskTimerVo>();
        page.forEach(taskTimer -> {
            TaskTimerVo taskInfo = buildTaskTimerVo(taskTimer);
            list.add(taskInfo);
        });
        resultVo.setTaskTimerList(list);
        resultVo.setTotal(page.getTotalElements());
        resultVo.setPageNo(null == pageNo ? 1 : pageNo);
        resultVo.setPageSize(page.getSize());
        return resultVo;
    }

    @Override
    public void updateTaskTimerStatus(TaskTimerRequestVo requestVo) {
        Assert.notNull(requestVo.getId(), "taskTimer主键");
        TaskStatus taskStatus = Assert.enumNotValid(TaskStatus.class, requestVo.getTaskStatus(), "状态");
        TaskTimer taskTimer = this.load(requestVo.getId());
        if (taskTimer.getTaskStatus() == taskStatus) {
            return;
        }
        taskTimer.setTaskStatus(taskStatus);
        this.update(taskTimer);
        try {
            switch (taskStatus) {
                case RUNNING:
                    sysJobService.runJobById(taskTimer.getId());
                    break;
                case STOPPING:
                    sysJobService.stopJob(taskTimer.getId());
                    break;
                default:
                    throw new RuntimeException("不可到达的分支");
            }
        } catch (Exception e) {
            log.error("{}任务失败", taskStatus.toString(), e);
            throw new RuntimeException(String.format("%s任务失败：%s", taskStatus.toString(), e.getMessage()));
        }
    }

    public TaskTimerVo buildTaskTimerVo(TaskTimer taskTimer) {
        if (taskTimer == null) {
            return null;
        }
        TaskTimerVo taskTimerVo = new TaskTimerVo();
        taskTimerVo.setId(taskTimer.getId());
        taskTimerVo.setTaskName(taskTimer.getTaskName());
        taskTimerVo.setTaskClass(taskTimer.getTaskClass());
        taskTimerVo.setTaskType(taskTimer.getTaskType().toString());
        taskTimerVo.setTaskStatus(taskTimer.getTaskStatus().name());
        taskTimerVo.setRemark(taskTimer.getRemark());
        taskTimerVo.setCreateTime(taskTimer.getCreateTime());
        taskTimerVo.setUpdateTime(taskTimer.getUpdateTime());
        Map<ParamType, String> paramMapping = taskTimerParamService.findParamMapping(taskTimer.getId());
        if (MapUtils.isNotEmpty(paramMapping)) {
            switch (taskTimer.getTaskType()) {
                case TIMING_TASK:
                    taskTimerVo.setParamValue(paramMapping.get(ParamType.TIMING_PARAM));
                    break;
                case LOOP_TASK:
                    // 延迟时间
                    String delay = paramMapping.get(ParamType.LOOP_DELAY_PARAM);
                    // 间隔时间
                    String interval = paramMapping.get(ParamType.LOOP_INTERVAL_PARAM);
                    taskTimerVo.setParamValue(delay + "/" + interval);
                    break;
                case CRON_TASK:
                    taskTimerVo.setParamValue(paramMapping.get(ParamType.CRON_PARAM));
                    break;
                default:
                    throw new RuntimeException("不可到达的分支");
            }
        }
        return taskTimerVo;
    }

}
