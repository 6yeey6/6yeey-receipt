package com.ibg.receipt.config.fileSystem;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 文件系统
 */
@Getter
@Configuration
public class FileSystemConfig {

    /**
     * 域名
     */
    @Value("${file.system.url:http://haofenqi-audit-service.haohuan.com}")
    private String fileUrl;

    /**
     * 域名
     */
    @Value("${meta.file.system.url:http://file-system.test.weicai.com.cn/api/file/upload}")
    private String metaFileSystemUrl;

    /**
     * 域名
     */
    @Value("${meta.file.system.secret:qVdVHG8NiRbFc3Xx}")
    private String secret;

    /**
     * 域名
     */
    @Value("${meta.file.system.uniqueKey:invoice}")
    private String uniqueKey;
    /**
     * 域名
     */
    @Value("${meta.file.download.url:http://file-system.test.weicai.com.cn/api/file/download/}")
    private String metaFileDownloadSystemUrl;
    /**
     * 域名
     */
    @Value("${receipt.file.download.url:http://core-receipt-api-test-150.test.weicai.com.cn/MANAGEMENT_PLATFORM/receiptChild/download/}")
    private String receiptFileDownloadUrl;

    /**
     * 邮件组
     */
    @Value("${mail.to.group:fapiao@we.cn}")
    private String mailToGroup;
}
