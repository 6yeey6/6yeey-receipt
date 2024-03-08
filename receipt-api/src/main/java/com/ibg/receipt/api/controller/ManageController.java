package com.ibg.receipt.api.controller;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.haohuan.HaoHuanService;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.haohuan.HaoHuanReqVo;
import com.ibg.receipt.vo.api.haohuan.HaoHuanRespVo;
import com.ibg.receipt.vo.api.manage.QueryBusinessContractReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private HaoHuanService haoHuanService;

    @GetMapping("/redis/delete/{redisKey}")
    public JsonResultVo<?> redisDelete(@PathVariable("redisKey") String redisKey) {
        if(redisKey == null || StringUtils.isEmpty(redisKey)){
            return JsonResultVo.error("9999","传入redisKey为空");
        }
        redisService.releaseLockWithTTL(redisKey);
        return JsonResultVo.success();
    }

    @PostMapping("/query/business/contract")
    public JsonResultVo<?> queryBusinessContract(@RequestBody QueryBusinessContractReqVO reqVO) {
        try {
            List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> addressesList = haoHuanService.getTotalByUniqId(HaoHuanReqVo.builder().loanIds(reqVO.getLoanId()).build());
            return JsonResultVo.success().setData(addressesList);
        } catch (ServiceException e) {
            log.warn("查询业务合同系统异常", e);
            return JsonResultVo.error(CodeConstants.C_10101002, "查询失败:" + e.getMessage());
        } catch (Exception e) {
            log.warn("查询业务合同系统异常", e);
            return JsonResultVo.error();
        }
    }



}
