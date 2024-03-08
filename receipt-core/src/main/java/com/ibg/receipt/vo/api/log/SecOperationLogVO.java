package com.ibg.receipt.vo.api.log;

import com.alibaba.fastjson.JSONObject;
import com.ibg.receipt.util.DateUtils;
import com.ibg.receipt.util.MD5Utils;
import com.ibg.receipt.util.StringUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.List;
import java.util.Map;

/**
 * 数据安全，日志打印vo
 */
@Data
public class SecOperationLogVO {
    /**
     * 系统用户id,
     * 若为系统调用则使用空字符串代替
     */
    private String systemUserId;
    /**
     * 系统用户姓名
     * 若为系统调用则使用空字符串代替
     */
    private String systemUserName;
    /**
     * 事件时间 yyyy-MM-dd HH:mm:ss
     */
    private String eventTime;
    /**
     * 业务平台编码 core-fund
     */
    private String businessId;
    /**
     * 被查询的用户key
     */
    private List<String> userKey;
    /**
     * 如果没有userkey，使用用户手机号代替，需脱敏
     * 手机号脱敏方式：Md5 小写 16位
     */
    private List<String> mobiles;
    /**
     * 手机号：0不包含、1包含&明文 、2包含&密文/脱敏
     */
    private String objectUserInfoMobile;
    /**
     * 银行卡号：0不包含、1包含&明文 、2包含&密文/脱敏
     */
    private String objectUserInfoBankNumber;
    /**
     * 身份证号：0不包含、1包含&明文 、2包含&密文/脱敏
     */
    private String objectUserInfoIdNumber;
    /**
     * 姓名：0不包含、1包含&明文 、2包含&密文/脱敏
     */
    private String objectUserInfoName;
    /**
     * 功能点
     * 如小A外呼-用户手机号查询
     * 建议采用：菜单栏-页面名称-功能按钮
     */
    private String functionPoint;
    /**
     * 数据接收方，
     * 如果为内部系统前端，与bussinessid 保持一致，若外发第三方与外发数据台账保持一致
     */
    private String dataReceive;
    /**
     * 备注（非必需）
     */
    private String note;

    public SecOperationLogVO(List<String> userKey, List<String> mobiles
            , String objectUserInfoMobile, String objectUserInfoBankNumber, String objectUserInfoIdNumber, String objectUserInfoName
            , String functionPoint) {
        String systemUserId = null;
        String systemUserName = null;
        // 获取登录人
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        if (principal != null) {
            Map map = JSONObject.parseObject(JSONObject.toJSONString(principal), Map.class);
            systemUserId = map.get("userName") != null ? map.get("userName").toString() : "";
            systemUserName = MD5Utils.md5Str(systemUserId);
        }
        this.systemUserId = StringUtils.defaultString(systemUserId);
        this.systemUserName = StringUtils.defaultString(systemUserName);
        this.eventTime = DateUtils.getCurrentDate(DateUtils.DATE_TIME_FORMAT_PATTERN);
        this.businessId = "core-fund";
        this.userKey = userKey;
        this.mobiles = mobiles;
        this.objectUserInfoMobile = objectUserInfoMobile;
        this.objectUserInfoBankNumber = objectUserInfoBankNumber;
        this.objectUserInfoIdNumber = objectUserInfoIdNumber;
        this.objectUserInfoName = objectUserInfoName;
        this.functionPoint = functionPoint;
        this.dataReceive = "core-fund";
        this.note = "";
    }

    @Getter
    public enum SecLogType {

        NOT("不包含", "0"),
        PLAINTEXT("包含&明文", "1"),
        CIPHERTEXT("包含&密文/脱敏", "2");

        private String name;
        private String value;

        SecLogType(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
