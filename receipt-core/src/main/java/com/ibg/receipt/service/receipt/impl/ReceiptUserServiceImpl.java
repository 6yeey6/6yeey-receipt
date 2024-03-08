package com.ibg.receipt.service.receipt.impl;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.service.impl.BaseServiceImpl;
import com.ibg.receipt.dao.receipt.ReceiptRepayDetailRepository;
import com.ibg.receipt.dao.receipt.ReceiptUserRepository;
import com.ibg.receipt.model.receipt.ReceiptUser;
import com.ibg.receipt.service.receipt.ReceiptUserService;
import com.ibg.receipt.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 14:05
 */
@Service
public class ReceiptUserServiceImpl extends BaseServiceImpl<ReceiptUser, ReceiptUserRepository>
        implements ReceiptUserService {

    @Autowired
    @Override
    protected void setRepository(ReceiptUserRepository repository) {
        super.repository = repository;
    }

    @Override
    @Transactional
    public ReceiptUser getReceiptUserByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    @Override
    public List<ReceiptUser> findAllUserList() {
        return repository.findAll();
    }

    @Override
    public void check(String landUserName) throws Exception {
        if(StringUtils.isEmpty(landUserName)){
            throw new Exception("登陆用户名为空");
        }
        if(getReceiptUserByUserName(landUserName) == null){
            throw new Exception("登陆用户不合法");
        }
    }
}
