package com.ibg.receipt.dao.receiptUploadInfo;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.receipt.ReceiptOrderLoan;
import com.ibg.receipt.model.receiptUploadInfo.ReceiptUploadInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface ReceiptUploadInfoRepository extends BaseRepository<ReceiptUploadInfo> {

    List<ReceiptUploadInfo> findByUploadBatchNo(String uploadBatchNo);
}
