package org.firstinspires.ftc.teamcode.Sensors;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class I2CDistance {
    private static DistanceSensor slot0; //Closest to shooter
    private static DistanceSensor slot1;
    private static DistanceSensor slot2;
    private static DistanceSensor slot3; //Closest to intake
//    private static DistanceSensor slot4; //Intake, for detecting overpossesion
    private static double[] slotMemory = {9880, 9880, 9880, 9880/*, 9880*/}; //Big values to not trip a reading (9880 because team spirit!)
    private static int loopNumber = 1;
    public static int checkCount = 5; //the number of normal loops for every 1 I2C loop
    public static double threshold = 47.0; //public for dash
    private static OpMode opmode;

    public static void init(OpMode opmode) {
        slot0 = opmode.hardwareMap.get(DistanceSensor.class, "slot0"); //I2C Bus 0 (Device 0) on expansion hub
        slot1 = opmode.hardwareMap.get(DistanceSensor.class, "slot1"); //I2C Bus 1 (Device 0) on expansion hub
        slot2 = opmode.hardwareMap.get(DistanceSensor.class, "slot2"); //I2C Bus 2 (Device 0) on expansion hub
        slot3 = opmode.hardwareMap.get(DistanceSensor.class, "slot3"); //I2C Bus 3 (Device 0) on expansion hub
//        slot4 = opmode.hardwareMap.get(DistanceSensor.class, "slot4"); //I2C Bus _ on _ hub

        slotMemory = new double[] {9880, 9880, 9880, 9880/*, 9880*/}; //Big values to not trip a reading (9880 because team spirit!)

        loopNumber = 0;
        I2CDistance.opmode = opmode;
    }

    public static void updateReadings() {
        slotMemory[0] = slot0.getDistance(DistanceUnit.MM);
        slotMemory[1] = slot1.getDistance(DistanceUnit.MM);
        slotMemory[2] = slot2.getDistance(DistanceUnit.MM);
        slotMemory[3] = slot3.getDistance(DistanceUnit.MM);
//        slotMemory[4] = slot4.getDistance(DistanceUnit.MM);
    }

    public static int ballsHeld() { //Sensors tripped
        int balls = 0;
        for (double distance: slotMemory) {
            if (distance < threshold) {
                balls++;
            }
        }
        return balls;
    }

    public static int ballsSecured() { //How many balls are in their resting place, checking starting from shooter
        int balls = 0;
        for (double distance: slotMemory) {
            if (distance > threshold) {
                return balls;
            }
            balls += 1;
        }
        return balls;
    }

    public static void update() {
        loopNumber++;
        if (loopNumber >= checkCount) {
//            updateReadings();
            loopNumber = 0;
        }

        opmode.telemetry.addData("Balls Held", ballsHeld());
        opmode.telemetry.addData("Balls Secured", ballsSecured());

        opmode.telemetry.addData("Shooter slot (slot0)", slotMemory[0]);
        opmode.telemetry.addData("2nd slot (slot1)", slotMemory[1]);
        opmode.telemetry.addData("3rd slot (slot2)", slotMemory[2]);
        opmode.telemetry.addData("Last slot (slot3)", slotMemory[3]);
//        opmode.telemetry.addData("Intake slot (Overpossession) (slot4)", slotMemory[4]);
        //nothing for now
    }

    public static Action waitToEmpty(double timeout, boolean disableReadings) {
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                updateReadings();
                return (ballsHeld() != 0 && !disableReadings) && timer.seconds() <= timeout;
            }
        };
    }

    public static Action waitToFill(double timeout, boolean disableReadings) {
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                updateReadings();
                return (ballsSecured() < 4 && !disableReadings) && timer.seconds() <= timeout;
            }
        };
    }
}