package com.ibg.receipt.job.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ibg.receipt.base.exception.Assert;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.job.constants.ParamType;
import com.ibg.receipt.job.service.TaskTimerParamParser;

public class CronTaskTimerParamParser implements TaskTimerParamParser {

    private static final String SEPARATOR_CRON = " ";

    @Override
    public List<Trigger> parse(String name, Map<ParamType, String> params) {
        checkParam(name, params);

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, Scheduler.DEFAULT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule(params.get(ParamType.CRON_PARAM))).build();

        List<Trigger> triggers = new ArrayList<>(1);
        triggers.add(trigger);

        return triggers;
    }

    private void checkParam(String name, Map<ParamType, String> params) {
        Assert.notBlank(name, "名字");
        Assert.notNull(params, "参数列表");
        if (!params.containsKey(ParamType.CRON_PARAM)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "CRON任务参数");
        }
        Assert.notBlank(params.get(ParamType.CRON_PARAM), "CRON任务参数");
        int length = params.get(ParamType.CRON_PARAM).split(SEPARATOR_CRON).length;
        if (length != 6 && length != 7) {
            throw ServiceException.exception(CodeConstants.C_10101006, "CRON任务参数");
        }
    }

}
