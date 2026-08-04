package de.raytiw.steamlight.protocol.command;

public record HelloCommand(String cmd) {

    public static HelloCommand create() {
        return new HelloCommand("hello");
    }
}