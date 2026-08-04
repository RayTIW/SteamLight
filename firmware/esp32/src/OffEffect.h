#pragma once

#include "Effect.h"

class OffEffect final : public Effect
{
public:
    void start(LedController& leds) override;
    void update(LedController& leds) override;
};