package com.ibg.receipt.job.service;

public interface SysJobService {

    void runJobById(Long id) throws Exception;

    void runJobByTaskClass(String taskClass) throws Exception;

    void runAllJob();

    void stopJob(Long id) throws Exception;

    void stopJob(String taskClass) throws Exception;

    void stopAllJob();

}
