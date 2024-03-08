package com.ibg.receipt.util;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

public class BigDecimalUtils {

    /** 默认精度，2位 */
    public static final int DEFAULT_PERCISION = 2;
    /** 利率精度，15位 */
    public static final int INTEREST_PERCISION = 15;
    /** 计算过程中精度，6位 */
    public static final int MIDDLE_PROCESS_PERCISION = 6;
    /** 默认四舍五入规则 */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    public static BigDecimal rounding(BigDecimal value) {
        Assert.notNull(value, "值");
        return value.setScale(DEFAULT_PERCISION, DEFAULT_ROUNDING);
    }

    /**
     * 校验精度
     *
     * @param value
     * @param scale
     * @return boolean
     */
    public static boolean validateScale(BigDecimal value, int scale) {
        Assert.notNull(value, "值");
        return value.stripTrailingZeros().scale() <= scale;
    }

    public static boolean isEquals(BigDecimal value1, BigDecimal value2) {
        Assert.notNull(value1, "值");
        Assert.notNull(value2, "值");
        if (value1.compareTo(value2) == 0) {
            return true;
        }
        return false;
    }

    /**
     * @Title: clearNoUseZeroForBigDecimal
     * @Description: 去掉BigDecimal尾部多余的0，通过stripTrailingZeros().toPlainString()实现
     * @param num
     * @return BigDecimal
     */
    public static BigDecimal clearNoUseZeroForBigDecimal(BigDecimal num) {
        BigDecimal returnNum = null;
        String numStr = num.stripTrailingZeros().toPlainString();
        if (numStr.indexOf(".") == -1) {
            // 如果num 不含有小数点,使用stripTrailingZeros()处理时,变成了科学计数法
            returnNum = new BigDecimal(numStr);
        } else {
            if (num.compareTo(BigDecimal.ZERO) == 0) {
                returnNum = BigDecimal.ZERO;
            } else {
                returnNum = num.stripTrailingZeros();
            }
        }
        return returnNum;
    }

    /**
     * 连乘
     *
     * @param decimals
     * @return
     */
    public static BigDecimal multiply(BigDecimal... decimals) {
        return multiply(DEFAULT_PERCISION, decimals);
    }

    /**
     * 连乘
     *
     * @param scale
     * @param decimals
     * @return
     */
    public static BigDecimal multiply(int scale, BigDecimal... decimals) {
        BigDecimal result = BigDecimal.ONE;
        for (BigDecimal decimal : decimals) {
            result = result.multiply(decimal);
        }
        return result.setScale(scale, DEFAULT_ROUNDING);
    }

    /**
     * 计算过程除,保留6位小数
     *
     * @param decimal
     *            总数
     * @param divisor
     *            除数
     * @return
     */
    public static BigDecimal divide(BigDecimal decimal, BigDecimal divisor) {
        return decimal.divide(divisor, BigDecimalUtils.MIDDLE_PROCESS_PERCISION, BigDecimalUtils.DEFAULT_ROUNDING);
    }

    /**
     * 计算过程除,保留6位小数
     *
     * @param decimal
     *            总数
     * @param divisor
     *            除数
     * @param scale
     *            精度
     * @return
     */
    public static BigDecimal divide(BigDecimal decimal, BigDecimal divisor,int scale) {
        return decimal.divide(divisor, scale, BigDecimalUtils.DEFAULT_ROUNDING);
    }

    /**
     * 如果expr1不是NULL，ifNull()返回expr1，否则它返回expr2
     *
     * @param expr1
     * @param expr2
     * @return
     */
    public static BigDecimal ifNull(BigDecimal expr1, BigDecimal expr2) {
        if (expr1 == null) {
            return expr2;
        }
        return expr1;
    }

    /**
     * 如果expr1不是NULL，ifNull()返回expr1，否则它返回0
     *
     * @param expr1
     * @return
     */
    public static BigDecimal ifNullDefaultZero(BigDecimal expr1) {
        return ifNull(expr1, BigDecimal.ZERO);
    }

    /**
     * 相加［如果为空，则按０处理］
     *
     * @param expr1
     * @param expr2
     * @return
     */
    public static BigDecimal add(BigDecimal expr1, BigDecimal expr2) {
        return ifNullDefaultZero(expr1).add(ifNullDefaultZero(expr2));
    }

    /**
     * 连加
     *
     * @param decimals
     * @return
     */
    public static BigDecimal add(BigDecimal... decimals) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal decimal : decimals) {
            result = result.add(ifNullDefaultZero(decimal));
        }
        return result;
    }

    /**
     * 相减［如果为空，则按０处理］
     *
     * @param expr1
     * @param expr2
     * @return
     */
    public static BigDecimal subtract(BigDecimal expr1, BigDecimal expr2) {
        return ifNullDefaultZero(expr1).subtract(ifNullDefaultZero(expr2));
    }

    public static BigDecimal intToBigDecimal(Integer expr1) {
        return Objects.nonNull(expr1) ? new BigDecimal(expr1) : null;
    }

    public static BigDecimal parseBigDecimal(String decimal, String paras) {
        try {
            return new BigDecimal(decimal);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101014, paras);
        }
    }

    public static BigDecimal parseBigDecimalWithBlankZero(String decimal, String paras) {
        try {
            if (StringUtils.isBlank(decimal)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(decimal);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101014, paras);
        }
    }

    public static BigDecimal parseWithDefault(String decimal, BigDecimal defaultVal) {
        if (StringUtils.isNotBlank(decimal)) {
            return new BigDecimal(decimal);
        } else {
            return defaultVal;
        }
    }

    /**
     * 元转换成分
     * @param decimal
     * @return
     */
    public static BigDecimal yuan2Cent(BigDecimal decimal){
        if (decimal == null) {
            return BigDecimal.ZERO;
        }
        return decimal.multiply(BigDecimal.valueOf(100)).setScale(0, DEFAULT_ROUNDING);
    }

    /**
     * 元转换成分
     *
     * @param decimal
     * @return
     */
    public static BigDecimal yuan2Cent(BigDecimal decimal, int scale) {
        if (decimal == null) {
            return BigDecimal.ZERO;
        }
        return decimal.multiply(BigDecimal.valueOf(100)).setScale(scale, DEFAULT_ROUNDING);
    }

    /**
     * 分转换成元
     * @param decimal
     * @return
     */
    public static BigDecimal cent2Yuan(BigDecimal decimal){
        if (decimal == null) {
            return BigDecimal.ZERO;
        }
        return decimal.divide(BigDecimal.valueOf(100), 2, DEFAULT_ROUNDING);
    }

    public static boolean isPositive(BigDecimal decimal){
        return decimal != null && decimal.compareTo(BigDecimal.ZERO) == 1;
    }

    public static boolean greaterThanZero(BigDecimal expr1) {
        return ifNullDefaultZero(expr1).compareTo(BigDecimal.ZERO) == 1 ? true : false;
    }

    public static boolean isEqualsZero(BigDecimal expr1) {
        return ifNullDefaultZero(expr1).compareTo(BigDecimal.ZERO) == 0 ? true : false;
    }

    public static boolean isEqualsWithDefaultZero(BigDecimal value1, BigDecimal value2) {
        if (ifNullDefaultZero(value1).compareTo(ifNullDefaultZero(value2)) == 0) {
            return true;
        }
        return false;
    }

	public static boolean greaterThan(BigDecimal expr1, BigDecimal expr2) {
        return ifNullDefaultZero(expr1).compareTo(ifNullDefaultZero(expr2)) == 1 ? true : false;
    }

    public static boolean greaterEqualThan(BigDecimal expr1, BigDecimal expr2) {
        return ifNullDefaultZero(expr1).compareTo(ifNullDefaultZero(expr2)) >= 0 ? true : false;
    }

    /**
     * 获取差值
     *
     * @param expr1
     * @param expr2
     * @return
     */
    public static double abs(BigDecimal expr1, BigDecimal expr2) {
        double abs = Math.abs(subtract(expr1,expr2).doubleValue());
        return abs;
    }

    /**
     * Object转BigDecimal
     * @param value
     * @return
     */
    public static BigDecimal getBigDecimal(Object value) {
        BigDecimal ret = null;
        if (value != null) {
            if (value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if (value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if (value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if (value instanceof Number) {
                ret = new BigDecimal(((Number) value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " into a BigDecimal.");
            }
        }
        return ret;
    }

    public static void main(String[] args) {

        BigDecimal a = new BigDecimal("0.28");
        BigDecimal b = new BigDecimal("0.03");
        System.out.println(abs(b,a)>0.08);
        System.out.println(abs(b,a)<0.08);
    }
}
