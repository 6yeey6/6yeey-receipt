package com.ibg.receipt.base.exception.code;

/**
 * @author yushijun
 * @date 2019/12/13
 * @description
 */
public class ServerCode {

    public static final Code S_SZS_502 = new Code("S_SZS_502", "石嘴山服务502");
    /** --------补偿相关异常 ---------- */
    public static final Code C_STATUS_0001 = new Code("C_STATUS_0001", "%");

    public static final Code S_LJ_SIGN_VERIFY_FAIL = new Code("S_LJ_001", "龙江返回报文验签失败");

    public static final Code C_BYCF_SIGN_VERIFY_FAIL = new Code("C_BYCF_001", "蒙商消金返回报文验签失败");

    public static final Code C_CYCF_SIGN_VERIFY_FAIL = new Code("C_CYCF_001", "长银消金返回报文验签失败");
}
