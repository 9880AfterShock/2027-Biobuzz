package org.firstinspires.ftc.teamcode.Mechanisms.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@Config
public class TestShooter {
    private static Servo flywheelServo;
    private static Servo hoodServo;
    private static DcMotorEx shooterMotor;
    private static OpMode opmode;
    public static boolean nectar;
    public static double shooterPower;

    public static final double flywheelPollenPos = 0.0;
    public static final double hoodPollenPos = 0.0;
    public static final double flywheelNectarPos = 1.0;
    public static final double hoodNectarPos = 1.0;

    public static void init(OpMode opmode) {
        nectar = false;

        flywheelServo = opmode.hardwareMap.get(Servo.class, "flywheelServo"); //Port _ on ___ hub
        hoodServo = opmode.hardwareMap.get(Servo.class, "hoodServo"); //Port _ on ___ hub
        shooterMotor = opmode.hardwareMap.get(DcMotorEx.class, "shooterMotor"); //Port _ on ___ hub

        shooterPower = 0;
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterMotor.setPower(0.0);

        TestShooter.opmode = opmode;
    }

    public static void nectarMode() {
        if (!nectar) {
            flywheelServo.setPosition(flywheelNectarPos);
            hoodServo.setPosition(hoodNectarPos);
            nectar = true;
        }
    }

    public static void pollenMode() {
        if (nectar) {
            flywheelServo.setPosition(flywheelPollenPos);
            hoodServo.setPosition(hoodPollenPos);
            nectar = false;
        }
    }

    public static void setShooterPower(double power) {
        power = Range.clip(power, -1, 1);
        if (shooterPower != power) {
            shooterPower = power;
            shooterMotor.setPower(power);
        }
    }

    public static void update(boolean toggle, boolean rev) {
        if (toggle) {
            if (nectar){
                pollenMode();
            } else {
                nectarMode();
            }
        }

        if (rev){
            setShooterPower(1.0);
        } else {
            setShooterPower(0.0);
        }

        opmode.telemetry.addData("Nectar Mode?", nectar);
    }
}