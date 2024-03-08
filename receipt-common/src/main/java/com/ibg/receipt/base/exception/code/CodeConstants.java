package com.ibg.receipt.base.exception.code;

/**
 * 错误码列表（注：命名以"C_"开头）
 */
public class CodeConstants {

    /** ---------- 系统 ---------- */
    public static final Code C_10101000 = new Code("10101000", "系统异常");
    public static final Code C_10101001 = new Code("10101001", "%s不能为空");
    public static final Code C_10101002 = new Code("10101002", "%s");
    public static final Code C_10101003 = new Code("10101003", "%s不能小于零");
    public static final Code C_10101004 = new Code("10101004", "%s枚举转化失败");
    public static final Code C_10101005 = new Code("10101005", "%s不存在");
    public static final Code C_10101006 = new Code("10101006", "%s不正确");
    public static final Code C_10101007 = new Code("10101007", "%s不能小于等于零");
    public static final Code C_10101008 = new Code("10101008", "%s已存在");
    public static final Code C_10101009 = new Code("10101009", "时间戳格式不对");
    public static final Code C_10101011 = new Code("10101011", "请勿重复提交");
    public static final Code C_10101012 = new Code("10101012", "[%s]长度必须在[%s]到[%s]之间");
    public static final Code C_10101013 = new Code("10101013", "%s不能大于%s");
    public static final Code C_10101014 = new Code("10101014", "[%s]数据格式不正确");
    public static final Code C_10101016 = new Code("10101016", "数据校验失败");
    public static final Code C_10121016 = new Code("10121016", "签名错误");
    public static final Code C_10121017 = new Code("10121017", "生成请求报文签名异常");
    public static final Code C_10121018 = new Code("10121018", "不可处理的逻辑分支");
    public static final Code C_10121045 = new Code("10121045", "金额精度不符合要求");
    public static final Code C_10121046 = new Code("10121046", "黑暗期，请稍后请求");
    public static final Code C_10121047 = new Code("10121047", "加密异常");
    public static final Code C_10121048 = new Code("10121048", "石嘴山-响应解密异常");
    public static final Code C_10121049 = new Code("10121049", "石嘴山-签名为空");
    public static final Code C_10121050 = new Code("10121050", "石嘴山-签名校验不一致");
    public static final Code C_10121051 = new Code("10121051", "石嘴山-token获取异常");
    public static final Code C_10121054 = new Code("10121054", "石嘴山-还款处理中");
    public static final Code C_10121055 = new Code("10121055", "南京银行-还款试算请求频繁");


    /** -----------还款----------- */
    public static final Code C_20101000 = new Code("20101000", "重复请求，该笔还款交易已存在");
    public static final Code C_20101001 = new Code("20101001", "申请还款金额与实际应还金额不一致");
    public static final Code C_20101002 = new Code("20101002", "还款指定的划扣卡未绑定，请先绑卡");
    public static final Code C_20101003 = new Code("20101003", "借款已还清，无需再次还款");
    public static final Code C_20101004 = new Code("20101004", "快捷支付还款验证码已失效");
    public static final Code C_20101005 = new Code("20101005", "快捷支付还款验证码校验失败");
    public static final Code C_20101007 = new Code("20101007", "系统正在清算中，请稍后发起还款");
    public static final Code C_20101008 = new Code("20101008", "申请还款期次与还款计划第一个要还期次不一致");
    public static final Code C_20101009 = new Code("20101009", "还款处理中");
    public static final Code C_20101010 = new Code("20101010", "该期次已结清");
    public static final Code C_20101011 = new Code("20101011", "已有处理中的还款订单,请稍后发起还款");
    public static final Code C_20101012 = new Code("20101012", "渤海线下还款-还款时间不能为空");
    public static final Code C_20101013 = new Code("20101013", "该笔还款交易已存在或正在处理中，请查询状态");
    public static final Code C_20101014 = new Code("20101014", "还款请求参数不能为空");
    public static final Code C_20101015 = new Code("20101015", "资金平台和业务方借款编号不能同时为空");
    public static final Code C_20101016 = new Code("20101016", "普通还款-要还款的期次不能为空");
    public static final Code C_20101017 = new Code("20101017", "支付渠道枚举payChannel错误");
    public static final Code C_20101018 = new Code("20101018", "代理还款处理中，请重试债转");
    public static final Code C_20101019 = new Code("20101019", "申请还款金额少于实际应还金额");
    public static final Code C_20101020 = new Code("20101020", "不存在未还期次");
    public static final Code C_20101021 = new Code("20101021", "该借款已结清");
    public static final Code C_20101022 = new Code("20101022", "出资方的试算服务异常，请您稍后再试，如需请联系好分期客服。");
    public static final Code C_20101023 = new Code("20101023", "出资方的还款服务异常，请您稍后再试，如需请联系好分期客服");
    public static final Code C_20101024 = new Code("20101024", "%s批扣中,请稍后还款");
    public static final Code C_20101025 = new Code("20101025", "还款明细大于1条");
    public static final Code C_20101030 = new Code("20101030", "无法进行提前结清操作，请明日再试");
    public static final Code C_20101031 = new Code("20101031", "还款人类型必填");
    public static final Code C_20101032 = new Code("20101032", "还款人手机号必填");
    public static final Code C_20101033 = new Code("20101033", "还款人类型为1时代还人姓名必填");
    public static final Code C_20101034 = new Code("20101034", "还款人类型为1时代还和本人关系必填");
    public static final Code C_20101035 = new Code("20101035", "还款人类型为1时代还理由必填");
    public static final Code C_20101036 = new Code("20101036", "还款金额必须大于0");
    public static final Code C_20101037 = new Code("20101037", "有日占息,则日占息金额必须大于0");
    public static final Code C_20101038 = new Code("20101038", "%s");
    public static final Code C_20101039 = new Code("20101039", "%s");

    /** --------放款 ---------- */
    public static final Code C_30101001 = new Code("30101001", "进件信息不存在");
    public static final Code C_30101002 = new Code("30101002", "该贷款申请已终止，不能放款！");
    public static final Code C_30101003 = new Code("30101003", "只允许对放款阶段的进件发起借款");
    public static final Code C_30101004 = new Code("30101004", "进件未审核通过，不能借款");
    public static final Code C_30101005 = new Code("30101005", "进件申请借款数据不存在");
    public static final Code C_30101006 = new Code("30101006", "签约日期不能为空");
    public static final Code C_30101007 = new Code("30101007", "借款期数与进件中的借款期数不一致");
    public static final Code C_30101008 = new Code("30101008", "还款卡未绑定");
    public static final Code C_30101009 = new Code("30101009", "借款信息已存在");
    public static final Code C_30101010 = new Code("30101010", "借款传的年利率与进件传的年利率不一致");
    public static final Code C_30101011 = new Code("30101011", "担保合同编号（担保)不能为空");
    public static final Code C_30101012 = new Code("30101012", "用信申请日期与放款申请日期不一致");
    public static final Code C_30101013 = new Code("30101013", "担保服务合同编号不能为空");
    public static final Code C_30101014 = new Code("30101014", "放款卡号与进件申请时不一致");
    public static final Code C_30101015 = new Code("30101015", "需要的文件数量与实际文件数量不一致");
    public static final Code C_30101016 = new Code("30101016", "借款进件号不一致");
    public static final Code C_30101017 = new Code("30101017", "进件对应的借款已存在");
    public static final Code C_30101018 = new Code("30101018", "借款未失败, 不允许二次放款");
    public static final Code C_30101020 = new Code("30101020", "借款合同金额不能小于借款金额");
    public static final Code C_30101021 = new Code("30101021", "资金平台和业务方进件号不能同时为空");
    public static final Code C_30101022 = new Code("30101022", "业务渠道编号枚举转化失败");
    public static final Code C_30101023 = new Code("30101023", "放款金额与进件申请时不一致");
    public static final Code C_30101024 = new Code("30101024", "放款卡用户姓名与进件不一致");
    public static final Code C_30101025 = new Code("30101025", "放款卡用户身份证号与进件不一致");
    public static final Code C_30101026 = new Code("30101026", "获取借款锁失败,请稍后重试");
    public static final Code C_30101027 = new Code("30101027", "额度过期");
    public static final Code C_30101028 = new Code("30101028", "放款卡未绑定");
    public static final Code C_30101029 = new Code("30101029", "放款卡与用户绑定成功最新的卡号不一致");
    public static final Code C_30101030 = new Code("30101030", "还款卡未绑定");
    public static final Code C_30101031 = new Code("30101031", "还款卡与用户绑定成功最新的卡号不一致");
    /** --------借款 ---------- */
    public static final Code C_40101001 = new Code("40101001", "[%s]尚不支持");
    public static final Code C_40101002 = new Code("40101002", "[%s]必须在[%s]到[%s]之间");
    public static final Code C_40101006 = new Code("40101006", "借款未放款loanKey=%s");
    public static final Code C_40101007 = new Code("40101007", "还款卡未绑定");

    /** --------绑卡 ---------- */
    public static final Code C_50101001 = new Code("50101001", "%s不能为空");

    /** --------复式记账 ---------- */
    public static final Code C_70101000 = new Code("70101000", "复式记账修改账户获取锁失败");


    /** --------资金路由 ---------- */
    public static final Code C_80101000 = new Code("80101000", "授信额度不可小于当前值[%s]");

    /** --------进件审核 ---------- */
    public static final Code C_90101000 = new Code("90101000", "项目内标识为互斥,客户只能有一笔放款");
    public static final Code C_90101001 = new Code("90101001", "已有上传文件处理中,请稍后");

    /** -------- 三方通信超时 ---------- */
    public static final Code C_90000001 = new Code("90000001", "通信超时，%s");


    public static final Code C_SENTINELERROR = new Code("sentinelError", "接口异常,异常");
    public static final Code C_SENTINELLIMIT = new Code("sentinelLimit", "接口异常,已降级请稍后重试");
    public static final Code C_60000002 = new Code("60000002", "接口异常,已限流请稍后重试");
    public static final Code C_60000003 = new Code("60000003", "接口异常,已参数限流请稍后重试");


    public static final Code C_99999999 = new Code("99999999", "用户名或者密码错误");
}
