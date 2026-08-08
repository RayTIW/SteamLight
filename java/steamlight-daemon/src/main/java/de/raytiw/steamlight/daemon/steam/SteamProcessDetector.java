package de.raytiw.steamlight.daemon.steam;

import de.raytiw.steamlight.daemon.event.SteamEvent;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

public final class SteamProcessDetector {

    private Boolean steamRunning;

    public Optional<SteamEvent> poll() {
        boolean currentlyRunning = isSteamRunning();

        if (steamRunning == null) {
            steamRunning = currentlyRunning;
            return Optional.empty();
        }

        if (currentlyRunning == steamRunning) {
            return Optional.empty();
        }

        steamRunning = currentlyRunning;

        return Optional.of(
                currentlyRunning
                        ? SteamEvent.STEAM_STARTED
                        : SteamEvent.STEAM_STOPPED);
    }

    public boolean isSteamRunning() {
        return ProcessHandle.allProcesses()
                .anyMatch(this::isSteamProcess);
    }

    private boolean isSteamProcess(ProcessHandle process) {
        return process.info()
                .command()
                .map(this::extractExecutableName)
                .map(this::isSteamExecutable)
                .orElse(false);
    }

    private String extractExecutableName(String command) {
        try {
            return Path.of(command)
                    .getFileName()
                    .toString()
                    .toLowerCase(Locale.ROOT);
        } catch (RuntimeException exception) {
            return command.toLowerCase(Locale.ROOT);
        }
    }

    private boolean isSteamExecutable(String executableName) {
        return executableName.equals("steam")
                || executableName.equals("steam.exe");
    }
}