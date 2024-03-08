package com.ibg.receipt.job.service;

import java.util.List;
import java.util.Map;

import org.quartz.Trigger;

import com.ibg.receipt.job.constants.ParamType;

public interface TaskTimerParamParser {

    /**
     * 解析
     * 
     * @param name
     * @param params
     * @return
     */
    List<Trigger> parse(String name, Map<ParamType, String> params);

}
