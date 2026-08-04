#include <Arduino.h>

#include "EffectManager.h"
#include "LedController.h"
#include "SerialProtocol.h"

LedController ledController;
EffectManager effectManager;
SerialProtocol serialProtocol;

void setup()
{
    ledController.begin();
    effectManager.begin(ledController);
    serialProtocol.begin();
}

void loop()
{
    serialProtocol.update(effectManager, ledController);
    effectManager.update(ledController);

    delay(1);
}