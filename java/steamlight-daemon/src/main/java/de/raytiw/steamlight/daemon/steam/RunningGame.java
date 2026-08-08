package de.raytiw.steamlight.daemon.steam;

public record RunningGame(
        long processId,
        long appId) {
}