package org.firstinspires.ftc.teamcode.Enums;

public enum LEDColor {
    GREEN(true, false),
    RED(false, true),
    AMBER(true, true),
    OFF(false, false);

    private final boolean n;
    private final boolean nPlusOne;

    LEDColor(boolean n, boolean nPlusOne) {
        this.n = n;
        this.nPlusOne = nPlusOne;
    }

    public boolean greenPower() {
        return n;
    }

    public boolean redPower() {
        return nPlusOne;
    }
}
