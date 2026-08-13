#include "OtaManager.h"

#include <ArduinoOTA.h>
#include <WiFi.h>

#include "secrets.h"

namespace
{
    constexpr char OTA_HOSTNAME[] = "steamlight";
}

void OtaManager::begin()
{
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    Serial.println("WiFi: connection started");
}

void OtaManager::update()
{
    if (WiFi.status() == WL_CONNECTED)
    {
        if (!otaStarted)
        {
            startOta();
        }

        ArduinoOTA.handle();
        return;
    }

    otaStarted = false;

    const unsigned long now = millis();

    if (now - lastReconnectAttempt >= RECONNECT_INTERVAL_MS)
    {
        lastReconnectAttempt = now;

        Serial.println("WiFi: reconnecting");
        WiFi.disconnect();
        WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    }
}

void OtaManager::startOta()
{
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

    otaStarted = true;

    Serial.println();

    Serial.print("WiFi: connected, IP=");
    Serial.println(WiFi.localIP());

    Serial.print("Status: ");
    Serial.println(WiFi.status());

    Serial.print("OTA: ready at ");
    Serial.print(OTA_HOSTNAME);
    Serial.println(".local");

    Serial.print("Gateway: ");

    Serial.println(WiFi.gatewayIP());

    Serial.print("Subnet: ");
    Serial.println(WiFi.subnetMask());

    Serial.print("MAC: ");
    Serial.println(WiFi.macAddress());

    Serial.print("RSSI: ");
    Serial.println(WiFi.RSSI());

}