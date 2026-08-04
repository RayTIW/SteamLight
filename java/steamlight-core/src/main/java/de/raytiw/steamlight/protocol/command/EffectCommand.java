package de.raytiw.steamlight.protocol.command;

public record EffectCommand(
        String cmd,
        String value) {

    public static EffectCommand of(Effect effect) {

        return new EffectCommand(
                "effect",
                effect.name().toLowerCase());
    }

}