package de.raytiw.steamlight.protocol.response;

public record StatusEvent(
        String event,
        int brightness,
        int leds) {
}