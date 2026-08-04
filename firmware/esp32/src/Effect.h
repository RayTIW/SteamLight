#pragma once

#include "LedController.h"

class Effect
{
public:
    virtual ~Effect() = default;

    virtual void start(LedController& leds) = 0;
    virtual void update(LedController& leds) = 0;

    virtual void stop(LedController& leds)
    {
        // Standardmäßig nichts erforderlich.
    }
};