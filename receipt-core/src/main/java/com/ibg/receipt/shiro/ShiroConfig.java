package com.ibg.receipt.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/22 19:29
 */
@Configuration
public class ShiroConfig {

//    @Bean
//    public SecurityManager securityManager(RedisSessionDAO redisSessionDao) {
//        // 注意：这里的DefaultWebSecurityManager和我们之前的Demo使用的DefaultSecurityManager有区别
//        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        // 修改web环境下的默认sessionManager
//        DefaultWebSessionManager sessionManager = new ReceiptWebSessionManager();
//        // 60分钟(此设置会覆盖容器（tomcat）的会话过期时间设置)
//        sessionManager.setGlobalSessionTimeout(60 * 60 * 1000);
//        securityManager.setSessionManager(sessionManager);
//        // 禁用cookie来存sessionID(我们这里不用禁用，如果不传Token，则会使用原方式认证)
////        sessionManager.setSessionIdCookieEnabled(false);
//        Set<Realm> realms = new HashSet<>();
//        realms.add(userRealm());
//        securityManager.setRealms(realms);
//        return securityManager;
//    }

    @Bean
    public SecurityManager securityManager(RedisSessionDAO redisSessionDao) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        // 取消Cookie中的RememberMe参数
        securityManager.setRememberMeManager(null);
        securityManager.setSessionManager(defaultWebSessionManager(redisSessionDao));
        return securityManager;
    }

    @Bean
    public DefaultWebSessionManager defaultWebSessionManager(RedisSessionDAO redisSessionDao) {
        DefaultWebSessionManager sessionManager = new ReceiptWebSessionManager();
        sessionManager.setGlobalSessionTimeout(24 * 60 * 60 * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionDAO(redisSessionDao);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        /**
         * 修改Cookie中的SessionId的key，默认为JSESSIONID，自定义名称
         */
        sessionManager.setSessionIdCookie(new SimpleCookie("JSESSIONID"));
        return sessionManager;
    }
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public AuthorizingRealm userRealm() {

        PasswordMatcher passwordMatcher = new PasswordMatcher();
        passwordMatcher.setPasswordService(passwordService());

        AuthorizingRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(passwordMatcher);
        userRealm.setAuthorizationCachingEnabled(false);
        userRealm.setAuthenticationCachingEnabled(false);
        return userRealm;
    }

    @Bean
    public PasswordService passwordService() {
        return new ReceiptPasswordService();
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        LifecycleBeanPostProcessor processor = new LifecycleBeanPostProcessor();
        return processor;
    }

//    @Bean(name = "credentialsMatcher")
//    public CredentialsMatcher credentialsMatcher() {
//        return new SimpleCredentialsMatcher();
//    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, Filter> shiroFilterFactoryBeanFilters = shiroFilterFactoryBean.getFilters();
        // shiro的authc过滤器（注意这些过滤器是有顺序的，此map类型为LinkedHashMap）
        shiroFilterFactoryBeanFilters.put("authc",new ShiroUserFilter());
//        shiroFilterFactoryBeanFilters.put("authc", new ReceiptUserFilter());

        // 此处也应注意加入map的顺序
        Map<String, String> filterMap = new LinkedHashMap<String, String>();
        // 登出
        filterMap.put("/user/logout", "anon");
        filterMap.put("/user/login","anon");// 不认证登录地址
        filterMap.put("/user/check","anon");// 不认证登录地址
        filterMap.put("/user/register","anon");// 不认证登录地址
        filterMap.put("/health","anon");// 不认证登录地址
        filterMap.put("/user/organizationList","anon");// 不认证登录地址
        filterMap.put("/user/userList","anon");// 不认证登录地址
        filterMap.put("/creditor/config/creditorList","anon");// 不认证登录地址
        filterMap.put("/MANAGEMENT_PLATFORM/receiptChild/download/**","anon");// 不认证登录地址
        filterMap.put("/MANAGEMENT_PLATFORM/manage/redis/delete","anon");// 不认证登录地址
        filterMap.put("/manage/redis/delete","anon");// 不认证登录地址

        filterMap.put("/CUSTOMER_SYSTEM/**","anon");// 不认证登录地址
        filterMap.put("CUSTOMER_SYSTEM/**","anon");// 不认证登录地址
        filterMap.put("/job/offLine", "anon");// 不认证登录地址
        filterMap.put("/channel/offLine", "anon");// 不认证登录地址
        filterMap.put("/BUS_SYSTEM/order/loan/create", "anon");// 不认证登录地址
        filterMap.put("/BUS_SYSTEM/order/loan/query", "anon");// 不认证登录地址
        filterMap.put("/creditor/config/receiptItemCodeList","anon");//不认证登录地址
        filterMap.put("/manage/query/business/contract","anon");//不认证登录地址
        // 对所有用户授权认证
        filterMap.put("/**", "authc");
        //filterMap.put("/**", "anon");
        // authc对应shiroFilterFactoryBeanFilters的key值，logout和anon都是shrio自带的过滤器。

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        return shiroFilterFactoryBean;
    }
}
