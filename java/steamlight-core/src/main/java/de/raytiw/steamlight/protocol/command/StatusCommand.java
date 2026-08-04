package de.raytiw.steamlight.protocol.command;

public record StatusCommand(String cmd) {

    public static StatusCommand create() {

        return new StatusCommand("status");
    }

}