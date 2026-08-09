#pragma once

class OtaManager
{
public:
    void begin();
    void update();

private:
    void connectWifi();
};