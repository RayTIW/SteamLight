package de.raytiw.steamlight.protocol.response;

public record ResultResponse(
        String result,
        String message) {

    public boolean isOk() {
        return "ok".equals(result);
    }
}