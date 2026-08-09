#include "OtaManager.h"

#include <Arduino.h>
#include <ArduinoOTA.h>
#include <WiFi.h>

#include "secrets.h"

namespace
{
    constexpr char OTA_HOSTNAME[] = "steamlight";
}

void OtaManager::begin()
{
    connectWifi();

    ArduinoOTA.setHostname(OTA_HOSTNAME);

    ArduinoOTA
        .onStart([]()
        {
            Serial.println("OTA: update started");
        })
        .onEnd([]()
        {
            Serial.println("OTA: update finished");
        })
        .onProgress([](
            unsigned int progress,
            unsigned int total)
        {
            const unsigned int percent =
                (progress * 100U) / total;

            Serial.printf(
                "OTA: progress %u%%\n",
                percent);
        })
        .onError([](ota_error_t error)
        {
            Serial.printf(
                "OTA: error %u\n",
                error);
        });

    ArduinoOTA.begin();

    Serial.print("OTA: ready at ");
    Serial.print(OTA_HOSTNAME);
    Serial.print(".local / ");
    Serial.println(WiFi.localIP());
}

void OtaManager::update()
{
    ArduinoOTA.handle();
}

void OtaManager::connectWifi()
{
    WiFi.mode(WIFI_STA);
    WiFi.begin(
        WIFI_SSID,
        WIFI_PASSWORD);

    Serial.print("WiFi: connecting");

    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
    }

 Serial.println();

 Serial.print("Status: ");
 Serial.println(WiFi.status());

 Serial.print("IP: ");
 Serial.println(WiFi.localIP());

 Serial.print("Gateway: ");
 Serial.println(WiFi.gatewayIP());

 Serial.print("Subnet: ");
 Serial.println(WiFi.subnetMask());

 Serial.print("MAC: ");
 Serial.println(WiFi.macAddress());

 Serial.print("RSSI: ");
 Serial.println(WiFi.RSSI());
}