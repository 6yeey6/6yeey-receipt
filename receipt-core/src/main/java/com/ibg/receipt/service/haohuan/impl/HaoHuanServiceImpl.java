package com.ibg.receipt.service.haohuan.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.base.exception.ExceptionUtils;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.config.haohuan.HaoHuanConfig;
import com.ibg.receipt.service.haohuan.HaoHuanService;
import com.ibg.receipt.util.HttpUtils;
import com.ibg.receipt.util.JsonUtils;
import com.ibg.receipt.util.NoticeUtils;
import com.ibg.receipt.vo.api.haohuan.HaoHuanReqVo;
import com.ibg.receipt.vo.api.haohuan.HaoHuanRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 业务接口
 */
@Service
@Slf4j
public class HaoHuanServiceImpl implements HaoHuanService{

    private static final String SUCCESS_CODE = "0";
    @Autowired
    private HaoHuanConfig haoHuanConfig;
    @Qualifier("restTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> getTotalByUniqId(HaoHuanReqVo vo) {
        return sendRequest(vo.getLoanIds());
    }


    /**
     * 请求业务查询文件列表接口
     *
     * @param loanIds
     * @return
     */
    private List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> sendRequest(String loanIds) {
        MultiValueMap<String,Object> map = new LinkedMultiValueMap();
        map.add("loanIds",loanIds);
        map.add("content",null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Content-Type", "application/x-www-form-urlencoded");
        String url = "http://haofenqi-audit-service.haohuan.com/api/v1/protocol/getTotalByUniqId";
        log.info("调用业务查询合同接口请求值为：{}",JSON.toJSONString(map));
        HttpEntity<MultiValueMap<String,Object>> param = new HttpEntity<>(map,headers);
        JSONObject result = restTemplate.postForObject(url, param, JSONObject.class);
        log.info("调用业务查询合同接口返回值为:{}",JSON.toJSONString(result));
        HaoHuanRespVo respVo = JSON.parseObject(result.toJSONString(), HaoHuanRespVo.class);
        if (!SUCCESS_CODE.equals(respVo.getCode()) || ObjectUtil.isNull(respVo.getData()) || ObjectUtil.isNull(respVo.getData())
                || ObjectUtil.isNull(respVo.getData().getContractInfo()) || ObjectUtil.isNull(respVo.getData().getContractInfo().get(0).getContractAddress())) {
            log.error("调用HAOHUAN业务查询合同列表失败!loanId:", loanIds);
            NoticeUtils.businessError("调用HAOHUAN业务查询合同列表失败!loanId:" + loanIds);
            throw ExceptionUtils.commonError(String.format("调用HAOHUAN业务查询合同列表失败!loanId:" + loanIds + ";code:" + respVo.getCode() + ";result" + JSON.toJSONString(respVo)));
        }
        return respVo.getData().getContractInfo().get(0).getContractAddress();
    }

    public <T> T postDataFrom(MultiValueMap<String,Object> map, String url, Class<T> clazz) {
 /*MultiValueMap<String,Object> map = new LinkedMultiValueMap();
        map.add("loanIds","88639311");
        map.add("content",null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Content-Type", "application/x-www-form-urlencoded");
        String url = "http://haofenqi-audit-service.haohuan.com/api/v1/protocol/getTotalByUniqId";
        HttpEntity<MultiValueMap<String,Object>> param = new HttpEntity<>(map,headers);
        String result = restTemplate.postForObject(url, param, String.class);
        System.out.println(result);*/
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String,Object>> param = new HttpEntity<>(map,headers);
        T t = restTemplate.postForObject(url, param, clazz);
//        T t = restTemplate.postForObject(url, formEntity, clazz);
        return t;
    }


    public List<HaoHuanRespVo.ResultData.ContractInfo.ContractAddress> getResult() {
        String str = "{\n" +
                "    \"code\": 0,\n" +
                "    \"data\": {\n" +
                "        \"contractInfo\": [\n" +
                "            {\n" +
                "                \"partnerLoanNo\": 11125311,\n" +
                "                \"contractAddress\": [\n" +
                "                    {\n" +
                "                        \"title\": \"《贷款合同》\",\n" +
                "                        \"type\": 1,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/34/14/1/4900_11125311.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《委托扣款授权书》\",\n" +
                "                        \"type\": 2,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/34/15/3/4900_11125311.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《征信查询授权书》\",\n" +
                "                        \"type\": 3,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/34/16/5/4900_11125311.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《还款管理服务协议》\",\n" +
                "                        \"type\": 4,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/34/17/6/4900_11125311.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《贷款用途承诺书》\",\n" +
                "                        \"type\": 5,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/34/18/18/4900_11125311.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《个人征信授权书》\",\n" +
                "                        \"type\": 6,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/27/65/4/4900_HEIKA.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《额度服务协议》\",\n" +
                "                        \"type\": 7,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/68/14/7/4900_0.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《数字证书使用授权书》\",\n" +
                "                        \"type\": 8,\n" +
                "                        \"url\": \"http://haofenqi-api-server.test.rrdbg.com/download/protocol/27/66/8/4900_0.pdf\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"title\": \"《征信查询授权书》\",\n" +
                "                        \"type\": 9,\n" +
                "                        \"url\": \"\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        HaoHuanRespVo respVo = JSON.parseObject(str, HaoHuanRespVo.class);
        if (!SUCCESS_CODE.equals(respVo.getCode()) || ObjectUtil.isNull(respVo.getData()) || ObjectUtil.isNull(respVo.getData())
                || ObjectUtil.isNull(respVo.getData().getContractInfo()) || ObjectUtil.isNull(respVo.getData().getContractInfo().get(0).getContractAddress())) {
            log.error("调用HAOHUAN业务查询合同列表失败!loanId:", 38483391);
            throw ExceptionUtils.commonError(String.format("调用HAOHUAN业务查询合同列表失败!loanId:" + 38483391 + ";code:" + respVo.getCode() + ";result" + JSON.toJSONString(respVo)));
        }
        return respVo.getData().getContractInfo().get(0).getContractAddress();
    }
}
