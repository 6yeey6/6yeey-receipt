package com.ibg.receipt.service.haohuan;

import com.ibg.receipt.vo.api.haohuan.HaoHuanReqVo;
import com.ibg.receipt.vo.api.haohuan.HaoHuanRespVo;

import java.util.List;


/**
 * 调用业务接口
 */
public interface HaoHuanService {


    /**
     * 查询订单文件列表
     *
     * @param vo
     * @return
     */
    List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> getTotalByUniqId(HaoHuanReqVo vo);
}
