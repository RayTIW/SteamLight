package de.raytiw.steamlight.daemon.sleep;

import de.raytiw.steamlight.daemon.event.SteamEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class SleepDetector {

    private boolean preparingForSleep;

    public Optional<SteamEvent> poll() throws IOException {

        boolean current = readPreparingForSleep();

        if (current == preparingForSleep) {
            return Optional.empty();
        }

        preparingForSleep = current;

        return Optional.of(
                current
                        ? SteamEvent.SUSPEND
                        : SteamEvent.RESUME);
    }

    private boolean readPreparingForSleep() throws IOException {

        Process process = new ProcessBuilder(
                "busctl",
                "get-property",
                "org.freedesktop.login1",
                "/org/freedesktop/login1",
                "org.freedesktop.login1.Manager",
                "PreparingForSleep")
                .redirectErrorStream(true)
                .start();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     process.getInputStream(),
                                     StandardCharsets.UTF_8))) {

            String result = reader.readLine();

            return "b true".equals(result);
        }
    }
}