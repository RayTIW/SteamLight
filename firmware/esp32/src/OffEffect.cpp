#include "OffEffect.h"

void OffEffect::start(LedController& leds)
{
    leds.clear();
    leds.show();
}

void OffEffect::update(LedController& leds)
{
    // Keine Animation.
}