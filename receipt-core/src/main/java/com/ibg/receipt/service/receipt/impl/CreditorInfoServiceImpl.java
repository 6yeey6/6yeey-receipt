package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.CreditorInfoRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.CreditorInfo;
import com.ibg.receipt.service.receipt.CreditorInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CreditorInfoServiceImpl extends BaseServiceImpl<CreditorInfo, CreditorInfoRepository>
        implements CreditorInfoService {
    @Autowired
    @Override
    protected void setRepository(CreditorInfoRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<CreditorInfo> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted) {
        return repository.findByCreditorAndDeleted(creditor, deleted);
    }

    @Override
    public CreditorInfo getByCreditor(CreditorEnum creditor) {
        List<CreditorInfo> creditorInfos = repository.findByCreditorAndDeleted(creditor, false);
        Assert.notEmpty(creditorInfos, "creditorInfos");
        return creditorInfos.get(0);
    }
}
