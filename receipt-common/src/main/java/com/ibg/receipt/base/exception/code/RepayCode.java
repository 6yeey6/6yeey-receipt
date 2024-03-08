package com.ibg.receipt.base.exception.code;

/**
 * @author yushijun
 * @date 2019/12/23
 * @description  还款相关错误码
 */
public class RepayCode {
    /** 还款字段类型校验失败码-message自定义 */
    public static final Code R_CHECK_0001 = new Code("R_CHECK_0001", "%s");
    public static final Code R_NOSUPPORT_0001 = new Code("R_NOSUPPORT_0001", "%s");

    /** 金额相关异常 */
    public static final Code R_AMOUNT_0001 = new Code("R_AMOUNT_0001", "%s");

}
