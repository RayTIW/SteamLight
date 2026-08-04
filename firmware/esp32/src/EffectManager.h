#pragma once

#include "BootEffect.h"
#include "IdleEffect.h"
#include "LedController.h"
#include "OffEffect.h"
#include "StaticColorEffect.h"

enum class EffectType
{
    BOOT,
    IDLE,
    OFF,
    STATIC_COLOR
};

class EffectManager
{
public:
    void begin(LedController& leds);
    void update(LedController& leds);

    void setEffect(EffectType effect, LedController& leds);
    void setStaticColor(const CRGB& color, LedController& leds);

private:
    void startCurrentEffect(LedController& leds);

    EffectType currentType = EffectType::BOOT;
    Effect* currentEffect = nullptr;

    BootEffect bootEffect;
    IdleEffect idleEffect;
    OffEffect offEffect;
    StaticColorEffect staticColorEffect;
};