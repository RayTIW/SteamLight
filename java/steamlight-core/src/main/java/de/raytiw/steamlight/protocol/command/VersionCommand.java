package de.raytiw.steamlight.protocol.command;

public record VersionCommand(String cmd) {

    public static VersionCommand create() {
        return new VersionCommand("version");
    }
}