package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.base.vo.PageVo;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.model.receipt.ReceiptOrder;
import com.ibg.receipt.vo.api.order.OrderReceiptQueryRequestVo;
import org.springframework.data.domain.Page;

public interface ReceiptOrderService extends BaseService<ReceiptOrder>{

    ReceiptOrder findByReceiptOrderKey(String receiptOrderKey);

    Page<ReceiptOrder> findAllqueryPaged(OrderReceiptQueryRequestVo requestVo);

    ReceiptOrder saveReceiptOrder(ReceiptOrder receiptOrder);
}
