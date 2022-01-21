package com.yibo.auth.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @Author: huangyibo
 * @Date: 2021/8/26 2:00
 * @Description: 添加CustomOauth2Exception的序列化实现
 */
public class CustomOAuth2ExceptionSerializer extends StdSerializer<CustomOAuth2Exception> {

    public CustomOAuth2ExceptionSerializer() {
        super(CustomOAuth2Exception.class);
    }


    @Override
    public void serialize(CustomOAuth2Exception exception, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("error", String.valueOf(exception.getHttpErrorCode()));
        jsonGenerator.writeStringField("message", exception.getMessage());
        jsonGenerator.writeStringField("path", request.getServletPath());
        jsonGenerator.writeStringField("timestamp", String.valueOf(System.currentTimeMillis()));
        if (exception.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : exception.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                jsonGenerator.writeStringField(key, add);
            }
        }
        jsonGenerator.writeEndObject();
    }
}
