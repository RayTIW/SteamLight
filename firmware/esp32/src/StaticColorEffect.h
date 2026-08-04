#pragma once

#include "Effect.h"

class StaticColorEffect final : public Effect
{
public:
    void setColor(const CRGB& newColor);

    void start(LedController& leds) override;
    void update(LedController& leds) override;

private:
    CRGB color = CRGB(0, 180, 80);
};