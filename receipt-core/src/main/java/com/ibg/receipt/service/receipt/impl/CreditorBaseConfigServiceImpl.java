package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.CreditorBaseConfigRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;
import com.ibg.receipt.service.receipt.CreditorBaseConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CreditorBaseConfigServiceImpl extends BaseServiceImpl<CreditorBaseConfig, CreditorBaseConfigRepository>
        implements CreditorBaseConfigService {

    @Autowired
    @Override
    protected void setRepository(CreditorBaseConfigRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<CreditorBaseConfig> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted) {
        return this.repository.findByCreditorAndDeleted(creditor, deleted);
    }
    @Override
    public void batchSave(List<CreditorBaseConfig> creditorBaseConfigs) {
        this.repository.save(creditorBaseConfigs);
    }

    @Override
    public List<CreditorBaseConfig> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted) {
        return this.repository.findByCreditorAndCreditorConfigVersionAndDeleted(creditor, creditorConfigVersion, deleted);
    }

}
