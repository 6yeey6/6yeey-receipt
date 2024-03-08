package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.CreditorAmountConfigRepository;
import com.ibg.receipt.dao.receiptChild.ReceiptChildOrderAmountRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorAmountConfig;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;
import com.ibg.receipt.model.receipt.ReceiptBaseInfo;
import com.ibg.receipt.service.receipt.CreditorAmountConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CreditorAmountConfigServiceImpl extends BaseServiceImpl<CreditorAmountConfig, CreditorAmountConfigRepository>
        implements CreditorAmountConfigService {
    @Autowired
    @Override
    protected void setRepository(CreditorAmountConfigRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<CreditorAmountConfig> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted) {
        return repository.findByCreditorAndDeleted(creditor, deleted);
    }

    @Override
    public List<CreditorAmountConfig> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted) {
        return repository.findByCreditorAndCreditorConfigVersionAndDeleted(creditor, creditorConfigVersion, deleted);
    }
    @Override
    public void batchSave(List<CreditorAmountConfig> creditorAmountConfigs) {
        repository.save(creditorAmountConfigs);
    }
}
