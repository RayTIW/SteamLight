package de.raytiw.steamlight.daemon.steam;

public final class SteamProcessDetectorDemo {

    private SteamProcessDetectorDemo() {
    }

    public static void main(String[] args) throws Exception {
        SteamProcessDetector detector =
                new SteamProcessDetector();

        while (!Thread.currentThread().isInterrupted()) {
            System.out.println(
                    "Steam läuft: "
                            + detector.isSteamRunning());

            detector.poll().ifPresent(event ->
                    System.out.println(
                            "Steam-Ereignis: " + event));

            Thread.sleep(1_000);
        }
    }
}