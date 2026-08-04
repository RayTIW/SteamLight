package de.raytiw.steamlight.protocol.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EffectCommandTest {

    @Test
    void shouldSerializeBootCommand() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        String json =
                mapper.writeValueAsString(
                        EffectCommand.of(Effect.BOOT));

        assertEquals(
                "{\"cmd\":\"effect\",\"value\":\"boot\"}",
                json);
    }
}