package com.example.wifitry;

public class WifiState {

    public final int WIFI_OFF = 0, WIFI_ON = 1, WIFI_SCAN = 2, WIFI_ACTIVE = 3;
    private int state;

    public WifiState(){
        setState(WIFI_OFF);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState(){
        return state;
    }
}
