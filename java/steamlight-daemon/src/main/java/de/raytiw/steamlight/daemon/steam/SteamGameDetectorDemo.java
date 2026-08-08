package de.raytiw.steamlight.daemon.steam;

import de.raytiw.steamlight.daemon.steam.SteamGameDetector;

public final class SteamGameDetectorDemo {

    public static void main(String[] args) throws Exception {
        SteamGameDetector detector =
                new SteamGameDetector();

        while (true) {
            System.out.println(
                    detector.findRunningGame());

            Thread.sleep(500);
        }
    }
}