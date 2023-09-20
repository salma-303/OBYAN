package com.example.obyan;

//package binderLayer;

import android.util.Log;

public class FirstFrameData {
    private final int motorSpeedDec;
    private final int dcBusCurrentAmps;
    private final int motorPhaseCurrentAmps;

    public FirstFrameData(int motorSpeedDec, int dcBusCurrentAmps, int motorPhaseCurrentAmps) {
        this.motorSpeedDec = motorSpeedDec;
        this.dcBusCurrentAmps = dcBusCurrentAmps;
        this.motorPhaseCurrentAmps = motorPhaseCurrentAmps;
    }

    // Add getter methods for the fields
    public int getMotorSpeedDec() {

        Log.d("firstframe", "getMotorSpeedDec() is called");
        Log.d("firstframe", "getMotorSpeedDec() is " + Double.toString(this.motorSpeedDec));
        return motorSpeedDec;
    }

    public int getDcBusCurrentAmps() {
        return dcBusCurrentAmps;
    }

    public int getMotorPhaseCurrentAmps() {
        return motorPhaseCurrentAmps;
    }
}
