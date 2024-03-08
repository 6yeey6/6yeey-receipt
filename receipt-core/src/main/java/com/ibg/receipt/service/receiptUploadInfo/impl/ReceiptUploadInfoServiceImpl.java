package com.ibg.receipt.service.receiptUploadInfo.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receiptUploadInfo.ReceiptUploadInfoRepository;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;
import com.ibg.receipt.service.receiptUploadInfo.ReceiptUploadInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReceiptUploadInfoServiceImpl extends BaseServiceImpl<ReceiptUploadInfo, ReceiptUploadInfoRepository>
        implements ReceiptUploadInfoService {

    @Autowired
    @Override
    protected void setRepository(ReceiptUploadInfoRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ReceiptUploadInfo> findByUploadBatchNo(String uploadBatchNo) {

        return repository.findByUploadBatchNo(uploadBatchNo);
    }
}
