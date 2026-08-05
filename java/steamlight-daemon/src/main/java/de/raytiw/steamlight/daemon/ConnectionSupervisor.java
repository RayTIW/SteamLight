package de.raytiw.steamlight.daemon;

import de.raytiw.steamlight.client.SteamLightClient;
import de.raytiw.steamlight.protocol.response.VersionEvent;

import java.time.Duration;

public final class ConnectionSupervisor {

    private static final Duration PING_INTERVAL =
            Duration.ofSeconds(5);

    private static final Duration RECONNECT_DELAY =
            Duration.ofSeconds(5);

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            runConnectionSession();

            if (!Thread.currentThread().isInterrupted()) {
                sleep(RECONNECT_DELAY);
            }
        }

        logInfo("ConnectionSupervisor beendet.");
    }

    private void runConnectionSession() {
        logInfo("Suche SteamLight ...");

        try (SteamLightClient client = new SteamLightClient()) {
            client.connect();

            VersionEvent version = client.version();

            logInfo(
                    "SteamLight verbunden: Firmware %s, Protokoll %d, LEDs %d"
                            .formatted(
                                    version.version(),
                                    version.protocol(),
                                    version.leds()));

            monitorConnection(client);

        } catch (Exception exception) {
            logWarning(
                    "Verbindung fehlgeschlagen oder verloren: "
                            + rootMessage(exception));
        }
    }

    private void monitorConnection(SteamLightClient client) {
        while (!Thread.currentThread().isInterrupted()) {
            client.ping();
            logInfo("Ping OK");

            sleep(PING_INTERVAL);
        }
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private static String rootMessage(Throwable throwable) {
        Throwable current = throwable;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        String message = current.getMessage();

        return message != null
                ? message
                : current.getClass().getSimpleName();
    }

    private static void logInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    private static void logWarning(String message) {
        System.err.println("[WARN] " + message);
    }
}