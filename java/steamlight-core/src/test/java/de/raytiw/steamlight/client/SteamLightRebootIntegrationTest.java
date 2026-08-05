package de.raytiw.steamlight.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class SteamLightRebootIntegrationTest {

    @Test
    void shouldRebootSteamLight() throws Exception {
        try (SteamLightClient client = new SteamLightClient()) {
            client.connect();

            client.reboot();

            Thread.sleep(2_000);

            /*
             * reconnect() erfolgt über denselben Client.
             */
            client.connect();

            assertFalse(client.deviceInfo().version().isBlank());
        }
    }
}