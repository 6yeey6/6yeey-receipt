package com.ibg.receipt.service.funder;

import com.ibg.receipt.service.common.NamedService;

public interface FunderRepayService extends NamedService {

    /**
     * 回购
     *
     * @param batchNo
     */
    void repurchase(String batchNo);

    /**
     * 发起还款
     */
    default void batchFunderRepay(String batchNo) {
        throw new UnsupportedOperationException("不支持此操作batchFunderRepay");
    }

    /**
     * 还款结果查询
     */
    default void batchFunderRepayQuery(String batchNo) {
        throw new UnsupportedOperationException("不支持此操作batchFunderRepay");
    }

    /**
     * 回购支付流水记录查询
     */
    default void batchOffLinePayQuery(String batchNo) {
        throw new UnsupportedOperationException("不支持此操作batchOffLinePayQuery");
    }

    /**
     * 订单支付
     */
    default void batchFunderRepayPay(String batchNo) {
        throw new UnsupportedOperationException("不支持此操作batchFunderRepay");
    }

    /**
     * 订单支付查询
     */
    default void batchFunderRepayPayQuery(String batchNo) {
        throw new UnsupportedOperationException("不支持此操作batchFunderRepayPayQuery");

    }
}