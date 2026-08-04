#pragma once

#include <Arduino.h>

#include "EffectManager.h"
#include "LedController.h"

class SerialProtocol
{
public:
    void begin();

    void update(
        EffectManager& effectManager,
        LedController& leds);

private:
    static constexpr size_t MAX_COMMAND_LENGTH = 256;

    String inputBuffer;

    void processCommand(
        const String& input,
        EffectManager& effectManager,
        LedController& leds);

    void sendOk(const char* message);
    void sendError(const char* message);
    void sendReady();
};