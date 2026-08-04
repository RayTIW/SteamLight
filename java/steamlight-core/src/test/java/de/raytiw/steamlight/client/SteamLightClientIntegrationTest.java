package de.raytiw.steamlight.client;

import de.raytiw.steamlight.protocol.response.StatusEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SteamLightClientIntegrationTest {

    @Test
    void shouldDetectAndControlSteamLight() throws Exception {
        try (SteamLightClient client = new SteamLightClient()) {
            client.connect();

            System.out.println(client.deviceInfo());

            client.setBrightness(25);
            client.boot();

            Thread.sleep(2_000);

            client.idle();

            StatusEvent status = client.status();

            assertEquals(25, status.brightness());
            assertEquals(28, status.leds());
        }
    }
}