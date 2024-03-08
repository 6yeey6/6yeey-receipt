package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.model.receipt.CreditorAmountConfig;
import com.ibg.receipt.model.receiptChild.ReceiptChildOrderAmount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CreditorService extends BaseService<Creditor> {

    List<Creditor> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

    List<Creditor> findByDeleted(Boolean deleted);

    List<Creditor> findByCreditorAndcreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted);

    Page<Creditor> findPageByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted, Integer pageNum, Integer pageSize);

    Page<Creditor> findPageByDeleted(Boolean deleted, Integer pageNum, Integer pageSize);

    List<Creditor> findByUserNameAndDeleted(String userName, Boolean deleted);

    List<Creditor> findByCreditorInAndDeleted(List<CreditorEnum>Creditors,Boolean deleted);
}
