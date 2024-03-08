package com.ibg.receipt.base.exception.code;

/**
 * @author yushijun
 * @date 2019/12/23
 * @description 借款校验返回码-涉及放款对外接口
 *
 * 1.命名规则：业务首首字母_动作/业务_码值
 * 2.校验类错误码，做了统一，暂时不做细分：前置校验、没有落库数据
 * 3.校验已存在、幂等校验等错误码，需要单独新创建，以便调用方区分
 * 4.已落库数据，需要返回特定错误码，以便调用方识别
 */
public class LoanCode {
    /** 放款字段类型校验失败码-message自定义 */
    public static final Code L_CHECK_0001 = new Code("L_CHECK_0001", "%s");
    /** 银行卡相关 */
    public static final Code L_CARD_0001 = new Code("L_CARD_0001", "%s");
    /** 还款计划相关 */
    public static final Code L_PLAN_0001 = new Code("L_PLAN_0001", "%s");

    /** */
    public static final Code L_XW_0001 = new Code("L_XW_0001", "新网SDK异常-重试");

}
