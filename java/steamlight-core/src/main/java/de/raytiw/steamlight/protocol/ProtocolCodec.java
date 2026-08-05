package de.raytiw.steamlight.protocol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.raytiw.steamlight.protocol.response.ReadyEvent;
import de.raytiw.steamlight.protocol.response.ResultResponse;
import de.raytiw.steamlight.protocol.response.StatusEvent;
import de.raytiw.steamlight.protocol.response.VersionEvent;

public final class ProtocolCodec {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String encode(Object command) {
        try {
            return objectMapper.writeValueAsString(command);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "SteamLight-Befehl konnte nicht serialisiert werden",
                    exception);
        }
    }

    public JsonNode decodeTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Ungültiges JSON: " + json,
                    exception);
        }
    }

    public ReadyEvent decodeReady(String json) {
        return decode(json, ReadyEvent.class);
    }

    public StatusEvent decodeStatus(String json) {
        return decode(json, StatusEvent.class);
    }

    public VersionEvent decodeVersion(String json) {
        return decode(json, VersionEvent.class);
    }

    public ResultResponse decodeResult(String json) {
        return decode(json, ResultResponse.class);
    }

    private <T> T decode(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException(
                    "Antwort konnte nicht als "
                            + type.getSimpleName()
                            + " gelesen werden: " + json,
                    exception);
        }
    }
}