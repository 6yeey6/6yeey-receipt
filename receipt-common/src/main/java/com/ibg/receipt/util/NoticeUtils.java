package com.ibg.receipt.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ibg.commons.wx.work.notice.starter.service.WxWorkNoticeStaticService;

/**
 * @author yushijun
 * @date 2018/8/15 报警整合工具
 */
public class NoticeUtils {


    /**
     *
     * 业务监控
     * @date:   2020年2月4日 下午6:01:47
     *
     * @param content
     */
    public static void businessWarn(String content) {
        WxWorkNoticeStaticService.wxNoticeSendInfo("业务监控：\n" + content, null, null);
    }
    /**
     *
     * 业务监控
     * @date:   2020年2月4日 下午6:01:47
     *
     * @param content
     */
    public static void businessWarn(String content, List<String> noticeUserName, List<String> noticePhone) {
        WxWorkNoticeStaticService.wxNoticeSendInfo("业务监控：\n" + content, noticeUserName, noticePhone);
    }
    /**
     *
     * 异常通知
     * @date:   2020年2月4日 下午6:01:47
     *
     * @param content
     */
    public static void businessError(String content) {
        WxWorkNoticeStaticService.wxNoticeSend("业务异常：\n" + content);
    }


    /**
     *
     * 异常通知
     * @date:   2020年2月4日 下午6:01:47
     *
     * @param content
     */
    public static void businessError(String content, List<String> noticeUserName, List<String> noticePhone) {
        WxWorkNoticeStaticService.wxNoticeWithRobotUrl(null,"业务异常：\n" + content, noticeUserName, noticePhone);
    }


    /**
     * 微信报警
     *
     * @param content
     *            报警内容
     *            @description 都调用businessWarn、businessError
     * @return
     */
    @Deprecated
    public static void workWxNotice(String topic, String content) {
        WxWorkNoticeStaticService.wxNoticeSend(topic + content);
    }

    /**
     * 微信报警，指定机器人
     * @param url
     * @param content
     * @param noticeUserName
     */
    public static void workWxNotice(String url, String content, List<String> noticeUserName){
        WxWorkNoticeStaticService.wxNoticeSendMessageWithUrl(url, content, noticeUserName);
    }

    /**
     * 集合不为空-微信报警
     *
     * @param content
     *            报警内容
     * @return
     */
    public static void ifNotEmptyNotice(Collection collection, String topic, String content) {
        if (CollectionUtils.isNotEmpty(collection)) {
            businessError(topic + content);
        }
    }


    /**
     * 如果obejct 为空微信报警
     *
     * @param content
     *            报警内容
     * @return
     */
    public static boolean ifGteNotice(Integer times, Integer targetTimes, String topic, String content) {
        if(times == null || targetTimes == null){
            return false;
        }
        if (times >= targetTimes) {
            businessError(topic + content);
            return true;
        }
        return false;
    }

    public static void wxNoticeSendMessage(String url, String content, List<String> noticeUserName) {
        WxWorkNoticeStaticService.wxNoticeSendMessageWithUrl(url, content, noticeUserName);
    }

    public static void wxNoticeSendImage(String url, String content, Map<String, Object> image) {
        WxWorkNoticeStaticService.wxNoticeSendImageWithUrl(url, content, image);
    }

    public static void wxNoticeSendNGroup(List<String> urls, String content, List<String> noticeUserName) {
        urls.stream().forEach(url -> {
            WxWorkNoticeStaticService.wxNoticeSendMessageWithUrl(url, content, noticeUserName);
        });
        businessError(content, noticeUserName, null);
    }
}
