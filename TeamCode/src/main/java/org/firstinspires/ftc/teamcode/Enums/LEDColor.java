package org.firstinspires.ftc.teamcode.Enums;

public enum LEDColor {
    GREEN(true, false, "Green"),
    RED(false, true, "Red"),
    AMBER(false, false, "Amber"),
    OFF(true, true, "Off");

    private final boolean n;
    private final boolean nPlusOne;
    private final String colorName;

    LEDColor(boolean n, boolean nPlusOne, String colorName) {
        this.n = n;
        this.nPlusOne = nPlusOne;
        this.colorName = colorName;
    }

    public boolean greenPower() {
        return n;
    }

    public boolean redPower() {
        return nPlusOne;
    }

    public String state() {
        return colorName;
    }
}
