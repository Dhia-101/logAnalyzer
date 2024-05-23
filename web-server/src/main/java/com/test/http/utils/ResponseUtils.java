package com.test.http.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.http.responses.MessageResponse;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ResponseUtils {
    private static final Logger LOGGER = Logger.getLogger(ResponseUtils.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object entity) {
        try {
            return MAPPER.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);

            try {
                return MAPPER.writeValueAsString(new MessageResponse(false, "json error"));
            } catch (JsonProcessingException p) {
                return null;
            }
        }
    }

    public static String loadFromResource(String path) {
        InputStream is = ResponseUtils.class.getResourceAsStream(path);

        try {
            return CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);

            return toJson(new MessageResponse(false, "invalid content"));
        }
    }
}