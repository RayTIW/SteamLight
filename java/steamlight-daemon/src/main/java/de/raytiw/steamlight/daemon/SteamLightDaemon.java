package de.raytiw.steamlight.daemon;

public final class SteamLightDaemon {

    private SteamLightDaemon() {
    }

    public static void main(String[] args) {
        System.out.println("[INFO] SteamLight Daemon gestartet.");

        ConnectionSupervisor supervisor =
                new ConnectionSupervisor();

        supervisor.run();

        System.out.println("[INFO] SteamLight Daemon beendet.");
    }
}