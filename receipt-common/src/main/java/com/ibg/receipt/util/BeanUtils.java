package com.ibg.receipt.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

public class BeanUtils {
    //map转换为javaBean
    public static <T extends Object>  T transferMap2Bean(Map<String,Object> map, Class<T> clazz) throws Exception{
        T instance = clazz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : descriptors) {
            String key=property.getName();
            if(map.containsKey(key)){
                Object value = map.get(key);
                Method setter = property.getWriteMethod();
                setter.invoke(instance, value);
            }
        }
        return instance;
    }

    /**
     * object转换为map
     * 
     * @email xudong01@youxin.com
     * @date 2020/10/19
     */
    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        return new org.apache.commons.beanutils.BeanMap(obj);
    }
}
