package com.ibg.receipt.service.receipt;

import com.ibg.receipt.base.service.BaseService;
import com.ibg.receipt.model.receipt.ReceiptRepayDetail;
import com.ibg.receipt.model.receipt.ReceiptUser;

import java.util.List;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 14:02
 */
public interface ReceiptUserService extends BaseService<ReceiptUser>  {

    ReceiptUser getReceiptUserByUserName(String userName);

    List<ReceiptUser> findAllUserList();

    void check(String landUserName) throws Exception;
}
