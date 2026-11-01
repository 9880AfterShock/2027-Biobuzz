package org.firstinspires.ftc.teamcode.Enums;

public enum Side {
    LEFT("Left", "left", "L"),
    RIGHT("Right", "right", "R");

    private final String capitalized;
    private final String lowercase;
    private final String letter;

    Side(String capitalized, String lowercase, String letter) {
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
