package com.ibg.receipt.base.exception;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.alibaba.fastjson.JSONArray;
import com.ibg.receipt.base.exception.code.Code;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.model.BaseModel;
import com.ibg.receipt.util.BigDecimalUtils;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.StringUtils;

public class Assert {

    public static void notNull(Object obj, String paras) {
        if (obj == null) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notBlank(String str, String paras) {
        if (StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notBlank(Enum<?> str, String paras) {
        if (str == null) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notEmpty(Collection<?> coll, String paras) {
        if (CollectionUtils.isEmpty(coll)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notEmpty(Object[] array, String paras) {
        if (ArrayUtils.isEmpty(array)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void validStringLength(String str, int max, int min, boolean canEmpty, String paras) {
        if (!canEmpty && StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        } else if (StringUtils.isNotBlank(str)) {
            int length = str.length();
            if (max < length || min > length) {
                throw ServiceException.exception(CodeConstants.C_10101012, paras, min, max);
            }
        }
    }

    public static void isTrue(boolean flag, String paras) {
        if (!flag) {
            throw ServiceException.exception(CodeConstants.C_10101002, paras);
        }
    }

    public static void notExist(BaseModel model, String paras) {
        if (model == null) {
            throw ServiceException.exception(CodeConstants.C_10101005, paras);
        }
    }

    public static void notExist(Object model, String paras) {
        if (model == null) {
            throw ServiceException.exception(CodeConstants.C_10101005, paras);
        }
    }

    public static void exist(BaseModel model, String paras) {
        if (model != null) {
            throw ServiceException.exception(CodeConstants.C_10101008, paras);
        }
    }

    /**
     * 验证精度
     *
     * @param decimal
     * @param scale
     */
    public static void wrongScale(BigDecimal decimal, Integer scale) {
        notNull(decimal, "验证数值");
        notNull(scale, "验证精度");

        if (decimal.stripTrailingZeros().scale() > scale) {
            throw ServiceException.exception(CodeConstants.C_10121045);
        }
    }

    /**
     * 大于等于(ge)零
     *
     * @param decimal
     * @param paras
     */
    public static void geZero(BigDecimal decimal, String paras) {
        notNull(decimal, paras);
        if (decimal.compareTo(BigDecimal.ZERO) < 0) {
            throw ServiceException.exception(CodeConstants.C_10101003, paras);
        }
    }

    /**
     * 大于gt零
     *
     * @param decimal
     * @param paras
     */
    public static void gtZero(BigDecimal decimal, String paras) {
        notNull(decimal, paras);
        if (decimal.compareTo(BigDecimal.ZERO) <= 0) {
            throw ServiceException.exception(CodeConstants.C_10101007, paras);
        }
    }

    /**
     * 大于等于(ge)零
     *
     * @param integer
     * @param paras
     */
    public static void geZero(Integer integer, String paras) {
        notNull(integer, paras);
        if (integer < 0) {
            throw ServiceException.exception(CodeConstants.C_10101003, paras);
        }
    }

    /**
     * 大于(gt)零
     *
     * @param integer
     * @param paras
     */
    public static void gtZero(Integer integer, String paras) {
        notNull(integer, paras);
        if (integer < 0) {
            throw ServiceException.exception(CodeConstants.C_10101007, paras);
        }
    }

    /**
     * 小于等于(le)指定参数
     *
     * @param integer
     * @param paras
     */
    public static void leNum(Integer integer, int num, String paras) {
        notNull(integer, paras);
        if (integer > num) {
            throw ServiceException.exception(CodeConstants.C_10101013, paras, String.valueOf(num));
        }
    }

    /**
     * 枚举是否有效
     *
     * @param enumType
     *            枚举类型
     * @param name
     *            枚举name
     * @param paras
     */
    public static <T extends Enum<T>> T enumNotValid(Class<T> enumType, String name, String paras) {
        notBlank(name, paras);
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101004, paras);
        }
    }

    /**
     * 大于等于(ge)零
     *
     * @param decimal
     * @param message
     */
    public static void geZero(BigDecimal decimal, String code, String message) {
        notNull(decimal, message);
        if (decimal.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(code, message);
        }
    }
    public static void isDate(Long startDate, String paras) {
        notNull(startDate, paras);
        String dateStr = startDate + "";
        if (dateStr.length() != 13) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
        try {
            new Date(startDate);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
    }

    public static Date validDate(Long startDate, String paras) {
        notNull(startDate, paras);
        String dateStr = startDate + "";
        if (dateStr.length() != 13) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
        try {
            return new Date(startDate);
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101009);
        }
    }

    public static void inRange(Integer value, Integer min, Integer max, String paras) {
        notNull(value, paras);
        if (value.intValue() < min.intValue() || value.intValue() > max.intValue()) {
            throw ServiceException.exception(CodeConstants.C_40101002, paras, min.toString(), max.toString());
        }
    }

    public static void inRange(BigDecimal decimal, BigDecimal min, BigDecimal max, String paras) {
        gtZero(decimal, paras);
        if (decimal.compareTo(min) < 0 || decimal.compareTo(max) > 0) {
            throw ServiceException.exception(CodeConstants.C_40101002, paras, BigDecimalUtils.rounding(min).toString(),
                    BigDecimalUtils.rounding(max).toString());
        }
    }

    public static void validJsonArray(String str, boolean canEmpty, String paras) {
        if (!canEmpty && StringUtils.isBlank(str)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        } else if (StringUtils.isNotBlank(str)) {
            try {
                JSONArray.parse(str);
            } catch (Exception e) {
                throw ServiceException.exception(CodeConstants.C_10101002, paras + "无效的json格式");
            }
        }
    }

    public static void notEmpty(Map<?, ?> map, String paras) {
        if (MapUtils.isEmpty(map)) {
            throw ServiceException.exception(CodeConstants.C_10101001, paras);
        }
    }

    public static void notExist(BaseModel model, Code exceptionCode) {
        if (model == null) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notNull(Object obj, Code exceptionCode) {
        if (obj == null) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notBlank(String str, Code exceptionCode) {
        if (StringUtils.isBlank(str)) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notBlank(Enum<?> str, Code exceptionCode) {
        if (str == null) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notEmpty(Collection<?> coll, Code exceptionCode) {
        if (CollectionUtils.isEmpty(coll)) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notEmpty(Object[] array, Code exceptionCode) {
        if (ArrayUtils.isEmpty(array)) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void isTrue(boolean flag, Code exceptionCode) {
        if (!flag) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    /**
     * 枚举是否有效
     *
     * @param enumType
     *            枚举类型
     * @param name
     *            枚举name
     * @param paras
     */
    public static <T extends Enum<T>> T enumNotValid(Class<T> enumType, String name, Code exceptionCode) {
        notBlank(name, exceptionCode);
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception e) {
            throw ServiceException.exception(exceptionCode);
        }
    }

    public static void notBlank(String str, Code exceptionCode, Object... paras) {
        if (StringUtils.isBlank(str)) {
            throw ServiceException.exception(exceptionCode, paras);
        }
    }
}
