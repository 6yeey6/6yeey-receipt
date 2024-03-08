package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.service.common.MailSender;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

/**
 */
@Component
@Slf4j
public class SendPasswordHandler extends BaseHandler {

    @Autowired
    private MailSender mailSender;
    @Autowired
    private ReceiptUserService receiptUserService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
        String password = job.getBusinessKey();
        password = EncryptUtil.getDecoded(password);
        JSONObject jsonObject = JSONObject.parseObject(job.getJobParam());
        String landUserName = jsonObject.getString("landUserName");
        ReceiptUser receiptUser = receiptUserService.getReceiptUserByUserName(landUserName);
        mailSender.send(receiptUser.getEmail().split(","),"导出文件秘钥",password);
        //更新任务状态
        saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
    }


    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名
     * @param object    类
     */
    private String getFieldValueByFieldName(String fieldName, Object object) {
        Object val = null;
        try {
            if (object instanceof Map) {
                val = ((Map) object).get(fieldName);
            } else {
                Field field = object.getClass().getDeclaredField(fieldName);
                //设置对象的访问权限，保证对private的属性的访问
                field.setAccessible(true);
                val = field.get(object);
            }

        } catch (Exception e) {
            return null;
        }
        if (val instanceof Enum) {
            val = ((Enum) val).name();
        } else {
            val = String.valueOf(val);
        }
        return (String) val;
    }

    @Override
    @Autowired
    public void setJobService(JobService jobService) throws Exception {
        super.jobService = jobService;
    }


    public static void main(String[] args) {
        System.out.println(EncryptUtil.getDecoded("ITz0lZpgF0bRmQOvj0FTUisXiff/p7J1HaHGFtMA0f4="));
    }
}
