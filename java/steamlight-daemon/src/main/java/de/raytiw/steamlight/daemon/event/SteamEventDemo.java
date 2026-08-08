package de.raytiw.steamlight.daemon.event;

import de.raytiw.steamlight.client.SteamLightClient;

public final class SteamEventDemo {

    private SteamEventDemo() {
    }

    public static void main(String[] args) throws Exception {
        SteamEventDispatcher dispatcher =
                new SteamEventDispatcher();

        try (SteamLightClient client =
                     new SteamLightClient()) {

            client.connect();

            dispatcher.dispatch(
                    SteamEvent.STARTUP,
                    client);

            Thread.sleep(2_000);

            dispatcher.dispatch(
                    SteamEvent.GAME_STARTED,
                    client);

            Thread.sleep(2_000);

            dispatcher.dispatch(
                    SteamEvent.GAME_STOPPED,
                    client);

            Thread.sleep(2_000);

            dispatcher.dispatch(
                    SteamEvent.SHUTDOWN,
                    client);
        }
    }
}