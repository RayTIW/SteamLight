#pragma once

#include <Arduino.h>

class OtaManager
{
public:
    void begin();
    void update();

private:
    void startOta();

    bool otaStarted = false;
    unsigned long lastReconnectAttempt = 0;

    static constexpr unsigned long RECONNECT_INTERVAL_MS = 5000;
};