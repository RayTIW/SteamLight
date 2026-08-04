#include "BootEffect.h"

void BootEffect::start(LedController& leds)
{
    currentPixel = 0;
    lastUpdate = millis();
    completedAt = 0;
    fillingComplete = false;
    finished = false;

    leds.clear();
    leds.show();
}

void BootEffect::update(LedController& leds)
{
    if (finished)
    {
        return;
    }

    const unsigned long now = millis();

    if (!fillingComplete)
    {
        if (now - lastUpdate < STEP_INTERVAL_MS)
        {
            return;
        }

        lastUpdate = now;

        if (currentPixel < leds.count())
        {
            leds.setPixel(currentPixel, CRGB(0, 180, 80));
            leds.show();
            currentPixel++;
        }

        if (currentPixel >= leds.count())
        {
            fillingComplete = true;
            completedAt = now;
        }

        return;
    }

    if (now - completedAt >= HOLD_TIME_MS)
    {
        finished = true;
    }
}

bool BootEffect::isFinished() const
{
    return finished;
}