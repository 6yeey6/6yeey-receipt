package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.model.receipt.CreditorAmountConfig;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;

import java.util.List;


public interface CreditorBaseConfigRepository extends BaseRepository<CreditorBaseConfig> {

    List<CreditorBaseConfig> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

    List<CreditorBaseConfig> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted);
}
