package com.ibg.receipt.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibg.receipt.base.exception.Assert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FinanceUtils {

    /**
     * 一年12个月
     */
    public static final BigDecimal MONTH_OF_YEAR = new BigDecimal("12");
    /**
     * 默认精度，2位
     */
    public static final int DEFAULT_PERCISION = 2;
    /**
     * 计算精度，7位
     */
    public static final int CALCULATE_PERCISION = 7;
    /**
     * 默认四舍五入规则
     */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    /**
     *
     * 等额本息试算
     *
     * @param amount
     * @param yearInterest
     * @param periods
     * @return
     */
    public static List<MonthlyRepay> calcDEBX(BigDecimal amount, BigDecimal yearInterest, int periods) {
        List<MonthlyRepay> list = new ArrayList<>();
        BigDecimal monthlyRate = yearInterest.divide(new BigDecimal("12"), 7, RoundingMode.HALF_UP);
        BigDecimal leftPrincipal = amount;
        BigDecimal amtPerMonth = calcMonthlyRepayTotal(amount, yearInterest, periods);
        BigDecimal monthInterest;
        BigDecimal monthPrincipal;

        for (int i = 1; i < periods + 1; i++) {
            monthInterest = leftPrincipal.multiply(monthlyRate).setScale(2,
                    RoundingMode.HALF_UP);
            monthPrincipal = amtPerMonth.subtract(monthInterest);
            leftPrincipal = leftPrincipal.subtract(monthPrincipal);
            //最后一期找平
            if (i == periods) {
                monthPrincipal = monthPrincipal.add(leftPrincipal);
                leftPrincipal = BigDecimal.ZERO;
            }
            list.add(new MonthlyRepay(i, monthPrincipal.setScale(2, RoundingMode.HALF_UP), monthInterest,
                    monthPrincipal.add(monthInterest).setScale(2, RoundingMode.HALF_UP),
                    leftPrincipal.setScale(2, RoundingMode.HALF_UP),""));
        }

        return list;
    }


    /**
     *
     * 等额本息试算(爱建)
     *
     * @param amount
     * @param yearInterest
     * @param periods
     * @return
     */
    public static List<MonthlyRepay> calcDEBXByMonth(BigDecimal amount, BigDecimal yearInterest, int periods,
                                                     LocalDate loanDate) {
        List<MonthlyRepay> list = new ArrayList<>();
        BigDecimal monthlyRate = yearInterest.divide(new BigDecimal("12"), 7, RoundingMode.HALF_UP);
        BigDecimal leftPrincipal = amount;
        BigDecimal amtPerMonth = calcMonthlyRepayTotal(amount, yearInterest, periods);
        BigDecimal monthInterest;
        BigDecimal monthPrincipal;
        LocalDate startDate = loanDate == null ? LocalDate.now() : loanDate;
        for (int i = 1; i < periods + 1; i++) {
            monthInterest = leftPrincipal.multiply(monthlyRate).setScale(2,
                RoundingMode.HALF_UP);
            monthPrincipal = amtPerMonth.subtract(monthInterest);
            leftPrincipal = leftPrincipal.subtract(monthPrincipal);
            //最后一期找平
            if (i == periods) {
                monthPrincipal = monthPrincipal.add(leftPrincipal);
                leftPrincipal = BigDecimal.ZERO;
            }
            String repayDate = startDate.plusMonths(i).toString();
            list.add(new MonthlyRepay(i, monthPrincipal.setScale(2, RoundingMode.HALF_UP), monthInterest,
                monthPrincipal.add(monthInterest).setScale(2, RoundingMode.HALF_UP),
                leftPrincipal.setScale(2, RoundingMode.HALF_UP), repayDate));

        }
        return list;
    }

    private BigDecimal calcTotalMonthAmount(BigDecimal totalRateByMonth,Integer period,BigDecimal amount){
        return amount.multiply((totalRateByMonth.multiply((BigDecimal.ONE.add(totalRateByMonth)).pow(period))))
            .divide((BigDecimal.ONE.add(totalRateByMonth).pow(period).subtract(BigDecimal.ONE)),2, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal calcMonthlyRepayTotal(BigDecimal amount, BigDecimal yearRate, int periods) {
        BigDecimal monthlyInterest = yearRate.divide(new BigDecimal("12"), 7, RoundingMode.HALF_UP);
        BigDecimal pow = (new BigDecimal("1").add(monthlyInterest)).pow(periods).setScale(12, RoundingMode.HALF_UP);
        BigDecimal amtPerMonth = amount.multiply(monthlyInterest.multiply(pow))
                .divide(pow.subtract(new BigDecimal("1")), 2, RoundingMode.HALF_UP);
        return amtPerMonth;
    }

    /**
     * 计算等额本息的月还本息
     *
     * @param amount        金额
     * @param months        期限
     * @param interestRatio 利率
     * @param roundingMode  四舍五入规则
     * @param finalScale    最终保留位数
     * @return 月还本息 = 本金 + 利息
     */
    public static BigDecimal calculateMonthlyRepayAmountForDEBX(BigDecimal amount, int months, BigDecimal interestRatio,
        RoundingMode roundingMode, Integer finalScale) {
        return calculateMonthlyRepayAmountForDEBX(amount, months, interestRatio, roundingMode, CALCULATE_PERCISION,
            finalScale);
    }

    public static BigDecimal calculateMonthlyRepayAmountForDEBX(BigDecimal amount, int months, BigDecimal interestRatio,
        RoundingMode roundingMode, Integer calculateScale, Integer finalScale) {
        Assert.notNull(amount, "金额");
        Assert.geZero(months, "期限");
        Assert.notNull(interestRatio, "利率");
        if (roundingMode == null) {
            roundingMode = DEFAULT_ROUNDING;
        }
        if (calculateScale == null) {
            calculateScale = CALCULATE_PERCISION;
        }
        if (finalScale == null) {
            finalScale = DEFAULT_PERCISION;
        }

        // 月利率
        BigDecimal monthlyInterestRatio = interestRatio.divide(MONTH_OF_YEAR, calculateScale, roundingMode);
        // pow
        BigDecimal pow = BigDecimal.ONE.add(monthlyInterestRatio).pow(months).setScale(calculateScale, roundingMode);
        // 每期本息 = 本金 + 利息 = [金额 × 利率 / 12 × （ 1 + 利率 / 12） ^ 期数] ÷ [（1 + 利率 / 12） ^ 期数 - 1]
        return amount.multiply(monthlyInterestRatio).multiply(pow)
            .divide(pow.subtract(BigDecimal.ONE), finalScale, roundingMode);
    }

    /**
    *
    * 等额本息试算
    *
    * @param amount
    * @param yearInterest
    * @param periods
    * @param firstRepayDate - 首期还款日
    * @param loanDate - 放款日期
    * @return
    */
    public static List<MonthlyRepay> calcDEBXByDay(BigDecimal amount, BigDecimal yearInterest, int periods,
            LocalDate loanDate, LocalDate firstRepayDate) {
        List<MonthlyRepay> list = new ArrayList<>();
        BigDecimal dailyRate = yearInterest.divide(BigDecimal.valueOf(360), 7, RoundingMode.HALF_UP);
        BigDecimal leftPrincipal = amount;
        BigDecimal amtPerMonth = calcMonthlyRepayTotal(amount, yearInterest, periods);
        BigDecimal monthInterest;
        BigDecimal monthTotal;
        BigDecimal monthPrincipal;
        LocalDate repayDate = firstRepayDate;
        LocalDate startDate = loanDate == null ? LocalDate.now() : loanDate;

        for (int i = 1; i < periods + 1; i++) {
            long interestDays = ChronoUnit.DAYS.between(startDate, repayDate);
            monthInterest = leftPrincipal.multiply(dailyRate).multiply(BigDecimal.valueOf(interestDays)).setScale(2,
                    RoundingMode.HALF_UP);
            monthTotal = amtPerMonth.subtract(leftPrincipal.multiply(dailyRate).multiply(BigDecimal.valueOf(30)))
                    .add(monthInterest).setScale(2, RoundingMode.HALF_UP);
            monthPrincipal = i == 1 ? monthTotal.subtract(monthInterest) : amtPerMonth.subtract(monthInterest);
            leftPrincipal = leftPrincipal.subtract(monthPrincipal);
            // 最后一期找平
            if (i == periods) {
                monthPrincipal = monthPrincipal.add(leftPrincipal);
                leftPrincipal = BigDecimal.ZERO;
            }
            list.add(new MonthlyRepay(i, monthPrincipal.setScale(2, RoundingMode.HALF_UP), monthInterest,
                    monthPrincipal.add(monthInterest).setScale(2, RoundingMode.HALF_UP),
                    leftPrincipal.setScale(2, RoundingMode.HALF_UP), repayDate.toString()));
            startDate = repayDate;
            repayDate = repayDate.plusMonths(1);
        }

        return list;
    }

    /**
     * 等额本息还款
     * <p>纯对日，月利息计算按日利率乘以自然天数计算</p>
     * <p>不支持指定还款日</p>
     * @param amount 借款总额
     * @param yearInterest 年利率
     * @param periods 借款期数
     * @param loanDate  借款日期
     * @param calculateScale  利率保留小数
     * @param finalScale 结果保留小数
     * @return
     */
    public static List<MonthlyRepay> calcDEBXByDay(BigDecimal amount, BigDecimal yearInterest, int periods,
                                                     LocalDate loanDate, Integer calculateScale, Integer finalScale){
        List<MonthlyRepay> list = new ArrayList<>();
        BigDecimal dailyRate = yearInterest.divide(BigDecimal.valueOf(360), calculateScale, RoundingMode.HALF_UP);
        BigDecimal leftPrincipal = amount;
        BigDecimal monthRepay = calculateMonthlyRepayAmountForDEBX(amount, periods, yearInterest, RoundingMode.HALF_UP, calculateScale, finalScale);
        BigDecimal monthInterest;
        BigDecimal monthPrincipal;
        LocalDate repayDate;
        LocalDate startDate = loanDate == null ? LocalDate.now() : loanDate;
        LocalDate realLoanDate = loanDate == null ? LocalDate.now() : loanDate;

        for (int i = 1; i < periods + 1; i++) {
            repayDate = realLoanDate.plusMonths(i);
            long interestDays = ChronoUnit.DAYS.between(startDate, repayDate);
            //月还款利息
            monthInterest = leftPrincipal.multiply(dailyRate).multiply(BigDecimal.valueOf(interestDays))
                .setScale(finalScale, RoundingMode.HALF_UP);
            //月还款本金
            monthPrincipal = monthRepay.subtract(monthInterest);
            //剩余本金
            leftPrincipal = leftPrincipal.subtract(monthPrincipal);
            // 最后一期找平
            if (i == periods) {
                monthPrincipal = monthPrincipal.add(leftPrincipal);
                leftPrincipal = BigDecimal.ZERO;
            }
            list.add(new MonthlyRepay(i, monthPrincipal.setScale(finalScale, RoundingMode.HALF_UP), monthInterest,
                monthPrincipal.add(monthInterest).setScale(finalScale, RoundingMode.HALF_UP),
                leftPrincipal.setScale(finalScale, RoundingMode.HALF_UP), repayDate.toString()));
            startDate = repayDate;
        }

        return list;
    }

    public static List<MonthlyRepay> calcDEBXByMonthWithEndDate(BigDecimal amount, BigDecimal yearInterest, int periods,
                                                                LocalDate loanDate,int endDay) {
        List<MonthlyRepay> list = new ArrayList<>();
        BigDecimal monthlyRate = yearInterest.divide(new BigDecimal("12"), 7, RoundingMode.HALF_UP);
        BigDecimal leftPrincipal = amount;
        BigDecimal amtPerMonth = calcMonthlyRepayTotal(amount, yearInterest, periods);
        BigDecimal monthInterest;
        BigDecimal monthPrincipal;
        LocalDate startDate = loanDate == null ? LocalDate.now() : loanDate;
        for (int i = 1; i < periods + 1; i++) {
            monthInterest = leftPrincipal.multiply(monthlyRate).setScale(2,
                RoundingMode.HALF_UP);
            monthPrincipal = amtPerMonth.subtract(monthInterest);
            leftPrincipal = leftPrincipal.subtract(monthPrincipal);
            //最后一期找平
            if (i == periods) {
                monthPrincipal = monthPrincipal.add(leftPrincipal);
                leftPrincipal = BigDecimal.ZERO;
            }
            //亲家特殊逻辑，还款日不能晚于endDay
            String repayDate = String.valueOf(DateUtils.getEndDateByEndDay(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                i,endDay).getTime());

            list.add(new MonthlyRepay(i, monthPrincipal.setScale(2, RoundingMode.HALF_UP), monthInterest,
                monthPrincipal.add(monthInterest).setScale(2, RoundingMode.HALF_UP),
                leftPrincipal.setScale(2, RoundingMode.HALF_UP), repayDate));

        }
        return list;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRepay {
        private int period;
        private BigDecimal principal;
        private BigDecimal interest;
        private BigDecimal total;
        private BigDecimal leftPrincipal;
        private String repayDate;
    }

    private FinanceUtils() {
    }

}
