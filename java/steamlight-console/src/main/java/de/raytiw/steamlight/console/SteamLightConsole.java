package de.raytiw.steamlight.console;

import de.raytiw.steamlight.client.SteamLightClient;
import de.raytiw.steamlight.protocol.response.StatusEvent;

import java.util.Scanner;

public final class SteamLightConsole {

    private SteamLightConsole() {
    }

    public static void main(String[] args) {
        String port = args.length > 0 ? args[0] : "COM3";

        try (SteamLightClient client = new SteamLightClient(port);
             Scanner scanner = new Scanner(System.in)) {

            client.connect();

            System.out.println("SteamLight verbunden: " + client.deviceInfo());
            printMenu();

            boolean running = true;

            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                try {
                    switch (input) {
                        case "1" -> {
                            StatusEvent status = client.status();
                            System.out.println(status);
                        }
                        case "2" -> client.boot();
                        case "3" -> client.idle();
                        case "4" -> client.off();
                        case "5" -> client.setColor(0, 180, 80);
                        case "6" -> client.setColor(255, 0, 0);
                        case "7" -> client.setColor(0, 0, 255);
                        case "8" -> setBrightness(client, scanner);
                        case "m", "menu" -> printMenu();
                        case "q", "quit", "exit" -> running = false;
                        default -> System.out.println(
                                "Unbekannter Befehl. 'm' zeigt das Menü.");
                    }
                } catch (RuntimeException exception) {
                    System.err.println(
                            "Befehl fehlgeschlagen: "
                                    + exception.getMessage());
                }
            }

        } catch (Exception exception) {
            System.err.println(
                    "SteamLight konnte nicht gestartet werden: "
                            + exception.getMessage());

            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static void setBrightness(
            SteamLightClient client,
            Scanner scanner) {

        System.out.print("Helligkeit 0–255: ");
        String value = scanner.nextLine().trim();

        try {
            client.setBrightness(Integer.parseInt(value));
        } catch (NumberFormatException exception) {
            System.out.println("Bitte eine ganze Zahl eingeben.");
        }
    }

    private static void printMenu() {
        System.out.println("""
                
                SteamLight Console
                ------------------
                1  Status
                2  Boot-Animation
                3  Idle
                4  Aus
                5  Steam-Grün
                6  Rot
                7  Blau
                8  Helligkeit
                m  Menü anzeigen
                q  Beenden
                """);
    }
}