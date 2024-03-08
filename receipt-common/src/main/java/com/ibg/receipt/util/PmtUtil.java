package com.ibg.receipt.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class PmtUtil {


    /**
     * 计算每期月供（每期总金额）
     *
     * @param rate 年利率 年利率除以12就是月利率
     * @param nper 贷款期数，单位月 该项贷款的付款总数。
     * @param pv   贷款金额,现值，或一系列未来付款的当前值的累积和，也称为本金。
     * @return
     */
    public static BigDecimal calculatePMT(BigDecimal rate, BigDecimal nper, BigDecimal pv) {
        BigDecimal v = BigDecimalUtils.add(new BigDecimal(1), BigDecimalUtils.divide(rate, new BigDecimal(12)));
        BigDecimal t = BigDecimalUtils.multiply(BigDecimalUtils.divide(nper, new BigDecimal(12)).negate(), new BigDecimal(12));
        BigDecimal monthRate = BigDecimalUtils.multiply(15, BigDecimalUtils.divide(rate, new BigDecimal(12)), pv);
        double v1 = 1 - Math.pow(v.doubleValue(), t.doubleValue());
        BigDecimal divide = BigDecimalUtils.divide(monthRate, new BigDecimal(v1)).setScale(2, RoundingMode.HALF_UP);
        return divide;
    }

    /**
     * 计算每期利息（按日计息）
     *
     * @param dayRate       日利率
     * @param startDay      放款日
     * @param dueDay        该期应还款日
     * @param leftPrinciple 期初剩余本金
     * @returnint
     */
    public static BigDecimal calculateInterest(BigDecimal dayRate, Date startDay, Date dueDay, BigDecimal leftPrinciple) {
        Integer dayDiff = DateUtils.getDayDiff(startDay, dueDay);
        BigDecimal multiply = BigDecimalUtils.multiply(leftPrinciple, dayRate);
        BigDecimal interest = BigDecimalUtils.multiply(leftPrinciple, dayRate, new BigDecimal(dayDiff)).setScale(2, RoundingMode.HALF_UP);
        return interest;
    }



}
