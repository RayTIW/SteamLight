#include "LedController.h"

void LedController::begin()
{
    FastLED.addLeds<WS2812B, LED_PIN, GRB>(leds, NUM_LEDS);

    FastLED.setBrightness(brightness);

    clear();
    show();
}

void LedController::clear()
{
    fill_solid(leds, NUM_LEDS, CRGB::Black);
}

void LedController::show()
{
    FastLED.show();
}

void LedController::fill(const CRGB& color)
{
    fill_solid(leds, NUM_LEDS, color);
}

void LedController::setPixel(int index, const CRGB& color)
{
    if (index >= 0 && index < NUM_LEDS)
    {
        leds[index] = color;
    }
}

void LedController::setBrightness(uint8_t newBrightness)
{
    brightness = newBrightness;
    FastLED.setBrightness(brightness);
    FastLED.show();
}

uint8_t LedController::getBrightness() const
{
    return brightness;
}

int LedController::count() const
{
    return NUM_LEDS;
}