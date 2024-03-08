package com.ibg.receipt.util;

import com.alibaba.fastjson.JSON;
import com.ibg.receipt.base.exception.ServiceException;
import com.ibg.receipt.base.exception.code.CodeConstants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.Buffer;

import java.io.IOException;

/**
 * okhttp工具类
 *
 * @author renxin
 */
@Slf4j
public class OkHttpUtils {

    /**
     * 读取RequestBody内容
     *
     * @param requestBody
     * @return
     * @throws IOException
     */
    public static String readRequestBody(final RequestBody requestBody) throws IOException {
        try {
            final RequestBody copy = requestBody;
            if (copy == null) {
                log.warn("copy request body is null");
                return "";
            }
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            log.error("read request body error", e);
            throw e;
        }
    }

    /**
     * 读取ResponseBody内容
     *
     * @param responseBody
     * @return
     * @throws IOException
     */
    public static String readResponseBody(ResponseBody responseBody) throws IOException {
        if (responseBody instanceof RealResponseBody) {
            RealResponseBody realResponseBody = (RealResponseBody) responseBody;
            return realResponseBody.source().readUtf8();
        } else {
            throw new ServiceException(CodeConstants.C_10101002.getCode(), "不能识别的类型" + JSON.toJSONString(responseBody));
        }
    }

}
