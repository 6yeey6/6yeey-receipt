package com.ibg.receipt.service.receiptUploadInfo;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;

import java.util.List;

public interface ReceiptUploadInfoService extends BaseService<ReceiptUploadInfo> {


    List<ReceiptUploadInfo> findByUploadBatchNo(String uploadBatchNo);
}
