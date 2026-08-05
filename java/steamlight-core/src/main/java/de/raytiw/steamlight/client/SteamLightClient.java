package de.raytiw.steamlight.client;

import de.raytiw.steamlight.exception.SteamLightException;
import de.raytiw.steamlight.protocol.ProtocolCodec;
import de.raytiw.steamlight.protocol.command.*;
import de.raytiw.steamlight.protocol.response.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import java.io.IOException;

import de.raytiw.steamlight.serial.SerialConnection;
import de.raytiw.steamlight.serial.SteamLightPortDetector;

import java.io.Closeable;
import java.io.IOException;

public final class SteamLightClient implements Closeable {

    private final ProtocolCodec codec;
    private final String configuredPort;

    private SerialConnection connection;
    private ReadyEvent deviceInfo;

    public SteamLightClient() {
        this(null);
    }

    public SteamLightClient(String configuredPort) {
        this.codec = new ProtocolCodec();
        this.configuredPort = configuredPort;
    }

    public void connect() {
        if (connection != null && connection.isOpen()) {
            return;
        }

        final String portName = configuredPort != null
                ? configuredPort
                : detectPort();

        try {
            connection = new SerialConnection(portName);
            connection.connect();

            /*
             * Der Port wird ohne Reset geöffnet.
             * Die Firmware wird aktiv über hello identifiziert.
             */
            send(HelloCommand.create());
            deviceInfo = waitForReady();

            if (!deviceInfo.isSteamLight()) {
                throw new SteamLightException(
                        "Gerät an " + portName + " ist kein SteamLight");
            }

            if (deviceInfo.protocol() != 1) {
                throw new SteamLightException(
                        "Nicht unterstützte Protokollversion: "
                                + deviceInfo.protocol());
            }

            System.out.println("SteamLight erkannt: " + deviceInfo);

        } catch (Exception exception) {
            closeQuietly();

            throw new SteamLightException(
                    "Verbindung zu SteamLight an "
                            + portName
                            + " fehlgeschlagen",
                    exception);
        }
    }

    public void boot() {
        setEffect(Effect.BOOT);
    }

    public void idle() {
        setEffect(Effect.IDLE);
    }

    public void off() {
        setEffect(Effect.OFF);
    }

    public void setEffect(Effect effect) {
        send(EffectCommand.of(effect));
        receiveResult();
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 255) {
            throw new IllegalArgumentException(
                    "Helligkeit muss zwischen 0 und 255 liegen");
        }

        send(BrightnessCommand.of(brightness));
        receiveResult();
    }

    public void setColor(
            int red,
            int green,
            int blue) {

        validateColor(red);
        validateColor(green);
        validateColor(blue);

        send(ColorCommand.of(red, green, blue));

        ResultResponse response = receiveResult();

        if (!"color_changed".equals(response.message())) {
            throw new SteamLightException(
                    "Unerwartete Farbanwort: "
                            + response.message());
        }
    }

    public StatusEvent status() {
        send(StatusCommand.create());

        String json = readUntil(node ->
                "status".equals(
                        node.path("event").asText()));

        return codec.decodeStatus(json);
    }

    public VersionEvent version() {
        send(VersionCommand.create());

        String json = readUntil(node ->
                "version".equals(
                        node.path("event").asText()));

        return codec.decodeVersion(json);
    }

    public ReadyEvent deviceInfo() {
        ensureConnected();
        return deviceInfo;
    }

    public PongEvent ping() {
        send(PingCommand.create());

        String json = readUntil(node ->
                "pong".equals(
                        node.path("event").asText()));

        return codec.decodePong(json);
    }

    public void reboot() {
        send(RebootCommand.create());

        ResultResponse response = receiveResult();

        if (!"rebooting".equals(response.message())) {
            throw new SteamLightException(
                    "Unerwartete Reboot-Antwort: "
                            + response.message());
        }

        /*
         * Der ESP trennt die Verbindung beim Neustart.
         * Deshalb den lokalen Verbindungszustand ebenfalls schließen.
         */
        closeQuietly();
    }

    private String detectPort() {
        return new SteamLightPortDetector(codec)
                .detect()
                .orElseThrow(() ->
                        new SteamLightException(
                                "Kein SteamLight-Gerät gefunden"));
    }

    private void send(Object command) {
        ensureConnected();

        try {
            connection.send(codec.encode(command));
        } catch (IOException exception) {
            throw new SteamLightException(
                    "Befehl konnte nicht gesendet werden",
                    exception);
        }
    }

    private ResultResponse receiveResult() {
        String json = readUntil(node ->
                node.has("result"));

        ResultResponse response =
                codec.decodeResult(json);

        if (!response.isOk()) {
            throw new SteamLightException(
                    "SteamLight-Fehler: "
                            + response.message());
        }

        return response;
    }

    private void ensureConnected() {
        if (connection == null || !connection.isOpen()) {
            throw new SteamLightException(
                    "SteamLight ist nicht verbunden");
        }
    }

    private void validateColor(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException(
                    "Farbwert muss zwischen 0 und 255 liegen");
        }
    }

    @Override
    public void close() {
        closeQuietly();
    }

    private void closeQuietly() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (IOException ignored) {
            // Beim Schließen gibt es nichts mehr sinnvoll zu tun.
        } finally {
            connection = null;
            deviceInfo = null;
        }
    }

    private ReadyEvent waitForReady() {
        final long deadline = System.nanoTime()
                + java.time.Duration.ofSeconds(8).toNanos();

        while (System.nanoTime() < deadline) {
            try {
                String line = connection.receive();

                if (line == null || line.isBlank()) {
                    continue;
                }

                try {
                    JsonNode node = codec.decodeTree(line);

                    if ("ready".equals(node.path("event").asText())
                            && "SteamLight".equals(
                            node.path("device").asText())) {

                        return codec.decodeReady(line);
                    }

                    System.out.println(
                            "Ignoriere unerwartete Antwort: " + line);

                } catch (IllegalArgumentException exception) {
                    // Beispielsweise:
                    // ESP-ROM:esp32c3-api1-20210207
                    System.out.println(
                            "Ignoriere Nicht-JSON-Zeile: " + line);
                }

            } catch (SerialPortTimeoutException ignored) {
                // Bis zum Gesamtablauf weiter warten.
            } catch (IOException exception) {
                throw new SteamLightException(
                        "Ready-Event konnte nicht gelesen werden",
                        exception);
            }
        }

        throw new SteamLightException(
                "SteamLight hat innerhalb von 8 Sekunden "
                        + "kein Ready-Event gesendet");
    }

    private String readUntil(ResponseMatcher matcher) {
        long deadline = System.nanoTime()
                + java.time.Duration.ofSeconds(3).toNanos();

        while (System.nanoTime() < deadline) {
            try {
                String line = connection.receive();

                if (line == null || line.isBlank()) {
                    continue;
                }

                try {
                    JsonNode json = codec.decodeTree(line);

                    if (matcher.matches(json)) {
                        return line;
                    }

                    System.out.println(
                            "Ignoriere unerwartete Antwort: " + line);

                } catch (IllegalArgumentException exception) {
                    System.out.println(
                            "Ignoriere Nicht-JSON-Zeile: " + line);
                }

            } catch (com.fazecast.jSerialComm.SerialPortTimeoutException ignored) {
                // Bis zum Gesamtablauf weiter warten.
            } catch (IOException exception) {
                throw new SteamLightException(
                        "Antwort konnte nicht gelesen werden",
                        exception);
            }
        }

        throw new SteamLightException(
                "Erwartete Antwort wurde nicht innerhalb "
                        + "von 3 Sekunden empfangen");
    }

    @FunctionalInterface
    private interface ResponseMatcher {
        boolean matches(JsonNode json);
    }
}