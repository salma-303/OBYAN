package com.example.obyan;
//package binderLayer;

public class SecondFrameData {

    private final double hallInputState;

    private final double governorGearPositionsDec;
    private final double motorTempDec;
    private final double controllerTempDec;

    // individual bits from inputStatus byte
    private final boolean lowSpeed;
    private final boolean highSpeed;
    private final boolean brake;
    private final boolean antiTheft;
    private final boolean parkingGear;
    private final boolean cruiseState;
    private final boolean reversing;
    private final boolean singleSupport;


    // individual bits from faultCode byte
    private final boolean overVoltage;
    private final boolean underVoltage;
    private final boolean overCurrent;
    private final boolean mosFailure;
    private final boolean hallFailure;
    private final boolean controllerOverheated;
    private final boolean stallProtection;
    private final boolean handlebarFailure;


    public SecondFrameData(double hallInputState, double governorGearPositionsDec, double motorTempDec, double controllerTempDec,
                           boolean lowSpeed, boolean highSpeed, boolean brake, boolean antiTheft, boolean parkingGear,
                           boolean cruiseState, boolean reversing, boolean singleSupport, boolean overVoltage, boolean underVoltage,
                           boolean overCurrent, boolean mosFailure, boolean hallFailure, boolean controllerOverheated, boolean stallProtection,
                           boolean handlebarFailure) {

        this.hallInputState = hallInputState;

        this.governorGearPositionsDec = governorGearPositionsDec;
        this.motorTempDec = motorTempDec;
        this.controllerTempDec = controllerTempDec;

        // individual bits from inputStatus byte
        this.lowSpeed = lowSpeed;
        this.highSpeed = highSpeed;
        this.brake = brake;
        this.antiTheft = antiTheft;
        this.parkingGear = parkingGear;
        this.cruiseState = cruiseState;
        this.reversing = reversing;
        this.singleSupport = singleSupport;


        // individual bits from faultCode byte
        this.overVoltage = overVoltage;
        this.underVoltage = underVoltage;
        this.overCurrent = overCurrent;
        this.mosFailure = mosFailure;
        this.hallFailure = hallFailure;
        this.controllerOverheated = controllerOverheated;
        this.stallProtection = stallProtection;
        this.handlebarFailure = handlebarFailure;

    }

    // Add getter methods for the fields
    public double hallInputState() {
        return hallInputState;
    }

    public double governorGearPositionsDec() {
        return governorGearPositionsDec;
    }

    public double motorTempDec() {
        return motorTempDec;
    }

    public double controllerTempDec() {
        return controllerTempDec;
    }


    // individual bits from inputStatus byte

    public boolean lowSpeed() {
        return lowSpeed;
    }

    public boolean highSpeed() {
        return highSpeed;
    }

    public boolean brake() {
        return brake;
    }

    public boolean antiTheft() {
        return antiTheft;
    }

    public boolean parkingGear() {
        return parkingGear;
    }

    public boolean cruiseState() {
        return cruiseState;
    }

    public boolean reversing() {
        return reversing;
    }

    public boolean singleSupport() {
        return singleSupport;
    }


    // individual bits from faultCode byte

    public boolean overVoltage() {
        return overVoltage;
    }

    public boolean underVoltage() {
        return underVoltage;
    }

    public boolean overCurrent() {
        return overCurrent;
    }

    public boolean mosFailure() {
        return mosFailure;
    }

    public boolean hallFailure() {
        return hallFailure;
    }

    public boolean controllerOverheated() {
        return controllerOverheated;
    }

    public boolean stallProtection() {
        return stallProtection;
    }

    public boolean handlebarFailure() {
        return handlebarFailure;
    }

}
