package org.firstinspires.ftc.teamcode.Sensors;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public class I2CDistance {
    private static DistanceSensor slot0; //Closest to shooter
    private static DistanceSensor slot1;
    private static DistanceSensor slot2;
    private static DistanceSensor slot3; //Closest to intake
    private static DistanceSensor slot4; //Intake, for detecting overpossesion
    private static double[] slotMemory = {0, 0, 0, 0, 0};
    private static int loopNumber = 1;
    public static int checkCount = 5; //the number of normal loops for every 1 I2C loop
    public static double threshold = 47.0; //public for dash
    private static OpMode opmode;

    public static void init(OpMode opmode) {
        slot0 = opmode.hardwareMap.get(DistanceSensor.class, "slot0"); //I2C Bus _ on _ hub
        slot1 = opmode.hardwareMap.get(DistanceSensor.class, "slot1"); //I2C Bus _ on _ hub
        slot2 = opmode.hardwareMap.get(DistanceSensor.class, "slot2"); //I2C Bus _ on _ hub
        slot3 = opmode.hardwareMap.get(DistanceSensor.class, "slot3"); //I2C Bus _ on _ hub
        slot4 = opmode.hardwareMap.get(DistanceSensor.class, "slot4"); //I2C Bus _ on _ hub

        slotMemory = new double[] {0, 0, 0, 0, 0};

        loopNumber = 1;
        I2CDistance.opmode = opmode;
    }

    public static void updateReadings() {
        slotMemory[0] = slot0.getDistance(DistanceUnit.MM);
        slotMemory[1] = slot1.getDistance(DistanceUnit.MM);
        slotMemory[2] = slot2.getDistance(DistanceUnit.MM);
        slotMemory[3] = slot3.getDistance(DistanceUnit.MM);
        slotMemory[4] = slot4.getDistance(DistanceUnit.MM);
    }

    public static int ballsHeld() { //Sensors tripped
        int balls = 0;
        for (double distance: slotMemory) {
            if (distance >= threshold) {
                balls++;
            }
        }
        return balls;
    }

    public static int ballsSecured() { //How many balls are in their resting place, checking starting from shooter
        int balls = 0;
        for (double distance: slotMemory) {
            if (distance < threshold) {
                return balls;
            }
            balls += 1;
        }
        return balls;
    }

    public static void update() {
        loopNumber++;
        if (loopNumber == checkCount) {
            updateReadings();
            loopNumber = 1;
        }

        opmode.telemetry.addData("Balls Held", ballsHeld());
        opmode.telemetry.addData("Balls Secured", ballsSecured());

        opmode.telemetry.addData("Shooter slot", slotMemory[0]);
        opmode.telemetry.addData("2nd slot", slotMemory[1]);
        opmode.telemetry.addData("3rd slot", slotMemory[2]);
        opmode.telemetry.addData("Last slot", slotMemory[3]);
        opmode.telemetry.addData("Intake slot (Overpossession)", slotMemory[4]);
        //nothing for now
    }
}