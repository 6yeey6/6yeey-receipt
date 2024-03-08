package com.ibg.receipt.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.job.constants.ParamType;
import com.ibg.receipt.job.service.TaskTimerParamParser;
import com.ibg.receipt.util.CollectionUtils;

import lombok.Data;

public class TimingTaskTimerParamParser implements TaskTimerParamParser {

    private static final String SEPARATOR_PART = ",";
    private static final String SEPARATOR_DAY_TIME = " ";
    private static final String SEPARATOR_TIME = ":";
    private static final String SEPARATOR_KEY = "_";

    @Override
    public List<Trigger> parse(String name, Map<ParamType, String> params) {
        checkParam(name, params);

        List<TimingDate> timingDates = parseValue(params.get(ParamType.TIMING_PARAM));

        if (CollectionUtils.isEmpty(timingDates)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "定时任务参数");
        }

        List<Trigger> triggers = new ArrayList<>(timingDates.size());

        for (TimingDate timingDate : timingDates) {
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(timingDate.key() + name)
                    .withSchedule(timingDate.schedule()).build();
            triggers.add(trigger);
        }

        return triggers;
    }

    private void checkParam(String name, Map<ParamType, String> params) {
        Assert.notBlank(name, "名字");
        Assert.notNull(params, "参数列表");
        if (!params.containsKey(ParamType.TIMING_PARAM)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "定时任务参数");
        }
        Assert.notBlank(params.get(ParamType.TIMING_PARAM), "定时任务参数");
    }

    private List<TimingDate> parseValue(String value) {
        List<TimingDate> timingDates = new ArrayList<>();

        String[] parts = value.split(SEPARATOR_PART);
        if (parts.length > 0) {
            for (String part : parts) {
                TimingDate timingDate = new TimingDate();

                String[] dayTime = part.split(SEPARATOR_DAY_TIME);
                if (dayTime.length > 1) {
                    timingDate.setDay(Integer.parseInt(dayTime[0]));
                    part = dayTime[1];
                }

                String[] time = part.split(SEPARATOR_TIME);
                timingDate.setHour(Integer.parseInt(time[0]));
                timingDate.setMinute(Integer.parseInt(time[1]));

                timingDate.check();

                timingDates.add(timingDate);
            }
        }

        return timingDates;
    }

    @Data
    private class TimingDate {
        private Integer day;
        private Integer hour;
        private Integer minute;

        public void check() {
            if (day != null && (day < 1 || day > 31)) {
                throw ServiceException.exception(CodeConstants.C_10101006, "日期参数");
            }
            Assert.notNull(hour, "小时参数");
            if (hour < 0 || hour > 23) {
                throw ServiceException.exception(CodeConstants.C_10101006, "小时参数");
            }
            Assert.notNull(minute, "分钟参数");
            if (minute < 0 || minute > 59) {
                throw ServiceException.exception(CodeConstants.C_10101006, "分钟参数");
            }
        }

        public String key() {
            StringBuffer key = new StringBuffer();

            if (day != null) {
                key.append(day).append(SEPARATOR_KEY);
            }
            if (hour != null) {
                key.append(hour).append(SEPARATOR_KEY);
            }
            if (minute != null) {
                key.append(minute).append(SEPARATOR_KEY);
            }

            return key.toString();
        }

        public CronScheduleBuilder schedule() {
            if (day != null) {
                return CronScheduleBuilder.monthlyOnDayAndHourAndMinute(day, hour, minute);
            }
            return CronScheduleBuilder.dailyAtHourAndMinute(hour, minute);
        }
    }

}
