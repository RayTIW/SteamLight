package de.raytiw.steamlight.serial;

import de.raytiw.steamlight.protocol.ProtocolCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SteamLightPortDetectorIntegrationTest {

    @Test
    void shouldDetectSteamLightPort() {
        SteamLightPortDetector detector =
                new SteamLightPortDetector(
                        new ProtocolCodec());

        String port = detector.detect()
                .orElseThrow(() ->
                        new AssertionError(
                                "SteamLight nicht gefunden"));

        System.out.println(
                "Erkannter SteamLight-Port: " + port);

        assertEquals("COM3", port);
    }
}