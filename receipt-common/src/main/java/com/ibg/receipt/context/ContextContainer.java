package com.ibg.receipt.context;

import org.springframework.context.ApplicationContext;

public class ContextContainer {
    private static ApplicationContext ac = null;

    public static Object getBean(String beanName) {
        return ac.getBean(beanName);
    }

    public static Object getBean(Class beanName) {
        return ac.getBean(beanName);
    }


    public static <T> T getClassBean(Class<T> beanName) {
        return ac.getBean(beanName);
    }

    public static void setAc(ApplicationContext ac) {
        ContextContainer.ac = ac;
    }

    public static ApplicationContext getAc() {
        return ac;
    }

    public static String getActiveProfile() {
        return ac.getEnvironment().getActiveProfiles()[0];
    }
}
