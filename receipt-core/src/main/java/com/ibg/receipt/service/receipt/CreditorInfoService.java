package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorInfo;

import java.util.List;

public interface CreditorInfoService extends BaseService<CreditorInfo> {

    List<CreditorInfo> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

    CreditorInfo getByCreditor(CreditorEnum creditor);
}
