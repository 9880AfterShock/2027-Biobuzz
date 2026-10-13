package com.example.meepmeepvisualizer;

public class Pinpoint { //Just so we can copy-paste
    //For calculating corner pose alignments, inches from center of robot to wall when braced against on that side
    public static final double frontDistance = 0.0393701 * (291.10703 + 4);
    public static final double leftDistance = 0.0393701 * (215.90000 + 15); //TODO: make sure the side values reflect matchloader (plate is the ~215)
    public static final double rightDistance = 0.0393701 * (215.90000 + 15); //TODO: make sure the side values reflect matchloader (plate is the ~215)
    public static final double backDistance = 0.0393701 * (213.50939 + 4);
}