package de.raytiw.steamlight.serial;

import com.fazecast.jSerialComm.SerialPort;
import de.raytiw.steamlight.protocol.ProtocolCodec;
import de.raytiw.steamlight.protocol.response.ReadyEvent;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

public final class SteamLightPortDetector {

    private static final Duration READY_TIMEOUT = Duration.ofSeconds(4);

    private final ProtocolCodec codec;

    public SteamLightPortDetector(ProtocolCodec codec) {
        this.codec = codec;
    }

    public Optional<String> detect() {
        return Arrays.stream(SerialPort.getCommPorts())
                .map(SerialPort::getSystemPortName)
                .filter(this::isSteamLightPort)
                .findFirst();
    }

    private boolean isSteamLightPort(String portName) {
        try (SerialConnection connection =
                     new SerialConnection(portName)) {

            connection.connect();

            long deadline =
                    System.nanoTime() + READY_TIMEOUT.toNanos();

            while (System.nanoTime() < deadline) {
                try {
                    String line = connection.receive();

                    if (line == null || line.isBlank()) {
                        continue;
                    }

                    ReadyEvent ready = codec.decodeReady(line);

                    return ready.isSteamLight()
                            && ready.protocol() == 1;
                } catch (IllegalArgumentException ignored) {
                    // Port liefert Daten, aber kein SteamLight-JSON.
                }
            }

            return false;
        } catch (Exception ignored) {
            return false;
        }
    }
}