#include "SerialProtocol.h"

#include <ArduinoJson.h>

void SerialProtocol::begin()
{
    Serial.begin(115200);

    inputBuffer.reserve(MAX_COMMAND_LENGTH);

    delay(300);
    sendReady();
}

void SerialProtocol::sendDeviceInfo(const char* event)
{
    JsonDocument doc;

    doc["event"] = event;
    doc["device"] = "SteamLight";
    doc["version"] = "0.3.0";
    doc["protocol"] = 1;
    doc["leds"] = NUM_LEDS;

    serializeJson(doc, Serial);
    Serial.println();
}

void SerialProtocol::sendReady()
{
    sendDeviceInfo("ready");
}

void SerialProtocol::sendVersion()
{
    sendDeviceInfo("version");
}

void SerialProtocol::update(
    EffectManager& effectManager,
    LedController& leds)
{
    while (Serial.available() > 0)
    {
        const char character = static_cast<char>(Serial.read());

        // PuTTY sendet je nach Einstellung CR, LF oder CR+LF.
        if (character == '\r' || character == '\n')
        {
            inputBuffer.trim();

            if (!inputBuffer.isEmpty())
            {
                // Serial.print("Empfangen: ");
                // Serial.println(inputBuffer);

                processCommand(inputBuffer, effectManager, leds);
                inputBuffer = "";
            }

            continue;
        }

        if (inputBuffer.length() >= MAX_COMMAND_LENGTH)
        {
            inputBuffer = "";
            sendError("command_too_long");
            continue;
        }

        inputBuffer += character;
    }
}

void SerialProtocol::processCommand(
    const String& input,
    EffectManager& effectManager,
    LedController& leds)
{
    JsonDocument document;

    const DeserializationError error =
        deserializeJson(document, input);

    if (error)
    {
        sendError("invalid_json");
        return;
    }

    const char* command = document["cmd"];

    if (command == nullptr)
    {
        sendError("missing_cmd");
        return;
    }

    if (strcmp(command, "hello") == 0)
    {
        sendReady();
        return;
    }

    if (strcmp(command, "version") == 0)
    {
       sendVersion();
       return;
    }

    if (strcmp(command, "ping") == 0)
    {
       sendPong();
       return;
    }

    if (strcmp(command, "reboot") == 0)
    {
       sendOk("rebooting");

       // wait until answer is send.
       Serial.flush();
       delay(100);

       ESP.restart();
       return;
    }

    if (strcmp(command, "effect") == 0)
    {
        const char* value = document["value"];

        if (value == nullptr)
        {
            sendError("missing_effect");
            return;
        }

   
        if (strcmp(value, "boot") == 0)
        {
            effectManager.setEffect(EffectType::BOOT, leds);
        }
        else if (strcmp(value, "idle") == 0)
        {
            effectManager.setEffect(EffectType::IDLE, leds);
        }
        else if (strcmp(value, "off") == 0)
        {
            effectManager.setEffect(EffectType::OFF, leds);
        }
        else
        {
            sendError("unknown_effect");
            return;
        }

        sendOk("effect_changed");
        return;
    }

    if (strcmp(command, "brightness") == 0)
    {
        if (!document["value"].is<int>())
        {
            sendError("invalid_brightness");
            return;
        }

        int value = document["value"];

        if (value < 0 || value > 255)
        {
            sendError("brightness_out_of_range");
            return;
        }

        leds.setBrightness(static_cast<uint8_t>(value));
        sendOk("brightness_changed");
        return;
    }

    if (strcmp(command, "color") == 0)
    {
        if (!document["r"].is<int>() ||
            !document["g"].is<int>() ||
            !document["b"].is<int>())
        {
            sendError("invalid_color");
            return;
        }

        const int red = document["r"];
        const int green = document["g"];
        const int blue = document["b"];

        if (red < 0 || red > 255 ||
            green < 0 || green > 255 ||
            blue < 0 || blue > 255)
        {
            sendError("color_out_of_range");
            return;
        }

        effectManager.setStaticColor(
            CRGB(red, green, blue),
            leds);

        sendOk("color_changed");
        return;
    }

    if (strcmp(command, "status") == 0)
    {
        Serial.printf(
            "{\"event\":\"status\",\"brightness\":%u,\"leds\":%d}\n",
            leds.getBrightness(),
            leds.count());

        return;
    }

    sendError("unknown_command");
}

void SerialProtocol::sendOk(const char* message)
{
    Serial.printf(
        "{\"result\":\"ok\",\"message\":\"%s\"}\n",
        message);
}

void SerialProtocol::sendError(const char* message)
{
    Serial.printf(
        "{\"result\":\"error\",\"message\":\"%s\"}\n",
        message);
}

void SerialProtocol::sendPong()
{
    Serial.println(
        R"({"event":"pong"})");
}

