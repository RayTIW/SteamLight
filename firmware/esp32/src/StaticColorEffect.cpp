#include "StaticColorEffect.h"

void StaticColorEffect::setColor(const CRGB& newColor)
{
    color = newColor;
}

void StaticColorEffect::start(LedController& leds)
{
    leds.fill(color);
    leds.show();
}

void StaticColorEffect::update(LedController& leds)
{
    // Statische Farbe benötigt keine Aktualisierung.
}