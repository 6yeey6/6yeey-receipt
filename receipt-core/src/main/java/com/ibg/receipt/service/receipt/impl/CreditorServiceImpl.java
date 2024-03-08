package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.base.vo.CustomPageRequest;
import com.ibg.receipt.dao.receipt.CreditorRepository;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.service.receipt.CreditorService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CreditorServiceImpl extends BaseServiceImpl<Creditor, CreditorRepository>
        implements CreditorService {
    @Autowired
    @Override
    protected void setRepository(CreditorRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<Creditor> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted) {
        return this.repository.findByCreditorAndDeleted(creditor, deleted);
    }

    @Override
    public List<Creditor> findByDeleted(Boolean deleted) {
        return this.repository.findByDeleted(deleted);
    }

    @Override
    public List<Creditor> findByCreditorAndcreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted) {
        return this.repository.findByCreditorAndCreditorConfigVersionAndDeleted(creditor, creditorConfigVersion, deleted);
    }

    @Override
    public Page<Creditor> findPageByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted, Integer pageNum, Integer pageSize) {
        return repository.findPageByCreditorAndDeleted(creditor, deleted, new CustomPageRequest(pageNum, pageSize, Sort.Direction.DESC, "id"));
    }

    @Override
    public Page<Creditor> findPageByDeleted(Boolean deleted, Integer pageNum, Integer pageSize) {
        return repository.findPageByDeleted(deleted, new CustomPageRequest(pageNum, pageSize, Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Creditor> findByUserNameAndDeleted(String userName,Boolean deleted) {
        return repository.findByUserNameAndDeleted(userName,deleted);
    }

    @Override
    public List<Creditor> findByCreditorInAndDeleted(List<CreditorEnum>Creditors,Boolean deleted){
        return repository.findByCreditorInAndDeleted(Creditors,deleted);
    }
}
