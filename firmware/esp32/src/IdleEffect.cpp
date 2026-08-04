#include "IdleEffect.h"

void IdleEffect::start(LedController& leds)
{
    lastUpdate = millis();
    level = MIN_LEVEL;
    direction = 1;

    leds.fill(CRGB(0, level, level / 2));
    leds.show();
}

void IdleEffect::update(LedController& leds)
{
    const unsigned long now = millis();

    if (now - lastUpdate < FRAME_INTERVAL_MS)
    {
        return;
    }

    lastUpdate = now;

    level += direction;

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

    leds.fill(CRGB(0, level, level / 2));
    leds.show();
}