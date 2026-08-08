package de.raytiw.steamlight.daemon.steam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

public final class SteamGameDetector {

    private static final Path PROC = Path.of("/proc");
    private static final String STEAM_APP_ID = "SteamAppId=";

    public Optional<RunningGame> findRunningGame() {
        if (!Files.isDirectory(PROC)) {
            return Optional.empty();
        }

        try (DirectoryStream<Path> processes =
                     Files.newDirectoryStream(PROC, "[0-9]*")) {

            for (Path process : processes) {
                OptionalLong appId =
                        readSteamAppId(process.resolve("environ"));

                if (appId.isPresent()) {
                    long pid = Long.parseLong(
                            process.getFileName().toString());

                    return Optional.of(
                            new RunningGame(
                                    pid,
                                    appId.getAsLong()));
                }
            }

        } catch (IOException | NumberFormatException ignored) {
            // /proc kann sich während des Scans ändern.
        }

        return Optional.empty();
    }

    private OptionalLong readSteamAppId(Path environ) {
        try {
            byte[] bytes = Files.readAllBytes(environ);

            String environment =
                    new String(bytes, StandardCharsets.UTF_8);

            for (String variable : environment.split("\0")) {
                if (!variable.startsWith(STEAM_APP_ID)) {
                    continue;
                }

                String value =
                        variable.substring(STEAM_APP_ID.length());

                long appId = Long.parseLong(value);

                if (appId > 0) {
                    return OptionalLong.of(appId);
                }
            }

        } catch (IOException
                 | SecurityException
                 | NumberFormatException ignored) {
            // Prozess verschwunden oder nicht lesbar.
        }

        return OptionalLong.empty();
    }
}