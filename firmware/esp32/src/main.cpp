#include <Arduino.h>

#include "OtaManager.h"
#include "EffectManager.h"
#include "LedController.h"
#include "SerialProtocol.h"

OtaManager otaManager;
LedController ledController;
EffectManager effectManager;
SerialProtocol serialProtocol;

void setup()
{
    ledController.begin();
    effectManager.begin(ledController);
    serialProtocol.begin();
    otaManager.begin();
}

void loop()
{
    serialProtocol.update(effectManager, ledController);
    effectManager.update(ledController);
    otaManager.update();

    delay(1);
}