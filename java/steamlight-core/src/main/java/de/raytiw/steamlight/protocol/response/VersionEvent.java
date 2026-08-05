package de.raytiw.steamlight.protocol.response;

public record VersionEvent(
        String event,
        String device,
        String version,
        int protocol,
        int leds) {
}