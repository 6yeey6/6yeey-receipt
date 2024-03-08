package com.ibg.receipt.job.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.job.constants.TaskStatus;
import com.ibg.receipt.job.model.TaskTimer;

public interface TaskTimerRepository extends BaseRepository<TaskTimer> {

    TaskTimer findByTaskClass(String taskClass);

    List<TaskTimer> findByTaskStatus(TaskStatus taskStatus);

    Page<TaskTimer> findByTaskStatus(TaskStatus taskStatus, Pageable pageable);

    Page<TaskTimer> findByTaskClass(String taskClass, Pageable pageable);

    Page<TaskTimer> findByTaskClassAndTaskStatus(String taskClass, TaskStatus taskStatus, Pageable pageable);

    List<TaskTimer> findByTaskClassAndTaskStatus(String taskClass, TaskStatus taskStatus);

}
