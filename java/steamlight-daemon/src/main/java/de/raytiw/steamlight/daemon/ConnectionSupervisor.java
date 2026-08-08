package de.raytiw.steamlight.daemon;

import de.raytiw.steamlight.client.SteamLightClient;
import de.raytiw.steamlight.daemon.event.SteamEvent;
import de.raytiw.steamlight.daemon.event.SteamEventDispatcher;
import de.raytiw.steamlight.daemon.steam.RunningGame;
import de.raytiw.steamlight.daemon.steam.SteamGameDetector;
import de.raytiw.steamlight.daemon.steam.SteamProcessDetector;
import de.raytiw.steamlight.protocol.response.VersionEvent;

import java.time.Duration;
import java.util.Optional;

public final class ConnectionSupervisor {

    private RunningGame runningGame;

    private final SteamGameDetector gameDetector =
            new SteamGameDetector();

    private static final Duration PING_INTERVAL =
            Duration.ofSeconds(5);

    private static final Duration RECONNECT_DELAY =
            Duration.ofSeconds(5);

    private final SteamEventDispatcher eventDispatcher =
            new SteamEventDispatcher();

    private final SteamProcessDetector steamDetector =
            new SteamProcessDetector();

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

            eventDispatcher.dispatch(
                    SteamEvent.STARTUP,
                    client);

            monitorConnection(client);

        } catch (Exception exception) {
            logWarning(
                    "Verbindung fehlgeschlagen oder verloren: "
                            + rootMessage(exception));
        }
    }

    private void monitorConnection(SteamLightClient client) {

        long nextPingAt = 0;

        while (!Thread.currentThread().isInterrupted()) {

            long now = System.nanoTime();

            // -----------------------------
            // Steam gestartet / beendet
            // -----------------------------

            steamDetector.poll().ifPresent(event ->
                    eventDispatcher.dispatch(event, client));

            // -----------------------------
            // Spiel gestartet / beendet
            // -----------------------------

            Optional<RunningGame> detectedGame =
                    gameDetector.findRunningGame();

            if (runningGame == null && detectedGame.isPresent()) {

                runningGame = detectedGame.get();

                logInfo("Spiel gestartet: AppID "
                        + runningGame.appId());

                eventDispatcher.dispatch(
                        SteamEvent.GAME_STARTED,
                        client);
            }

            if (runningGame != null && detectedGame.isEmpty()) {

                logInfo("Spiel beendet: AppID "
                        + runningGame.appId());

                runningGame = null;

                eventDispatcher.dispatch(
                        SteamEvent.GAME_STOPPED,
                        client);
            }

            if (runningGame != null
                    && detectedGame.isPresent()
                    && runningGame.appId()
                    != detectedGame.get().appId()) {

                logInfo("Spielwechsel: "
                        + runningGame.appId()
                        + " -> "
                        + detectedGame.get().appId());

                runningGame = detectedGame.get();

                eventDispatcher.dispatch(
                        SteamEvent.GAME_STARTED,
                        client);
            }

            // -----------------------------
            // Ping
            // -----------------------------

            if (now >= nextPingAt) {

                client.ping();

                logInfo("Ping OK");

                nextPingAt = now + PING_INTERVAL.toNanos();
            }

            sleep(Duration.ofMillis(500));
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