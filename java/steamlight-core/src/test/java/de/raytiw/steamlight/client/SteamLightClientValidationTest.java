package de.raytiw.steamlight.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SteamLightClientValidationTest {

    @Test
    void shouldRejectInvalidColorValues() {
        try (SteamLightClient client =
                     new SteamLightClient("COM3")) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> client.setColor(-1, 0, 0));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> client.setColor(0, 256, 0));

            assertThrows(
                    IllegalArgumentException.class,
                    () -> client.setColor(0, 0, 999));
        }
    }
}