package com.ibg.receipt.api.interceptor;

import java.util.List;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import com.ibg.receipt.base.vo.JsonResultVo;
import com.ibg.receipt.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public JsonResultVo handleMethodArgumentNotValidException(Exception e) {
        log.warn("接口请求参数校验失败.", e);
        JsonResultVo resultVo = JsonResultVo.error();
        resultVo.setStatus(CodeConstants.C_10101016.getCode());

        if (e instanceof IllegalArgumentException) {
            resultVo.setMessage(e.getMessage());
            return resultVo;
        }
        BindingResult result = null;
        if (e instanceof MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            result = ((BindException) e).getBindingResult();
        }
        StringBuilder sbuf = new StringBuilder();
        List<FieldError> fieldErrorList = result.getFieldErrors();
        if (CollectionUtils.isNotEmpty(fieldErrorList)) {
            for (FieldError fe : fieldErrorList) {
                String field = fe.getField();
                String code = fe.getDefaultMessage();
                sbuf.append(String.format("%s:%s", field, code)).append("; ");
            }
        }
        resultVo.setMessage(sbuf.toString());
        return resultVo;
    }
    
    @org.springframework.web.bind.annotation.ExceptionHandler
    public JsonResultVo<?> handleException(ServiceException e) {
        log.warn("接口请求业务异常.", e);
        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } else {
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({Exception.class})
    public JsonResultVo handleException(Exception e) {
        log.error("接口请求失败.", e);
        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } else {
            return JsonResultVo.error(CodeConstants.C_10101002.getCode(), e.getMessage());
        }
    }
}
