#pragma once

#include <Arduino.h>

#include "Effect.h"

class BootEffect final : public Effect
{
public:
    void start(LedController& leds) override;
    void update(LedController& leds) override;

    bool isFinished() const;

private:
    static constexpr unsigned long STEP_INTERVAL_MS = 45;
    static constexpr unsigned long HOLD_TIME_MS = 500;

    int currentPixel = 0;
    unsigned long lastUpdate = 0;
    unsigned long completedAt = 0;
    bool fillingComplete = false;
    bool finished = false;
};