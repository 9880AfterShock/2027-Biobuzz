package org.firstinspires.ftc.teamcode.Sensors;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class I2CDistance {
    private static DistanceSensor slot1; //Closest to shooter
    private static DistanceSensor slot2;
    private static DistanceSensor slot3;
    private static DistanceSensor slot4; //Closest to intake
    private static DistanceSensor slot5; //Intake, for detecting overpossesion
    private static OpMode opmode;

    public static void init(OpMode opmode) {
        slot1 = opmode.hardwareMap.get(DistanceSensor.class, "slot1"); //I2C Bus _ on _ hub

        I2CDistance.opmode = opmode;
    }

    public static double getYawRadians() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public static void resetYaw() {
        imu.resetYaw();
    }

    public static void update() {
        //nothing for now
    }
}