package com.ibg.receipt.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.ibg.receipt.base.exception.CommonRetryableException;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.Code;
import com.ibg.receipt.base.exception.code.CodeConstants;

/**
 * Json结果
 *
 * @author ning
 * @param <T>
 */
public class JsonResultVo<T> implements Serializable {

    private static final long serialVersionUID = -3682737772692845415L;

    /** 成功状态码 */
    public static final String SUCCESS = "0000";
    /** 失败状态码 */
    public static final String ERROR = "0001";
    /** 请求外部接口返回结果为失败 */
    public static final String REQUEST_ERROR = "0002";

    /** 请求外部接口返回结果为失败 */
    public static final String TOKEN_INVALID = "0003";

    /** 超时状态码 */
    public static final String TIMEOUT = CodeConstants.C_90000001.getCode();

    /** 状态码 */
    protected String status = JsonResultVo.SUCCESS;
    /** 消息 */
    protected String message;
    /** 数据集 */
    protected T data = null;

    /**
     * 通过vo 构建
     *
     * @param codeVo
     * @return
     */
    public static <T> JsonResultVo<T> error(Code codeVo) {
        if (null == codeVo) {
            return JsonResultVo.error(JsonResultVo.ERROR, "此返回信息未配置编码,请配置");
        }
        return JsonResultVo.error(codeVo.getCode(), codeVo.getMessage());
    }

    public static <T> JsonResultVo<T> error(Code codeVo, String... paras) {
        if (null == codeVo) {
            return JsonResultVo.error(JsonResultVo.ERROR, "此返回信息未配置编码,请配置");
        }
        return JsonResultVo.error(codeVo.getCode(), String.format(codeVo.getMessage(), paras));
    }

    /***
     * 解析当前JsonResultVo的status
     * status ==null   抛出CommonRetryableException
     * status ==0000   成功,返回T泛型数据
     * status ==C_90000001   抛出CommonRetryableException可重试异常
     * 其他值 抛出ServiceException
     * @return
     */
    public T analyses()  {
        if (null == this) {
            throw new CommonRetryableException("Json结果为空,无法解析");
        }
        //成功
        if (JsonResultVo.SUCCESS.equals(this.getStatus())){
            return this.getData();
        }else if (CodeConstants.C_90000001.getCode().equals(this.getStatus())){
            //超时,重试
            throw new CommonRetryableException(this.message);

        }else{
            //其他异常,抛出service异常终止流程
            throw new ServiceException(this.message);
        }
    }


    /**
     * 构建一个成功结果的对象
     *
     * @param message
     * @return
     */
    public static <T> JsonResultVo<T> successWithMessage(String message) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(JsonResultVo.SUCCESS);
        vo.setMessage(message);
        return vo;
    }

    public static <T> boolean isSuccess(JsonResultVo<T> vo) {
        return JsonResultVo.SUCCESS.equals(vo.getStatus());
    }



    public static <T> T convertDataToObject(JsonResultVo<T> vo, Class<T> clazz) {
        if (vo.getData() == null) {
            return null;
        } else {
            return JSON.parseObject(JSON.toJSONString(vo.getData()), clazz);
        }
    }

    /**
     * 构建返回结果
     *
     * @param data
     * @return
     */
    public static <T> JsonResultVo<T> successWithMessage(T data, String message) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(JsonResultVo.SUCCESS);
        vo.setMessage(message);
        vo.setData(data);
        return vo;
    }

    /**
     * 构建返回结果
     *
     * @param data
     * @return
     */
    public static <T> JsonResultVo<T> success(T data) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(JsonResultVo.SUCCESS);
        vo.setMessage("");
        vo.setData(data);
        return vo;
    }

    /**
     * 构建一个成功结果的对象
     *
     * @return
     */
    public static <T> JsonResultVo<T> success() {
        return JsonResultVo.successWithMessage("");
    }

    /**
     * 默认的错误异常
     *
     * @return
     */
    public static <T> JsonResultVo<T> error() {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(JsonResultVo.ERROR);
        vo.setMessage("服务器异常");
        return vo;
    }

    /**
     * 构建一个失败结果的对象
     *
     * @param status
     * @param message
     * @return
     */
    public static <T> JsonResultVo<T> error(String status, String message) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(status);
        vo.setMessage(message);
        return vo;
    }

    /**
     * 构建一个失败结果的对象
     *
     * @param status
     * @param message
     * @return
     */
    public static <T> JsonResultVo<T> errorWithData(String status, String message,T t) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(status);
        vo.setMessage(message);
        vo.setData(t);
        return vo;
    }

    /**
     * token失效
     *
     * @param data
     * @return
     */
    public static <T> JsonResultVo<T> tokenInvalid(T data) {
        JsonResultVo<T> vo = new JsonResultVo<>();
        vo.setStatus(JsonResultVo.TOKEN_INVALID);
        vo.setMessage("token失效");
        vo.setData(data);
        return vo;
    }

    public static JsonResultVo addRows(List<?> list) {
        return JsonResultVo.success().addData("rows", list);
    }

    public boolean isSuccess() {
        return JsonResultVo.SUCCESS.equals(this.getStatus());
    }

    @JSONField(serialize = false)
    public boolean isTimeOut(){
        return JsonResultVo.TIMEOUT.equals(this.getStatus());
    }


    /**
     * 增加一个值
     *
     * @Title: addData
     * @author: FengQing
     * @Description: 初始化调用此方法时，会把 this.data
     *               设置成hashMap。已经指定泛型或赋非map的时，再调用时会抛出阻断提示异常
     * @date: 2018年9月19日 下午3:59:38
     * @param key
     * @param value
     * @return
     */
    public JsonResultVo<T> addData(String key, Object value) {
        Map<String, Object> map = null;
        if (this.data == null) {
            map = new HashMap<String, Object>();
        } else if (this.data instanceof Map) {
            map = (Map<String, Object>) this.data;
        } else {
            throw ServiceException.exception(CodeConstants.C_10101002,
                    "JsonResultVo.data 已经是" + this.getData().getClass() + ",无法执行 addData 方法。");
        }
        map.put(key, value);
        this.data = (T) map;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public JsonResultVo<T> setData(T data) {
        this.data = data;
        return this;
    }
}
