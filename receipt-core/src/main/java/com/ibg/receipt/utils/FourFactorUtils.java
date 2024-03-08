package com.ibg.receipt.utils;

import java.util.ArrayList;
import java.util.List;

import com.ibg.receipt.util.StringUtils;
import com.weicai.caesar.CaesarUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FourFactorUtils {

    public static List<String> getAllTexts(String text) {
        List<String> textList = new ArrayList<>();
        textList.add(text);
        if (isEncrypted(text)) {
            try {
                textList.add(CaesarUtil.decode(text));
            } catch (Exception e) {
                log.warn("解密文本失败", e);
            }
        } else {
            try {
                textList.add(CaesarUtil.encode(text));
            } catch (Exception e) {
                log.warn("加密文本失败");
            }
        }
        return textList;
    }

    public static boolean isEncrypted(String text) {
        try {
            return CaesarUtil.isEncrypted(text);
        } catch (Exception e) {
            log.warn("判断是否加密异常", e);
            return false;
        }
    }

    public static String encode(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        if (isEncrypted(text)) {
            return text;
        } else {
            return CaesarUtil.encode(text);
        }
    }

    public static String decode(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        if (isEncrypted(text)) {
            return CaesarUtil.decode(text);
        } else {
            return text;
        }
    }

}
