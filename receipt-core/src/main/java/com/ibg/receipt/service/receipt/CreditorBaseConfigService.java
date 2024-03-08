package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;

import java.util.List;

public interface CreditorBaseConfigService extends BaseService<CreditorBaseConfig> {

    List<CreditorBaseConfig> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

    void batchSave(List<CreditorBaseConfig> creditorBaseConfigs);

    List<CreditorBaseConfig> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted);

}
