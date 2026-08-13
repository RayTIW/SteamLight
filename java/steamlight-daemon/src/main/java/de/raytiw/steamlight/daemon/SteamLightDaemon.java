package de.raytiw.steamlight.daemon;

import com.fazecast.jSerialComm.SerialPort;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class SteamLightDaemon {

    private SteamLightDaemon() {
    }

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("[INFO] SteamLight Daemon gestartet.");

        ConnectionSupervisor supervisor =
                new ConnectionSupervisor();

        Thread supervisorThread =
                new Thread(
                        supervisor::run,
                        "steamlight-supervisor");

        CountDownLatch serialShutdownFinished =
                new CountDownLatch(1);

        SerialPort.addShutdownHook(
                new Thread(() -> {
                    try {
                        supervisor.switchOffSteamLight();
                    } finally {
                        serialShutdownFinished.countDown();
                    }
                }, "steamlight-serial-shutdown"));

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    System.out.println("[INFO] Shutdown angefordert.");

                    try {
                        serialShutdownFinished.await(
                                5,
                                TimeUnit.SECONDS);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }

                    supervisor.shutdown();

                    try {
                        supervisorThread.join(5_000);
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                    }
                }, "steamlight-shutdown"));

        supervisorThread.start();
        supervisorThread.join();

        System.out.println("[INFO] SteamLight Daemon beendet.");
    }
}