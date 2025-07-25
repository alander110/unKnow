package com.project.user.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

// 自定义序列化器
public class SensitiveDataSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeString("");
            return;
        }

        // 根据不同字段类型进行脱敏处理
        String fieldName = gen.getOutputContext().getCurrentName();
        String maskedValue = value;

        if ("phone".equals(fieldName)) {
            maskedValue = value.substring(0, 3) + "****" + value.substring(7);
        } else if ("idCard".equals(fieldName)) {
            maskedValue = value.substring(0, 4) + "**********" + value.substring(14);
        }

        gen.writeString(maskedValue);
    }
}

