package de.raytiw.steamlight.serial;

import org.junit.jupiter.api.Test;

class SerialConnectionTest {

    @Test
    void shouldReadStatus() throws Exception {
        try (SerialConnection serial = new SerialConnection("COM3")) {
            serial.connect();

            String ready = serial.receive();
            System.out.println("ESP: " + ready);

            serial.send("{\"cmd\":\"status\"}");

            String status = serial.receive();
            System.out.println("ESP: " + status);
        }
    }

}