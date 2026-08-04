package de.raytiw.steamlight.protocol.command;

public record BrightnessCommand(
        String cmd,
        int value) {

    public static BrightnessCommand of(int brightness) {

        return new BrightnessCommand(
                "brightness",
                brightness);
    }

}