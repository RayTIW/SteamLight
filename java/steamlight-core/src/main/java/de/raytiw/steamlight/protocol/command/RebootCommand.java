package de.raytiw.steamlight.protocol.command;

public record RebootCommand(String cmd) {

    public static RebootCommand create() {
        return new RebootCommand("reboot");
    }
}