package com.ibg.receipt.config.haohuan;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 诺诺配置类
 */
@Getter
@Configuration
public class HaoHuanConfig {

    /**
     * 域名
     */
    @Value("${haohuan.basicUrl:http://haofenqi-audit-service.haohuan.com}")
    private String basicUrl;

    /**
     * appKey
     */
    @Value("${haohuan.method:/api/v1/protocol/getTotalByUniqId}")
    private String method;
}
