package de.raytiw.steamlight.daemon.event;

import de.raytiw.steamlight.client.SteamLightClient;
import de.raytiw.steamlight.daemon.steam.SteamProcessDetector;

import java.time.Duration;
import java.util.Objects;

public final class SteamEventDispatcher {

    private static final Duration STARTUP_ANIMATION_DURATION =
            Duration.ofSeconds(2);

    public void dispatch(
            SteamEvent event,
            SteamLightClient client) {

        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(client, "client");

        System.out.println("[INFO] Steam-Ereignis: " + event);

        switch (event) {
            case STARTUP, STEAM_STARTED -> handleStartup(client);
            case STEAM_STOPPED, SUSPEND, SHUTDOWN -> client.off();
            case GAME_STARTED -> client.boot();
            case GAME_STOPPED -> client.idle();
        }
    }

    private void handleStartup(SteamLightClient client) {
        client.boot();
        sleep(STARTUP_ANIMATION_DURATION);
        client.idle();
    }

    private static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }


}