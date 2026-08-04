package de.raytiw.steamlight.protocol.response;

public record ReadyEvent(
        String event,
        String device,
        String version,
        int protocol,
        int leds) {

    public boolean isSteamLight() {
        return "ready".equals(event)
                && "SteamLight".equals(device);
    }
}