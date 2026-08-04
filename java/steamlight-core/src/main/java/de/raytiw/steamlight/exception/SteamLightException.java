package de.raytiw.steamlight.exception;

public class SteamLightException extends RuntimeException {

    public SteamLightException(String message) {
        super(message);
    }

    public SteamLightException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}