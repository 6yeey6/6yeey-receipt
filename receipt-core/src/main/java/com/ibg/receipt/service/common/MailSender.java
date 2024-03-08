package com.ibg.receipt.service.common;


import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.vo.mail.AttachmentVo;

public interface MailSender {

    /**
     * 发送邮件
     *
     * @param to
     *            收件人
     * @param subject
     *            主题
     * @param text
     *            正文
     * @return
     */
    ProcessStatus send(String[] to, String subject, String text);

    /**
     * 发送邮件
     *
     * @param to
     *            收件人
     * @param cc
     *            抄送人
     * @param bcc
     *            密送人
     * @param subject
     *            主题
     * @param text
     *            正文
     * @param attachments
     *            附件
     * @return
     */
    ProcessStatus send(String[] to, String[] cc, String[] bcc, String subject, String text,
        AttachmentVo... attachments);

    /**
     * 发送邮件(异步)
     *
     * @param to
     *            收件人
     * @param cc
     *            抄送人
     * @param bcc
     *            密送人
     * @param subject
     *            主题
     * @param text
     *            正文
     * @param attachments
     *            附件
     * @return
     */
    void sendEmailAsync(String[] to, String[] cc, String[] bcc, String subject, String text,
        AttachmentVo... attachments);
}
