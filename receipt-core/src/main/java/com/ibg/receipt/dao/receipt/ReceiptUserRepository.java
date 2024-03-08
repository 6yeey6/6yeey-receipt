package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.receipt.ReceiptUser;

import java.util.List;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 14:06
 */
public interface ReceiptUserRepository extends BaseRepository<ReceiptUser> {


    ReceiptUser findByUserName(String userName);

}
