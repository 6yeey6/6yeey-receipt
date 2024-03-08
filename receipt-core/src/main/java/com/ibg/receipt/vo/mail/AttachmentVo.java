package com.ibg.receipt.vo.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 邮件附件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentVo implements Serializable {

    private static final long serialVersionUID = 3693364562845237894L;

    /** 文件名 */
    private String fileName;
    /** 文件 */
    private byte[] fileBytes;

}
