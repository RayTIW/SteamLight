package de.raytiw.steamlight.protocol.command;

public record ColorCommand(
        String cmd,
        int r,
        int g,
        int b) {

    public static ColorCommand of(
            int r,
            int g,
            int b) {

        return new ColorCommand(
                "color",
                r,
                g,
                b);
    }

}