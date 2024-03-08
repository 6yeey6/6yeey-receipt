package com.ibg.receipt.vo.job.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public abstract class JobParamVo {

    private boolean isOpenNextJob = true;

    public boolean isOpenNextJob() {
        return isOpenNextJob;
    }

    public JobParamVo setOpenNextJob(boolean openNextJob) {
        isOpenNextJob = openNextJob;
        return this;
    }

    public JSONObject convertJson() {
        return JSON.parseObject(JSON.toJSONString(this));
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
