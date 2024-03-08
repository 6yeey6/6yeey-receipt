package com.ibg.receipt.job.handler.receipt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.enums.business.ReceiptItemCodeAmount;
import com.ibg.receipt.enums.job.JobMachineStatus;
import com.ibg.receipt.enums.job.JobStatus;
import com.ibg.receipt.job.handler.base.BaseHandler;
import com.ibg.receipt.model.job.Job;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;
import com.ibg.receipt.service.common.MetaFileService;
import com.ibg.receipt.service.job.JobService;
import com.ibg.receipt.service.receipt.ReceiptService;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.service.receiptUploadInfo.ReceiptUploadInfoService;
import com.ibg.receipt.util.*;
import com.ibg.receipt.vo.api.receiptChild.ChildOrderSuccessVo;
import com.ibg.receipt.vo.api.receiptChild.ReceiptChildListResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 子单完成
 */
@Component
@Slf4j
public class ReceiptUploadFinishHandler extends BaseHandler implements CommandLineRunner{

    @Autowired
    private ReceiptUploadInfoService receiptUploadInfoService;
    @Autowired
    private MetaFileService metaFileService;
    @Autowired
    private FileSystemConfig fileSystemConfig;
    @Autowired
    private ReceiptUserService receiptUserService;

    @Override
    public void handler() throws Exception {
        Job job = this.getJob().get();
//        Job job = jobService.get(80l);
        String uploadBatchNo = job.getBusinessKey();
        List<ReceiptUploadInfo> receiptUploadInfoList = receiptUploadInfoService.findByUploadBatchNo(uploadBatchNo);
        String receiptUserId = receiptUploadInfoList.get(0).getReceiptUserId();
        ReceiptUser receiptUser = receiptUserService.getReceiptUserByUserName(receiptUserId);
        //生成上传明细文件
        //构建sheet1
        String sheetName = "上传结果"+uploadBatchNo;
        List<String> titles = Lists.newArrayList("借款ID","开票项资金编码", "子单ID", "上传匹配状态","上传文件名",
                "文件访问链接");
        List<String> cellList = Lists.newArrayList("loanId","receiptItemCode",  "receiptChildOrderKey", "status","fileName",
                "receiptUrl");

        XSSFWorkbook workbook = generateExcel(sheetName,titles,cellList,receiptUploadInfoList);

        //上传文件
        String metaFileId = metaFileService.uploadToMetaFs(new ByteArrayInputStream(ExcelUtils.excelToByte(workbook)) ,uploadBatchNo+".xls" ,
                null, null, "RECEIPT");
        String fileUrl = fileSystemConfig.getReceiptFileDownloadUrl() + metaFileId;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileUrl",fileUrl);
        jsonObject.put("receiver",fileSystemConfig.getMailToGroup());
        //生成发邮件job
        jobService.generateJob(JobMachineStatus.SEND_INNER_EMAIL_URL, uploadBatchNo, jsonObject.toJSONString(), new Date(),null);

        //更新任务状态
        saveJob(JobStatus.SUCCESS, ProcessStatus.SUCCESS, null);
    }

    private XSSFWorkbook generateExcel(String sheetName, List<String> titles, List<String> cellList,
                                       List<ReceiptUploadInfo> responseVos) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        int rowNum = 0;
        XSSFRow row = sheet.createRow(rowNum++);
        for (int i = 0; i < cellList.size(); i++) {
            row.createCell(i).setCellValue(titles.get(i));
        }
        for (Object t : responseVos) {
            row = sheet.createRow(rowNum++);
            for (int i = 0; i < cellList.size(); i++) {
                if(cellList.get(i).equals("receiptItemCode")){
                    row.createCell(i).setCellValue(ReceiptItemCodeAmount.getEnum(getFieldValueByFieldName(cellList.get(i), t)).getDesc());
                }else if(cellList.get(i).equals("status")){
                    row.createCell(i).setCellValue(StringUtils.equals(StringUtils.getWithDefault(getFieldValueByFieldName(cellList.get(i), t),"0"),"1")
                            ?"匹配成功":"匹配失败");
                }else{
                    row.createCell(i).setCellValue(EncryptUtil.getDecoded(StringUtils.getWithDefault(getFieldValueByFieldName(cellList.get(i), t),"无")));
                }
            }
        }
        return workbook;
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


    @Override
    public void run(String... strings) throws Exception {
//        handler();
    }
}
