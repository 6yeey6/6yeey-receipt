package com.ibg.receipt.base;


import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.service.job.JobService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class ReceiptJobTest extends TestBase {

    @Autowired
    JobService jobService;
    @Autowired
    private  com.ibg.receipt.job.quartz.receipt.ReceiptQueryLoanIdsJob receiptQueryLoanIdsJob;
    @Autowired
    private com.ibg.receipt.job.handler.receipt.ReceiptInitChildOrderHandler receiptInitChildOrderHandler;


    //@Test
    //public void testRepurchase(){
    //    receiptQueryLoanIdsJob.run("");
    //}

    @Test
    public void hanler() throws Exception{
        Job job = jobService.get(15L);
        receiptInitChildOrderHandler.getJob().set(job);
        receiptInitChildOrderHandler.handler();
    }

}
