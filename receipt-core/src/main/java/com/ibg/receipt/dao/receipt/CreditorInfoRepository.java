package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorInfo;

import java.util.List;

public interface CreditorInfoRepository extends BaseRepository<CreditorInfo> {

    List<CreditorInfo> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

}
