package de.raytiw.steamlight.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

    private static final ObjectMapper mapper =
            new ObjectMapper();

    private JsonUtil() {
    }

    public static String toJson(Object object) {

        try {

            return mapper.writeValueAsString(object);

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }

    }

}