package com.ibg.receipt.dao.repay;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.model.repay.ChannelRepayFileSync;


public interface ChannelRepayFileSyncRepository extends BaseRepository<ChannelRepayFileSync> {

    ChannelRepayFileSync findByRepayOrderKey(String repayOrderKey);

}
