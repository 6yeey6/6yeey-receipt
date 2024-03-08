package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReceiptOrderRepository extends BaseRepository<ReceiptOrder>, JpaSpecificationExecutor<ReceiptOrder> {

    ReceiptOrder findByReceiptOrderKey(String receiptOrderKey);
}
