#include "ErrorEffect.h"

void ErrorEffect::start(LedController& leds)
{
    lastUpdate = millis();
    level = MIN_LEVEL;
    direction = 1;

    leds.fill(CRGB(level, 0, 0));
    leds.show();
}

void ErrorEffect::update(LedController& leds)
{
    const unsigned long now = millis();

    if (now - lastUpdate < FRAME_INTERVAL_MS)
    {
        return;
    }

    lastUpdate = now;

    level += direction * 2;

    if (level >= MAX_LEVEL)
    {
        level = MAX_LEVEL;
        direction = -1;
    }
    else if (level <= MIN_LEVEL)
    {
        level = MIN_LEVEL;
        direction = 1;
    }

    leds.fill(CRGB(level, 0, 0));
    leds.show();
}