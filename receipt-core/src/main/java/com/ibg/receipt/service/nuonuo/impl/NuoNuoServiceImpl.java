package com.ibg.receipt.service.nuonuo.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.config.nuonuo.NuoNuoConfig;
import com.ibg.receipt.redis.service.RedisService;
import com.ibg.receipt.service.nuonuo.NuoNuoService;
import com.ibg.receipt.util.JacksonUtil;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.api.nuonuo.base.NuoNuoBaseReqVO;
import com.ibg.receipt.vo.api.nuonuo.base.NuoNuoBaseRespVO;
import com.ibg.receipt.vo.api.nuonuo.constants.NuoNuoConstants;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptApplyReqVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptQueryReqVo;
import com.ibg.receipt.vo.api.nuonuo.req.ReceiptRetryReqVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptApplyRespVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptQueryRespVo;
import com.ibg.receipt.vo.api.nuonuo.resp.ReceiptRetryRespVo;
import lombok.extern.slf4j.Slf4j;
import nuonuo.open.sdk.NNOpenSDK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 诺诺接口
 */
@Service
@Slf4j
public class NuoNuoServiceImpl implements NuoNuoService {

    @Autowired
    private NuoNuoConfig nuoNuoConfig;
    @Autowired
    private RedisService redisService;
    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public String getAccessToken(String appKey, String appSecret) {
        Assert.notBlank(appKey, "appKey");
        Assert.notBlank(appSecret, "appSecret");
        if ("test".equals(active)) {
            return "";
        }

        // 诺诺获取accessToken 30天
        String key = NuoNuoConstants.FUND_NUONUO_ACCESSTOKEN + appKey;

        // 判断缓存是否存在
        if (redisService.exists(key) && StringUtils.isNotBlank(redisService.get(key))) {
            return redisService.get(key);
        }

        try {
            log.info("诺诺-accessToken请求:appKey->{},appSecret->{}", appKey, appSecret);
            String merchantToken = NNOpenSDK.getIntance().getMerchantToken(appKey, appSecret);
            log.info("诺诺-accessToken响应:{}", merchantToken);
            if (StringUtils.isBlank(merchantToken)) {
                throw new ServiceException("请求诺诺accessToken响应为空");
            }
            JSONObject jsonObject = JSONObject.parseObject(merchantToken);
            String accessToken = jsonObject.getString("access_token");
            if (StringUtils.isBlank(accessToken)) {
                throw new ServiceException("请求诺诺accessToken响应异常,响应:" + merchantToken);
            }
            // 缓存accessToken,30天过期
            redisService.setex(key, 60 * 60 * 24 * 30, accessToken);
            return accessToken;
        } catch (Exception e) {
            log.error("请求诺诺accessToken异常：", e);
            throw new ServiceException("请求诺诺accessToken异常,待重试");
        }
    }

    private JsonResultVo sendRequest(NuoNuoBaseReqVO vo, String method) {
        NNOpenSDK sdk = NNOpenSDK.getIntance();
        // ISV下授权商户税号，自用型应用置""即可
        String taxnum = "test".equals(active) ? "339902999999789113" : "";
        String appKey = vo.getAppKey();
        String appSecret = vo.getAppSecret();
        // 访问令牌
        String token = this.getAccessToken(appKey, appSecret);
        String content = JacksonUtil.writeValue(vo);
        // 唯一标识，32位随机码，无需修改，保持默认即可
        String senid = UUID.randomUUID().toString().replace("-", "");
        String result;
        try {
            log.info("诺诺-接口请求:url->{},method->{},appKey->{},appSecret->{},token->{},content->{},senid->{}", nuoNuoConfig.getBasicUrl(),method,appKey, appSecret, token, content, senid);
            result = sdk.sendPostSyncRequest(nuoNuoConfig.getBasicUrl(), senid, appKey, appSecret, token, taxnum, method, content);
            log.info("诺诺-接口响应:{}", result);
        } catch (Exception e) {
            log.error("请求诺诺异常：", e);
            throw new ServiceException("请求诺诺异常");
        }
        if (StringUtils.isBlank(result)) {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "请求诺诺返回为空，请求参数：" + JSON.toJSONString(vo));
        }
        NuoNuoBaseRespVO respVO = JSONObject.parseObject(result, NuoNuoBaseRespVO.class);
        String code = respVO.getCode();
        String describe = respVO.getDescribe();
        Object data = respVO.getResult();
        return NuoNuoConstants.CODE_SUCCESS.equals(code) ? JsonResultVo.success(data) : JsonResultVo.errorWithData(code, describe, data);
    }

    @Override
    public JsonResultVo<ReceiptApplyRespVo> receiptApply(ReceiptApplyReqVo vo) {
        return sendRequest(vo, NuoNuoConstants.RECEIPT_APPLY_METHOD);
    }

    @Override
    public JsonResultVo<List<ReceiptQueryRespVo>> receiptQuery(ReceiptQueryReqVo vo) {
        return sendRequest(vo, NuoNuoConstants.RECEIPT_QUERY_METHOD);
    }

    @Override
    public JsonResultVo<ReceiptRetryRespVo> receiptRetry(ReceiptRetryReqVo vo) {
        return sendRequest(vo, NuoNuoConstants.RECEIPT_RETRY_METHOD);
    }

    @Override
    public JsonResultVo<ReceiptApplyRespVo> receiptNewApply(ReceiptApplyReqVo vo) {
        return sendRequest(vo, NuoNuoConstants.NEW_RECEIPT_APPLY_METHOD);
    }

    @Override
    public JsonResultVo<List<ReceiptQueryRespVo>> receiptNewQuery(ReceiptQueryReqVo vo) {
        return sendRequest(vo, NuoNuoConstants.NEW_RECEIPT_QUERY_METHOD);
    }
}
