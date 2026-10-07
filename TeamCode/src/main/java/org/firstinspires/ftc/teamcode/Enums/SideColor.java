package org.firstinspires.ftc.teamcode.Enums;

public enum SideColor {
    RED("Red", "red", "r"),
    BLUE("Blue", "blue", "b");

    private final String capitalized;
    private final String lowercase;
    private final String letter;

    SideColor(String capitalized, String lowercase, String letter) {
        this.capitalized = capitalized;
        this.lowercase = lowercase;
        this.letter = letter;
    }

    public String getCapitalized() {
        return capitalized;
    }
    public String getLowercase() {
        return lowercase;
    }
    public String getLetter() {
        return letter;
    }
}
