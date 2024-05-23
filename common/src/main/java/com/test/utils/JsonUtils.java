package com.test.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static String serialize(Object input) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(input);
    }

    public static <T> T deserialize(String input, Class<T> tClass) throws IOException {
        return OBJECT_MAPPER.readValue(input, tClass);
    }
}