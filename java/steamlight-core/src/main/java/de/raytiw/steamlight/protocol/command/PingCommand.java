package de.raytiw.steamlight.protocol.command;

public record PingCommand(String cmd) {

    public static PingCommand create() {
        return new PingCommand("ping");
    }
}