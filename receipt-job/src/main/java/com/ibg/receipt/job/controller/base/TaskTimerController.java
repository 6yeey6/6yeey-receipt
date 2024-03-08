package com.ibg.receipt.job.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.job.service.TaskTimerService;
import com.ibg.receipt.job.vo.TaskTimerListResponseVo;
import com.ibg.receipt.job.vo.TaskTimerRequestVo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/taskTimer")
@Slf4j
public class TaskTimerController {

    @Autowired
    private TaskTimerService taskTimerService;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public JsonResultVo list(@RequestBody TaskTimerRequestVo queryVo) {
        // 返回结果
        TaskTimerListResponseVo responseVo = new TaskTimerListResponseVo();
        try {
            // 查询列表
            responseVo = taskTimerService.findTaskTimerList(queryVo);
            log.info("查询taskTimer列表成功。");
            JsonResultVo result = JsonResultVo.success().addData("taskTimerInfo", responseVo);
            result.setStatus("0");
            return result;
        } catch (Exception e) {
            log.error(" 查询taskTimer列表异常", e);
            return JsonResultVo.error(JsonResultVo.ERROR, "获取列表失败");
        }
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public JsonResultVo updateStatus(@RequestBody TaskTimerRequestVo messageVo) {
        try {
            taskTimerService.updateTaskTimerStatus(messageVo);
            log.info("状态修改成功messageVo:{}", messageVo);
            JsonResultVo result = JsonResultVo.success();
            result.setStatus("0");
            return result;
        } catch (Exception e) {
            log.error("状态修改失败:{}", e);
            return JsonResultVo.error(JsonResultVo.ERROR, "状态修改失败");
        }
    }

}
