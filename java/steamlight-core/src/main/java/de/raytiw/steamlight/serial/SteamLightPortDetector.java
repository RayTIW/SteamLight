package de.raytiw.steamlight.serial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;
import de.raytiw.steamlight.protocol.ProtocolCodec;
import de.raytiw.steamlight.protocol.command.HelloCommand;
import de.raytiw.steamlight.protocol.response.ReadyEvent;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class SteamLightPortDetector {

    private static final int ESPRESSIF_VENDOR_ID = 0x303A;
    private static final int USB_SERIAL_JTAG_PRODUCT_ID = 0x1001;

    private static final Duration HANDSHAKE_TIMEOUT =
            Duration.ofSeconds(3);

    private final ProtocolCodec codec;

    public SteamLightPortDetector(ProtocolCodec codec) {
        this.codec = codec;
    }

    public Optional<String> detect() {
        List<SerialPort> candidates = findUsbCandidates();

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        /*
         * Auch einen einzelnen Treffer verifizieren wir per Handshake.
         * VID/PID identifizieren nur den Espressif-USB-Controller,
         * nicht automatisch unsere SteamLight-Firmware.
         */
        return candidates.stream()
                .filter(this::isSteamLight)
                .map(SerialPort::getSystemPortName)
                .findFirst();
    }

    private List<SerialPort> findUsbCandidates() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(this::hasSteamLightUsbId)
                .toList();
    }

    private boolean hasSteamLightUsbId(SerialPort port) {
        return port.getVendorID() == ESPRESSIF_VENDOR_ID
                && port.getProductID()
                == USB_SERIAL_JTAG_PRODUCT_ID;
    }

    private boolean isSteamLight(SerialPort port) {
        String portName = port.getSystemPortName();

        System.out.printf(
                "Prüfe SteamLight-Kandidat %s "
                        + "(VID=%04X, PID=%04X)%n",
                portName,
                port.getVendorID(),
                port.getProductID());

        try (SerialConnection connection =
                     new SerialConnection(portName)) {

            connection.connect();

            connection.send(
                    codec.encode(
                            HelloCommand.create()));

            ReadyEvent ready =
                    waitForReady(connection);

            boolean matches =
                    ready.isSteamLight()
                            && ready.protocol() == 1;

            if (matches) {
                System.out.println(
                        "SteamLight gefunden an " + portName);
            }

            return matches;

        } catch (Exception exception) {
            System.out.printf(
                    "Kandidat %s verworfen: %s%n",
                    portName,
                    exception.getMessage());

            return false;
        }
    }

    private ReadyEvent waitForReady(
            SerialConnection connection)
            throws IOException {

        long deadline = System.nanoTime()
                + HANDSHAKE_TIMEOUT.toNanos();

        while (System.nanoTime() < deadline) {
            try {
                String line = connection.receive();

                if (line == null || line.isBlank()) {
                    continue;
                }

                try {
                    JsonNode node = codec.decodeTree(line);

                    if ("ready".equals(
                            node.path("event").asText())
                            && "SteamLight".equals(
                            node.path("device").asText())) {

                        return codec.decodeReady(line);
                    }

                } catch (IllegalArgumentException ignored) {
                    // Nicht-JSON-Ausgaben dieses Ports ignorieren.
                }

            } catch (SerialPortTimeoutException ignored) {
                // Bis zum Gesamttimeout weiter warten.
            }
        }

        throw new IllegalStateException(
                "Kein SteamLight-Ready empfangen");
    }
}