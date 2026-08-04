#pragma once

#include <Arduino.h>
#include <FastLED.h>

#ifndef LED_PIN
#define LED_PIN 2
#endif

#ifndef NUM_LEDS
#define NUM_LEDS 28
#endif

class LedController
{
public:
    void begin();

    void clear();
    void show();
    void fill(const CRGB& color);
    void setPixel(int index, const CRGB& color);

    void setBrightness(uint8_t brightness);
    uint8_t getBrightness() const;

    int count() const;

private:
    static constexpr uint8_t DEFAULT_BRIGHTNESS = 40;

    CRGB leds[NUM_LEDS];
    uint8_t brightness = DEFAULT_BRIGHTNESS;
};