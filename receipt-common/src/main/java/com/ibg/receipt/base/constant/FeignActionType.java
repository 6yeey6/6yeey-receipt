package com.ibg.receipt.base.constant;

/***
 * 调用FeignClient的动作类型
 * 方便出现异常的时候,定位日志
 *
 */
public enum FeignActionType {
    PRE_BIND_CARD("预绑卡"),
    PRE_BIND_CARD_QUERY("预绑卡查询"),
    UPLOAD_FILE("上传文件"),
    APPLICATION_AUDIT("进件审核"),
    APPLICATION_AUDIT_QUERY("进件审核查询"),
    LOAN_AUDIT("放款审核"),
    LOAN_AUDIT_QUERY("放款审核查询"),
    LOAN_APPLY("放款申请"),
    LOAN_APPLY_QUERY("放款申请查询"),
    LOAN_TRY_CAL("放款试算"),
    REPAY_PLAN_QUERY("还款计划查询"),
    REPAY_TRY_CAL("还款试算"),
    LPR_QUERY("LPR查询");


    private final String string;

    private FeignActionType(String string) {
        this.string = string;
    }

    public static String out(FeignActionType  type){
        if(type!=null){
            return type.toString()+type.name();
        }
        return null;

    }
    @Override
    public String toString() {
        return this.string;
    }

    public static FeignActionType fromName(String string) {
        if (string != null) {
            for (FeignActionType temp : FeignActionType.values()) {
                if (string.equals(temp.name())) {
                    return temp;
                }
            }
        }

        return null;
    }
}
