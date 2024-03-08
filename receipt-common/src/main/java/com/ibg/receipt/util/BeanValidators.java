package com.ibg.receipt.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

/**
 * JSR303 Validator(Hibernate Validator)工具类.
 * ConstraintViolation中包含propertyPath, message 和invalidValue等信息.
 * 提供了各种convert方法，适合不同的i18n需求:
 * 1. List<String>, String内容为message
 * 2. List<String>, String内容为propertyPath + separator + message
 * 3. Map<propertyPath, message>
 * 详情见wiki: https://github.com/springside/springside4/wiki/HibernateValidator
 *
 * @author calvin
 */
public class BeanValidators {

    /**
     * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException,
     * 而不是返回constraintViolations.
     *
     * @param validator
     * @param object
     * @param groups
     * @throws ConstraintViolationException
     */
    public static void validateWithException(Validator validator,
            Object object, Class<?>... groups)
            throws ConstraintViolationException {
        Set constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    /**
     * 辅助方法,
     * 转换ConstraintViolationException中的Set<ConstraintViolations>中为List<message>.
     *
     * @param e
     * @return
     */
    public static List<String> extractMessage(ConstraintViolationException e) {
        return BeanValidators.extractMessage(e.getConstraintViolations());
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为List<message>
     *
     * @param constraintViolations
     * @return
     */
    public static List<String> extractMessage(
            Set<? extends ConstraintViolation> constraintViolations) {
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation violation : constraintViolations) {
            errorMessages.add(violation.getMessage());
        }
        return errorMessages;
    }

    /**
     * 辅助方法,
     * 转换ConstraintViolationException中的Set<ConstraintViolations>为Map<property,
     * message>.
     *
     * @param e
     * @return
     */
    public static Map<String, String> extractPropertyAndMessage(
            ConstraintViolationException e) {
        return BeanValidators.extractPropertyAndMessage(e
            .getConstraintViolations());
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为Map<property, message>.
     *
     * @param constraintViolations
     * @return
     */
    public static Map<String, String> extractPropertyAndMessage(
            Set<? extends ConstraintViolation> constraintViolations) {
        Map<String, String> errorMessages = new HashMap<>();
        for (ConstraintViolation violation : constraintViolations) {
            errorMessages.put(violation.getPropertyPath().toString(),
                violation.getMessage());
        }
        return errorMessages;
    }

    /**
     * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<
     * propertyPath message>.
     *
     * @param e
     * @return
     */
    public static List<String> extractPropertyAndMessageAsList(
            ConstraintViolationException e) {
        return BeanValidators.extractPropertyAndMessageAsList(
            e.getConstraintViolations(), " ");
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolations>为List<propertyPath message>.
     *
     * @param constraintViolations
     * @return
     */
    public static List<String> extractPropertyAndMessageAsList(
            Set<? extends ConstraintViolation> constraintViolations) {
        return BeanValidators.extractPropertyAndMessageAsList(
            constraintViolations, " ");
    }

    /**
     * 辅助方法, 转换ConstraintViolationException中的Set<ConstraintViolations>为List<
     * propertyPath + separator + message>.
     *
     * @param e
     * @param separator
     * @return
     */
    public static List<String> extractPropertyAndMessageAsList(
            ConstraintViolationException e, String separator) {
        return BeanValidators.extractPropertyAndMessageAsList(
            e.getConstraintViolations(), separator);
    }

    /**
     * 辅助方法, 转换Set<ConstraintViolation>为List<propertyPath + separator +
     * message>.
     *
     * @param constraintViolations
     * @param separator
     * @return
     */
    public static List<String> extractPropertyAndMessageAsList(
            Set<? extends ConstraintViolation> constraintViolations,
            String separator) {
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation violation : constraintViolations) {
            errorMessages.add(violation.getPropertyPath() + separator
                + violation.getMessage());
        }
        return errorMessages;
    }
}