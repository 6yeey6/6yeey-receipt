package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorAmountConfig;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;

import java.util.List;

public interface CreditorAmountConfigService extends BaseService<CreditorAmountConfig> {

    List<CreditorAmountConfig> findByCreditorAndDeleted(CreditorEnum creditor,Boolean deleted);

    List<CreditorAmountConfig> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted);

    void batchSave(List<CreditorAmountConfig> creditorBaseConfigs);
}
