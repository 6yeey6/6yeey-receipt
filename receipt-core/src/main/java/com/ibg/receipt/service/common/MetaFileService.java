package com.ibg.receipt.service.common;

import com.ibg.receipt.config.fileSystem.FileSystemConfig;
import com.ibg.receipt.util.MetaFsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MetaFileService {
    @Autowired
    private FileSystemConfig fileSystemConfig;

    public String uploadToMetaFs(InputStream fileStream, String fileName, Long expireTime,
                                 String customType, String businessType) throws Exception {
        return MetaFsUtil.uploadToMetaFs(fileSystemConfig.getMetaFileSystemUrl(),fileSystemConfig.getSecret(),fileSystemConfig.getUniqueKey(),null, fileStream, fileName,
                expireTime, customType, businessType);

    }
}
