package de.raytiw.steamlight.protocol.response;

public record PongEvent(String event) {

    public boolean isPong() {
        return "pong".equals(event);
    }
}