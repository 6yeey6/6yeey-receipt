package com.ibg.receipt.service.common;

import com.google.common.collect.Sets;
import com.ibg.receipt.base.constant.ProcessStatus;
import com.ibg.receipt.util.CollectionUtils;
import com.ibg.receipt.util.FileUtils;
import com.ibg.receipt.util.JsonUtils;
import com.ibg.receipt.util.StringUtils;
import com.ibg.receipt.vo.mail.AttachmentVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class MailSenderImpl implements MailSender {

    /** 分隔符 */
    public static final String COMMON_SEPARATOR = "|";

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /** 发件人 */
    @Value("${mail.from}")
    private String mailFrom;
    /** 邮件白名单是否启用 */
    @Value("${mail.white.list.enable}")
    private boolean mailWhiteListEnable;
    /** 邮件白名单 */
    @Value("${mail.white.list}")
    private String mailWhiteList;
    /** 邮件白名单列表 */
    private List<String> whiteList;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @PostConstruct
    public void init() {
        try {
            whiteList = StringUtils.splitToList(mailWhiteList, COMMON_SEPARATOR);
        } catch (Exception e) {
            log.error("MailSender初始化出错", e);
        }
    }

    @Override
    public ProcessStatus send(String[] to, String subject, String text) {
        return this.send(to, null, null, subject, text);
    }

    @Override
    public void sendEmailAsync(String[] to, String[] cc, String[] bcc, String subject, String text,
            AttachmentVo... attachments) {
        threadPool.execute(() -> send(to, cc, bcc, subject, text, attachments));
    }

    @Override
    public ProcessStatus send(String[] to, String[] cc, String[] bcc, String subject, String text,
            AttachmentVo... attachments) {
        List<File> files = null;
        try {

            to = filter(to);
            cc = filter(cc);
            bcc = filter(bcc);

            if (ArrayUtils.isEmpty(to)) {
                log.info("收件人列表为空，停止发送");
                return ProcessStatus.FAIL;
            }

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            log.info("mailFrom:{}",mailFrom);
            log.info("mailTo:{}", JsonUtils.toJson(to));
            helper.setTo(to);
            if (ArrayUtils.isNotEmpty(cc)) {
                helper.setCc(cc);
            }
            if (ArrayUtils.isNotEmpty(bcc)) {
                helper.setBcc(bcc);
            }
            helper.setSubject(subject);
            helper.setText(text, true);

            if (ArrayUtils.isNotEmpty(attachments)) {
                files = new ArrayList<File>();

                for (AttachmentVo attachment : attachments) {
                    //File file = new File(StringUtils.join(
                    //        new String[] { FileUtils.getTempDirectoryPath(), attachment.getFileName() },
                    //        File.separator));
                    //FileUtils.writeByteArrayToFile(file, attachment.getFileBytes());
                    //files.add(file);
                    //helper.addAttachment(attachment.getFileName(), file);
                    helper.addAttachment(attachment.getFileName(),()->new ByteArrayInputStream(attachment.getFileBytes()));
                }
            }
            try{
                mailSender.send(message);
            }catch (MailSendException e){
                log.error("邮件发送异常！",e);
                Set<String> tmpInvalidMails = getInvalidAddress(e);
                // 非无效收件人导致的异常，暂不处理
                if (tmpInvalidMails.isEmpty()) {
                    log.error("非无效收件人导致的异常，暂不处理");
                    return ProcessStatus.FAIL;
                }

                Set<String> toMails = Sets.difference(new HashSet<>(Arrays.asList(to)), tmpInvalidMails);
                Set<String> ccMails = Objects.isNull(cc) ? null : Sets.difference(new HashSet<>(Arrays.asList(cc)), tmpInvalidMails);
                Set<String> bccMails = Objects.isNull(bcc) ? null : Sets.difference(new HashSet<>(Arrays.asList(bcc)), tmpInvalidMails);
                if (CollectionUtils.isEmpty(toMails)) {
                    return ProcessStatus.FAIL;
                }
                helper.setTo(toMails.toArray(new String[toMails.size()]));
                if(CollectionUtils.isNotEmpty(ccMails)){
                    helper.setCc(ccMails.toArray(new String[ccMails.size()]));
                }
                if(CollectionUtils.isNotEmpty(bccMails)){
                    helper.setBcc(bccMails.isEmpty() ? null : (bccMails.toArray(new String[bccMails.size()])));
                }
                mailSender.send(message);
            }
        } catch (Exception e) {
            log.error("发送邮件出错", e);
            return ProcessStatus.FAIL;
        } finally {
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
        return ProcessStatus.SUCCESS;
    }

    private Set<String> getInvalidAddress(MailSendException e){
        Set<String> mails = new HashSet<>();
        for (Exception exception : e.getFailedMessages().values()) {
            if (exception instanceof SendFailedException) {
                if (Objects.nonNull(((SendFailedException) exception).getInvalidAddresses())) {
                    for (Address address : ((SendFailedException) exception).getInvalidAddresses()) {
                        mails.add(address.toString());
                    }
                }
            }
        }
        return mails;
    }

    /**
     * 过滤
     *
     * @param addresses
     * @return
     */
    private String[] filter(String[] addresses) {
        if (addresses == null) {
            return null;
        }
        if (!mailWhiteListEnable) {
            return addresses;
        }
        if (CollectionUtils.isEmpty(whiteList)) {
            return null;
        }

        List<String> list = new ArrayList<String>();

        for (String address : addresses) {
            if (whiteList.contains(address)) {
                list.add(address);
                continue;
            }
            log.info("邮件白名单中不包含 - [{}]", address);
        }

        if (list.isEmpty()) {
            return new String[] {};
        }

        return list.toArray(new String[] {});
    }

}
