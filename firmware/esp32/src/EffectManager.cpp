#include "EffectManager.h"

void EffectManager::begin(LedController& leds)
{
    setEffect(EffectType::BOOT, leds);
}

void EffectManager::update(LedController& leds)
{
    if (currentEffect != nullptr)
    {
        currentEffect->update(leds);
    }

    if (currentType == EffectType::BOOT && bootEffect.isFinished())
    {
        setEffect(EffectType::IDLE, leds);
    }
}

void EffectManager::setEffect(EffectType effect, LedController& leds)
{
    if (currentEffect != nullptr)
    {
        currentEffect->stop(leds);
    }

    currentType = effect;

    switch (currentType)
    {
        case EffectType::BOOT:
            currentEffect = &bootEffect;
            break;

        case EffectType::IDLE:
            currentEffect = &idleEffect;
            break;

        case EffectType::OFF:
            currentEffect = &offEffect;
            break;

        case EffectType::STATIC_COLOR:
            currentEffect = &staticColorEffect;
            break;
    }

    startCurrentEffect(leds);
}

void EffectManager::setStaticColor(
    const CRGB& color,
    LedController& leds)
{
    staticColorEffect.setColor(color);
    setEffect(EffectType::STATIC_COLOR, leds);
}

void EffectManager::startCurrentEffect(LedController& leds)
{
    if (currentEffect != nullptr)
    {
        currentEffect->start(leds);
    }
}