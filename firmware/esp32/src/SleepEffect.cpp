#include "SleepEffect.h"

void SleepEffect::start(LedController& leds)
{
    leds.clear();
    leds.show();
}

void SleepEffect::update(LedController& leds)
{
    // Im Sleep-Zustand bleibt die LED-Leiste aus.
}