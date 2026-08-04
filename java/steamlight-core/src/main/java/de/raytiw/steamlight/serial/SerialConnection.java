package de.raytiw.steamlight.serial;

import com.fazecast.jSerialComm.SerialPort;
import de.raytiw.steamlight.exception.SteamLightException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public final class SerialConnection implements Closeable {

    private final String portName;

    private SerialPort port;
    private BufferedReader reader;
    private BufferedWriter writer;

    public SerialConnection(String portName) {
        if (portName == null || portName.isBlank()) {
            throw new IllegalArgumentException(
                    "Portname darf nicht leer sein");
        }

        this.portName = portName;
    }

    public void connect() {
        if (isOpen()) {
            return;
        }

        port = SerialPort.getCommPort(portName);

        port.setComPortParameters(
                115200,
                8,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY);

        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);

        port.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_SEMI_BLOCKING,
                250,
                0);

        port.clearDTR();
        port.clearRTS();

        if (!port.openPort()) {
            throw new IllegalStateException(
                    "Kann Port nicht öffnen: " + portName);
        }

        port.clearDTR();
        port.clearRTS();

        reader = new BufferedReader(
                new InputStreamReader(
                        port.getInputStream(),
                        StandardCharsets.UTF_8));

        writer = new BufferedWriter(
                new OutputStreamWriter(
                        port.getOutputStream(),
                        StandardCharsets.UTF_8));

        System.out.println("Port geöffnet.");
    }

    public void send(String json) throws IOException {
        writer.write(json);
        writer.write('\n');
        writer.flush();

        System.out.println("Sende: " + json);
    }

    public String receive() throws IOException {

        String line = reader.readLine();

        System.out.println("<<< " + line);

        return line;
    }

    public boolean isOpen() {
        return port != null && port.isOpen();
    }

    public String getPortName() {
        return portName;
    }

    @Override
    public void close() throws IOException {
        IOException firstException = null;

        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException exception) {
            firstException = exception;
        }

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException exception) {
            if (firstException == null) {
                firstException = exception;
            }
        }

        if (port != null && port.isOpen()) {
            port.closePort();
        }

        if (firstException != null) {
            throw firstException;
        }
    }

}