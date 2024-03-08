package com.ibg.receipt.config.nuonuo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 诺诺配置类
 */
@Getter
@Configuration
public class NuoNuoConfig {

    /**
     * 域名
     */
    @Value("${nuonuo.basicUrl:https://sandbox.nuonuocs.cn/open/v1/services}")
    private String basicUrl;

    /**
     * 开票订单提醒企业微信地址
     */
    @Value("${nuonuo.robot.notice.url:http}")
    private String manualDboUrl;

    /**
     * 开票订单提醒开票链接
     */
    @Value("${nuonuo.robot.notice.link:http}")
    private String manualLink;
}
