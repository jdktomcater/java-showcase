package com.jdktomcat.showcase.spring.ai.bot.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> cls) {
        try { return mapper.readValue(json, cls); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public static String toJson(Object obj) {
        try { return mapper.writeValueAsString(obj); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}

