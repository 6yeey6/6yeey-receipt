package com.ibg.receipt.service.nuonuo;

import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptApplyReqVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptQueryReqVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptRetryReqVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptApplyRespVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptQueryRespVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptRetryRespVo;

import java.util.List;

/**
 * 诺诺接口
 */
public interface NuoNuoService {

    /**
     * 获取access_token
     *
     * @return
     */
    String getAccessToken(String appKey, String appSecret);

    /**
     * 开票申请
     *
     * @param vo
     * @return
     */
    JsonResultVo<ReceiptApplyRespVo> receiptApply(ReceiptApplyReqVo vo);

    /**
     * 开票结果查询
     *
     * @param vo
     * @return
     */
    JsonResultVo<List<ReceiptQueryRespVo>> receiptQuery(ReceiptQueryReqVo vo);

    /**
     * 开票重试接口
     *
     * @param vo
     * @return
     */
    JsonResultVo<ReceiptRetryRespVo> receiptRetry(ReceiptRetryReqVo vo);



    /**
     * 诺税通saas请求开具发票接口
     *
     * @param vo
     * @return
     */
    JsonResultVo<ReceiptApplyRespVo> receiptNewApply(ReceiptApplyReqVo vo);



    /**
     * 诺税通saas请求开具发票接口
     *
     * @param vo
     * @return
     */
    JsonResultVo<List<ReceiptQueryRespVo>> receiptNewQuery(ReceiptQueryReqVo vo);

}
