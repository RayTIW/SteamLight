#pragma once

#include <Arduino.h>

#include "Effect.h"

class IdleEffect final : public Effect
{
public:
    void start(LedController& leds) override;
    void update(LedController& leds) override;

private:
    static constexpr unsigned long FRAME_INTERVAL_MS = 25;
    static constexpr uint8_t MIN_LEVEL = 20;
    static constexpr uint8_t MAX_LEVEL = 70;

    unsigned long lastUpdate = 0;
    int level = MIN_LEVEL;
    int direction = 1;
};