package com.ibg.receipt.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 日期工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = ONE_SECOND * 60;
    public static final int ONE_HOUR = ONE_MINUTE * 60;
    public static final int ONE_DAY = ONE_HOUR * 24;
    public static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    public static String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static String TIME_FORMAT_PATTERN = "HH:mm:ss";
    public static String TIME_FORMAT_PATTERN_COLON = "HHmmss";
    public static String DATE_FORMAT_SLASH = "yyyy/MM/dd";
    public static String DATE_FORMAT_PATTERN_NO_HYPHEN = "yyyyMMdd";
    public static String DATE_FORMAT_PATTERN_PART_NO_HYPHEN = "yyyyMMddHH";
    public static String DATE_FORMAT_PATTERN_YYYYMM = "yyyyMM";
    public static String DATE_FORMAT_PATTERN_FOR_DOT = "yyyy.MM.dd";
    public static String DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_TIME_NO_BLANK_FORMAT = "yyyyMMddHHmmss";
    public static String DATE_TIME_UTC_FORMAT = "yyyyMMddHHmmssSSS";
    public static String DATE_TIME_FORMAT = "YYYY-MM-DDHH:mm:SS";
    public static String DATE_TIME_UTC="yyyy-MM-dd'T'HH:mm:ss.SSS+0800";
    public static String DATE_FORMAT_CHN_YMD = "yyyy年MM月dd日";
    public static String DATE_TIME_FORMAT_PATTERN_2 = "YYYYMMDD HH:MM:SS";
    public static String DATE_FORMAT_PATTERN_NO_HYPHEN1 = "yyyyMMdd HH:mm:ss";

    /**
     * 设置为某日期的开始00:00:00
     *
     * @param date
     * @return
     */
    public static Date setDateBegin(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public  static  Date getEarliestDate(Date date1,Date date2){
        return DateUtils.differenceDays(date1, date2)>0?date2:date1;

    }
    public static Date formatToDate(String dateStr,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date;
        } catch (ParseException e) {
            System.out.println("日期格式转换异常!"+e.fillInStackTrace());
            return null;
        }
    }

    /**
     * 设置为某日期的结束23:59:59
     *
     * @param date
     * @return
     */
    public static Date setDateEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 设置为某日期的结束hour:minute:second
     *
     * @param date
     * @return
     */
    public static Date setDateTime(Date date,int hour,int minute,int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        return c.getTime();
    }

    /**
     * 获取指定月的同一天
     *
     * @param date 当前时间
     * @param i 设置为上一个月      +为后一个月  0 为本月
     * @param day 某个月的几号  小于等于0跳过
     * @return
     */
    public static Date getMonthDateTime(Date date,int i,int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date); // 设置为当前时间

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) +i); // 设置为上一个月      +为后一个月  0 为本月
        if(day>0){
            calendar.set(Calendar.DAY_OF_MONTH,day);
        }
        return calendar.getTime();
    }

    /**
     * 得到对应月的第一天
     *
     * @return String
     */
    public static Date getMonthFristDay(int month) {
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.MONTH, month);
        cld.set(Calendar.DAY_OF_MONTH, 1);
        cld.set(Calendar.HOUR_OF_DAY, 0);
        cld.set(Calendar.MINUTE, 0);
        cld.set(Calendar.SECOND, 0);
        cld.set(Calendar.MILLISECOND, 0);
        return cld.getTime();
    }


    /**
     * 得到对应月的第一天
     *
     * @return String
     */
    public static Date getMonthEndDay(int month) {
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.MONTH, month);
        cld.set(Calendar.DAY_OF_MONTH, cld.getActualMaximum(Calendar.DAY_OF_MONTH));
        cld.set(Calendar.HOUR_OF_DAY, 23);
        cld.set(Calendar.MINUTE, 59);
        cld.set(Calendar.SECOND, 59);
        cld.set(Calendar.MILLISECOND, 999);

        return cld.getTime();
    }




    /**
     * 得到本月对应的第一天
     *
     * @return String
     */
    public static String getMonthFristDay() {
        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = dft.format(cld.getTime());
        return firstDay;
    }

    /**
     * 得到本月对应的最后一天
     *
     * @return String
     */
    public static String getMonthEndDay() {
        Calendar cld = Calendar.getInstance();
        cld.add(Calendar.MONTH, 1);
        cld.set(Calendar.DAY_OF_MONTH, 0);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String endDay = dft.format(cld.getTime());
        return endDay;
    }

    /**
     * 得到指定月的第一天
     *
     * @return String
     */
    public static String getFristDayInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = dft.format(calendar.getTime());
        return firstDay;
    }

    /**
     * 得到指定月的最后一天
     *
     * @return String
     */
    public static String getEndDayInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 0);
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        String endDay = dft.format(calendar.getTime());
        return endDay;
    }

    /**
     * 得到指定月的最后一天
     *
     * @return String
     */
    public static String getEndDayInMonth(int year, int month, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 0);
        SimpleDateFormat dft = new SimpleDateFormat(format);
        String endDay = dft.format(calendar.getTime());
        return endDay;
    }

    /**
     * 获得指定格式的日期字符串
     *
     * @return
     */
    public static String getCurrentDate(String format) {
        String dateString = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            Date currentTime_1 = new Date();
            dateString = formatter.format(currentTime_1);
        } catch (Exception e) {
        }
        return dateString;
    }

    /**
     * 转换时间字符串为日期和时间
     *
     * @param s
     * @return
     */
    public static Date getFormatTime(String s) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            return simpleDateFormat.parse(s);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 获取后几天的日期yyyy-MM-dd 00:00:00
     *
     * @param num
     * @return
     */
    public static Date getNextDayDate(Date cDate, int num) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(cDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        date.set(Calendar.DATE, date.get(Calendar.DATE) + num);
        String nextDate = dft.format(date.getTime()) + " 00:00:00";
        Date ndate = null;
        try {
            ndate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ndate;
    }

    /**
     * 获取后几天的日期指定小时的时间戳
     *
     * @param num
     * @return
     */
    public static Long getNextDayDateAndHour(Date cDate, int num,int hour) {
        Date nextDayDate = getNextDayDate(cDate, num);
        nextDayDate.setHours(hour);
        long time = nextDayDate.getTime();
        return time;
    }


    /**
     * 获取后几天的标准日期yyyy-MM-dd HH:mm:ss
     *
     * @param num
     * @return
     */
    public static Date getNextDayRealDate(Date cDate, int num) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar date = Calendar.getInstance();
        try {
            date.setTime(cDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        date.set(Calendar.DATE, date.get(Calendar.DATE) + num);
        String nextDate = dft.format(date.getTime());
        Date ndate = null;
        try {
            ndate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nextDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ndate;
    }



    /**
     * 格式化日期
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatDate(Date date, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前月天数
     *
     * @return
     */
    public static int getCurMonthDays() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 转换日期，转换失败时返回null
     *
     * @param str
     * @param parsePatterns
     * @return
     */
    public static Date parse(String str, String parsePatterns) {
        try {
            return parseDate(str, parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }


    /**
     * @author: FengQing
     * @Description: 格式化日期 （yyyyMMdd）：String To Date
     * @date: 2018年9月28日 下午2:14:33
     * @param str
     * @return
     */
    public static Date parse4NoHyphen(String str) {
        try {
            return DateUtils.parseDate(str, DateUtils.DATE_FORMAT_PATTERN_NO_HYPHEN);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     *
     * @author: FengQing
     * @Description: 格式化日期 （yyyyMMdd）：Date To String
     * @date:   2018年9月29日 下午3:31:42
     *
     * @param date
     * @return
     */
    public static String format4NoHyphen(Date date) {
        return format(date, DateUtils.DATE_FORMAT_PATTERN_NO_HYPHEN);
    }

    /**
     * @author: zhangshuwei01
     * @Description: 格式化日期 （yyyy.MM.dd）：String To Date
     * @param str
     * @return
     */
    public static Date parse4Dot(String str) {
        try {
            return DateUtils.parseDate(str, DateUtils.DATE_FORMAT_PATTERN_FOR_DOT);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     *
     * @author: zhangshuwei01
     * @Description: 格式化日期 （yyyy.MM.dd）：Date To String
     * @date:   2018年9月29日 下午3:31:42
     *
     * @param date
     * @return
     */
    public static String format4Dot(Date date) {
        return format(date, DateUtils.DATE_FORMAT_PATTERN_FOR_DOT);
    }

    public static String format(Date date, String parsePatterns) {
        return new SimpleDateFormat(parsePatterns).format(date);
    }

    /**
     * 日期(yyyy-MM-dd)
     *
     * @param date
     * @return
     */
    public static Date parseSimpleDate(Date date) {
        String pattern = "yyyy-MM-dd";
        try {
            return parseDate(DateFormatUtils.format(date, pattern), pattern);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 相差天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static Integer differenceDays(Date date1, Date date2) {
        date1 = parseSimpleDate(date1);
        date2 = parseSimpleDate(date2);
        return (int) ((date1.getTime() - date2.getTime()) / ONE_DAY);
    }

    /**
     * 日期date1早于日期date2
     * @param date1
     * @param date2
     * @return true-date1早于date2
     */
    public static boolean before(Date date1, Date date2){
        return differenceDays(date1, date2) < 0;
    }

    /**
     * 日期date1晚于日期date2
     * @param date1
     * @param date2
     * @return true date1晚于date2
     */
    public static boolean after(Date date1, Date date2){
        return differenceDays(date1, date2) > 0;
    }

    /**
     * 标准相差天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static Integer getDayDiff(Date date1, Date date2) {
        return Math.abs((int) ((date1.getTime() - date2.getTime()) / ONE_DAY));
    }

    public static Date getFormatMiddleDate(Date date) {
        String dateStr = DateUtils.format(date, "yyyy-MM-dd") + " 12:00:00";
        return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date ifnullGet(Date date1, Date date2) {
        return date1 == null ? date2 : date1;
    }

    /**
     * 获取日期
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 设置日期
     *
     * @param date
     * @param day
     * @return
     */
    public static Date setDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 得到指定月的最后一天
     *
     * @return String
     */
    public static Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }

    public static Integer getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static Integer getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取延迟秒数时间
     *
     * @param delaySeconds
     * @return
     */
    public static Date getDelayDate(int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, delaySeconds);
        return cal.getTime();
    }

    public static Date getDelayDate(Date date, int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, delaySeconds);
        return cal.getTime();
    }

    /**
     * 判断两个时间段是否有交集
     * @param startDate
     * @param endDate
     * @param targetStartDate
     * @param targetEndDate
     * @return
     */
    public static boolean dateHasIntersection(Date startDate, Date endDate, Date targetStartDate, Date targetEndDate) {
        // 对start 和end 进行排序 start > end
        long startA = startDate.getTime() - endDate.getTime() < 0 ? startDate.getTime() : endDate.getTime();
        long endA = endDate.getTime() - startDate.getTime() > 0 ? endDate.getTime() : startDate.getTime();
        long startB = targetStartDate.getTime() - targetEndDate.getTime() < 0 ? targetStartDate.getTime()
                : targetEndDate.getTime();
        long endB = targetEndDate.getTime() - targetStartDate.getTime() > 0 ? targetEndDate.getTime()
                : targetStartDate.getTime();

        // 对两个区间结束-开始对比
        if (Math.max(startA, startB) <= Math.min(endA, endB)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 设置为某日期的结束01:00:00
     *
     * @param date
     * @return
     */
    public static Date setDate1AMEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }

    /**
     * 设置为某日期的指定小时
     *
     * @param date
     * @return
     */
    public static Date setDateAndHour(Date date,int hours) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }

    /**
     * @param date
     * @return yyyy-MM-dd
     */
    public static String formatSimpleData(Date date) {
        return format(date, DATE_FORMAT_PATTERN);
    }

    /**
     * 设置为某日期22:00:00
     *
     * @param date
     * @return
     */
    public static Date setDateTwentyTwo(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }

    /**
     * 设置为某日期的结束02:00:00
     *
     * @param date
     * @return
     */
    public static Date setDate2AMEnd(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 2);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 000);
        return c.getTime();
    }

    /**
     * 判断日期格式是否合法
     * @param str
     * @param pattern
     * @return
     */
    public static boolean isValidDate(String str, String pattern) {
        boolean convertSuccess = true;// 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {// 设置lenient为false.否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 获取startDate日期后month月的日期
     * @param startDate 开始日期
     * @param month  几个月后
     * @return
     */
    public static Date getMonthDate(Date startDate,int month){
        LocalDateTime localDateTime = startDate.toInstant()
            .atZone(ZoneId.systemDefault() )
            .toLocalDateTime().plusMonths(month);
        Date date = Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * @author: liuye07
     * @Description: 格式化日期 （yyyy-MM-dd HH:mm:ss）：String To Date
     * @date: 2021年6月25日 下午2:14:33
     * @param str
     * @return
     */
    public static Date parseStrToDate(String str) {
        try {
            return DateUtils.parseDate(str, DateUtils.DATE_TIME_FORMAT_PATTERN);
        } catch (ParseException e) {
            return null;
        }
    }

    /***
     *  判断两个日期 天是否相等
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDayOfMonth(Date date1, Date date2) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static Date localDate2Date(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate date2LocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static long getTimeWithAddDays(Date date, Integer day) {
        return addDays(date, day).getTime();
    }

    /**
     * @desc 时区转换 Asia/Chongqing-->GMT+8
     * @author xudong01
     * @date 2020/08/20
     */
    public static Date timezoneConvert(Date sourceTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN);
        df.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        String sourceDate = df.format(sourceTime);
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date targetTime = df.parse(sourceDate);
        return new Date(targetTime.getTime());
    }

    public static List<String> differenceDays(Date date) {

        Date currentDate = new Date();
        Integer differenceDays = DateUtils.differenceDays(currentDate, date);
        List<String> dateList = new ArrayList<String>();

        for (int i = 0; i < differenceDays; i++) {
            Date day = DateUtils.setDateBegin(DateUtils.getNextDayDate(date, i));
            dateList.add(DateUtils.format4NoHyphen(day));
        }
        if (dateList.size() == 0) {
            dateList.add(DateUtils.format4NoHyphen(date));
        }
        return dateList;
    }

    public static Date getEndDateByEndDay(Date date, Integer periods, int endDay) {

        Date endDate = Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(periods)
            .atStartOfDay(ZoneId.systemDefault()).toInstant());
        if(endDate.getDate() >= endDay){
            endDate.setDate(endDay);
        }
        return endDate;
	}
    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成随机时间
     *
     * @param start
     * @param end
     * @return
     */
    public static Date randomDate(Date start, Date end) {
        try {
            // getTime()表示返回自 1970 年 1 月 1 日 00:00:00 GMT 以来此 Date 对象表示的毫秒数。
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin, long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        // 如果返回的是开始时间和结束时间，则递归调用本函数查找随机值
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

    /**
     * 获取此区间所有日期
     *
     * @param start
     * @param end
     * @return
     */
    public static List<String> startToEnd(Date start, Date end) {
        List<String> list = new ArrayList<>();
        long s = start.getTime();
        long e = end.getTime();
        Long oneDay = 1000 * 60 * 60 * 24l;
        while (s <= e) {
            start = new Date(s);
            list.add(new SimpleDateFormat(DateUtils.DATE_FORMAT_SLASH).format(start));
            s += oneDay;
        }
        return list;
    }

    public static LocalDateTime toLocalDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

}
