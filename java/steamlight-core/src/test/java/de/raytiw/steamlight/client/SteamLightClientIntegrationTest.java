package de.raytiw.steamlight.client;

import de.raytiw.steamlight.protocol.response.PongEvent;
import de.raytiw.steamlight.protocol.response.StatusEvent;
import de.raytiw.steamlight.protocol.response.VersionEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SteamLightClientIntegrationTest {

    @Test
    void shouldDetectAndControlSteamLight() throws Exception {
        try (SteamLightClient client = new SteamLightClient()) {
            client.connect();

            VersionEvent version = client.version();

            assertEquals("version", version.event());
            assertEquals("SteamLight", version.device());
            assertEquals("0.3.0", version.version());
            assertEquals(1, version.protocol());
            assertEquals(28, version.leds());

            client.setBrightness(25);
            client.boot();

            Thread.sleep(2_000);

            client.idle();

            StatusEvent status = client.status();

            assertEquals(25, status.brightness());
            assertEquals(28, status.leds());

            PongEvent pong = client.ping();

            assertEquals("pong", pong.event());
            assertTrue(pong.isPong());
        }
    }
}