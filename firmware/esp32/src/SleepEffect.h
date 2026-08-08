#pragma once

#include "Effect.h"

class SleepEffect : public Effect
{
public:
    void start(LedController& leds) override;
    void update(LedController& leds) override;
};