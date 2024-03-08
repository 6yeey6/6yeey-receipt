package com.ibg.receipt.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class ChinaDate {

    private static final String DATE_FORMAT = "yyyyMMdd";
    /**
     * 国家法定假日，不包含普通的周六日，格式yyyyMMdd
     */
    private List<String> restDate = new ArrayList<>();
    /**
     * 周末调整为上班的工作日，不包含普通的工作日，格式yyyyMMdd
     */
    private List<String> workDate = new ArrayList<>();

    public static ChinaDate getInstance() {
        return ChinaDateHolder.chinaDate;
    }

    /**
     * 是否休息日
     *
     * @param d
     * @return
     */
    public boolean isRestDay(Date d) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();

        String format = localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        return restDate.contains(format)
                || (dayOfWeek.getValue() > 5 && !workDate.contains(format));
    }



    /**
     * 是否工作日
     *
     * @param d
     * @return
     */
    public boolean isWorkDay(Date d) {
        return !isRestDay(d);
    }

    private ChinaDate() {
        try {
            String url = "http://hao.360.cn/rili/";
            Connection c = Jsoup.connect(url).timeout(10_000).ignoreContentType(true);
            c.userAgent(
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
            Document d = c.get();
            Element dateRest = d.getElementById("dateRest");
            Element dateWork = d.getElementById("dateWork");
            String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
            List<String> restList = IOUtils.readLines(new ByteArrayInputStream(dateRest.text().getBytes()),
                    Charset.defaultCharset());
            restDate.addAll(restList.stream().map(s -> s.length() == 8 ? s : year + s).collect(Collectors.toList()));
            List<String> workList = IOUtils.readLines(new ByteArrayInputStream(dateWork.text().getBytes()),
                    Charset.defaultCharset());
            workDate.addAll(workList.stream().map(s -> s.length() == 8 ? s : year + s).collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("休息日工作日初始化失败", e);
        }
    }

    public ChinaDate(List<String> restDate, List<String> workDate) {
        this.restDate = restDate;
        this.workDate = workDate;
    }

    private static class ChinaDateHolder {
        static ChinaDate chinaDate = new ChinaDate();
    }

    public static void main(String[] args) {
        System.out.println(ChinaDate.getInstance().getWorkDate());
    }
}
