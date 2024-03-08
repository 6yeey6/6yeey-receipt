package com.ibg.receipt.dao.job;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.job.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface JobRepository extends BaseRepository<Job>, JpaSpecificationExecutor<Job> {

    Page<Job> findByJobStatusAndIdGreaterThan(byte jobStatus, Long id, Pageable pageable);

    @Query(value = "select * from job where id > :id and job_status = :jobStatus and mod(id,:mod) = :result order by ?#{#pageable}",
        countQuery = "select count(1) from job where id > :id and job_status = :jobStatus and mod(id,:mod) = :result", nativeQuery = true)
    Page<Job> findJobBySharding(@Param("jobStatus") byte jobStatus, @Param("id") Long id, @Param("mod") int mod,
            @Param("result") int result, Pageable pageable);


    @Query(value = "select * from job where id > :id and job_status = :jobStatus and mod(id,:mod) = :result order by id limit :pageSize", nativeQuery = true)
    List<Job> findJobBySharding(@Param("jobStatus") byte jobStatus, @Param("id") Long id, @Param("mod") int mod,
            @Param("result") int result,  @Param("pageSize") int pageSize);
    List<Job> findAllByBusinessKeyEqualsOrderByUpdateTimeDesc(String businessKey);

    List<Job> findJobsByMachineStatusAndCreateTimeBetween(String machineStatus, Date starteTime,Date endTime);

    List<Job> findJobsByMachineStatusAndLastJobIdAndCreateTimeBetween(String machineStatus, Long id, Date starteTime,Date endTime);
}
