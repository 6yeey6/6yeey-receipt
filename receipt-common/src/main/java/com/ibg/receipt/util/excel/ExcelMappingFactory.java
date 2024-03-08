package com.ibg.receipt.util.excel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.ibg.receipt.base.exception.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ExcelMappingFactory {

    /** 类的表格信息缓存 */
    private final static LoadingCache<Class<?>, ExcelMapping> MAPPING_LOADING_CACHE = CacheBuilder.newBuilder()
        .maximumSize(5).build(new CacheLoader<Class<?>, ExcelMapping>() {
            @Override
            public ExcelMapping load(Class<?> key) {
                return loadExcelMappingByClass(key);
            }
        });

    /**
     * 获取类的表格信息
     *
     * @param clazz
     * @return
     */
    public static ExcelMapping get(Class<?> clazz) {
        try {
            return MAPPING_LOADING_CACHE.get(clazz);
        } catch (Exception e) {
            log.error("获取类[{}]的表格信息失败", clazz.getName(), e);
            throw ExceptionUtils.commonError(String.format("获取类[%s]的表格信息失败", clazz.getName()));
        }
    }

    /**
     * 通过注解加载类的表格信息
     *
     * @param clazz
     * @return
     */
    private static ExcelMapping loadExcelMappingByClass(Class<?> clazz) {
        try {
            List<ExcelProperty> propertyList = Lists.newArrayList();
            List<Field> fields = getDeclaredFields(clazz);
            for (Field field : fields) {
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                if (excelField == null) {
                    continue;
                }
                propertyList.add(ExcelProperty.builder()
                    .name(StringUtils.isBlank(excelField.name()) ? field.getName() : excelField.name())
                    .column(StringUtils.isBlank(excelField.value()) ? field.getName() : excelField.value())
                    .width(excelField.width()).dateFormat(excelField.dateFormat()).order(excelField.order()).build());
            }
            if (propertyList.isEmpty()) {
                throw ExceptionUtils.commonError(String.format("类[%s]的表格信息加载失败", clazz.getName()));
            }
            propertyList.sort((a, b) -> a.getOrder() - b.getOrder());
            return ExcelMapping.builder().propertyList(propertyList).build();
        } catch (Exception e) {
            log.error("类[{}]的表格信息加载失败", clazz.getName(), e);
            throw ExceptionUtils.commonError(String.format("类[%s]的表格信息加载失败", clazz.getName()));
        }
    }

    /**
     * 获取包括父类的所有声明的属性
     *
     * @param clazz
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> clazz) {
        List<Field> fieldList = Lists.newArrayList();
        while (clazz != null && !clazz.getName().toLowerCase().equals("java.lang.object")) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

}
