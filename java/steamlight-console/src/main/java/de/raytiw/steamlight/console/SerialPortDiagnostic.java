package de.raytiw.steamlight.console;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortTimeoutException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public final class SerialPortDiagnostic {

    private SerialPortDiagnostic() {
    }

    public static void main(String[] args) throws Exception {
        String portName = args.length > 0 ? args[0] : "COM3";
        String mode = args.length > 1 ? args[1] : "baseline";

        SerialPort port = SerialPort.getCommPort(portName);

        port.setComPortParameters(
                115200,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);

        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                500,
                0);

        // Zustand möglichst schon vor openPort() vorgeben.
        applyLineState(port, mode);

        System.out.printf(
                "Öffne %s mit Modus '%s'%n",
                portName,
                mode);

        if (!port.openPort()) {
            throw new IllegalStateException(
                    "Port konnte nicht geöffnet werden: " + portName);
        }

        // Zustand nach dem Öffnen noch einmal explizit setzen.
        applyLineState(port, mode);

        System.out.println("Port geöffnet.");
        Thread.sleep(250);

        var writer = new BufferedWriter(
                new OutputStreamWriter(
                        port.getOutputStream(),
                        StandardCharsets.UTF_8));

        String hello = "{\"cmd\":\"hello\"}";

        System.out.println(">>> " + hello);

        writer.write(hello);
        writer.write('\n');
        writer.flush();

        System.out.println("Beobachte Ausgabe für 10 Sekunden ...");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        port.getInputStream(),
                        StandardCharsets.UTF_8))) {

            long deadline = System.nanoTime()
                    + java.time.Duration.ofSeconds(10).toNanos();

            while (System.nanoTime() < deadline) {
                try {
                    String line = reader.readLine();

                    if (line != null) {
                        System.out.println("<<< " + line);
                    }
                } catch (SerialPortTimeoutException ignored) {
                    // Weiter beobachten.
                }
            }
        } finally {
            port.closePort();
            System.out.println("Port geschlossen.");
        }
    }

    private static void applyLineState(
            SerialPort port,
            String mode) {

        switch (mode) {
            case "baseline" -> {
                // DTR und RTS nicht verändern.
            }

            case "clear-both" -> {
                port.clearDTR();
                port.clearRTS();
            }

            case "dtr-on" -> {
                port.setDTR();
                port.clearRTS();
            }

            case "rts-on" -> {
                port.clearDTR();
                port.setRTS();
            }

            case "set-both" -> {
                port.setDTR();
                port.setRTS();
            }

            default -> throw new IllegalArgumentException(
                    "Unbekannter Modus: " + mode);
        }
    }
}