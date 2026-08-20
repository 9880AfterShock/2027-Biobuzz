package org.firstinspires.ftc.teamcode.Mechanisms.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Sensors.Gyro;

public class Drivetrain {
    private static DcMotorEx leftFront;
    private static DcMotorEx rightFront;
    private static DcMotorEx leftBack;
    private static DcMotorEx rightBack;
    private static OpMode opmode;

    public static final boolean fieldCentric = true;
    private static final double speedDivider = 3.0; //divider for slow mode
    private static final double strafeMultiplier = 1.1; //multiplier for counteracting strafing slippage

    private static double leftFrontPower;
    private static double rightFrontPower;
    private static double leftBackPower;
    private static double rightBackPower;


    public static void init(OpMode opmode) {
        leftFront = opmode.hardwareMap.get(DcMotorEx.class, "leftFront"); //Port _ on ___ hub
        rightFront = opmode.hardwareMap.get(DcMotorEx.class, "rightFront"); //Port _ on ___ hub
        leftBack = opmode.hardwareMap.get(DcMotorEx.class, "leftBack"); //Port _ on ___ hub
        rightBack = opmode.hardwareMap.get(DcMotorEx.class, "rightBack"); //Port _ on ___ hub

        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);

        leftFrontPower = 0.0;
        rightFrontPower = 0.0;
        leftBackPower = 0.0;
        rightBackPower = 0.0;


        Drivetrain.opmode = opmode;

    }

    public static void updateDrive(double x, double y, double rotation, boolean slowMode, boolean resetIMU){
        if (resetIMU && fieldCentric){
            Gyro.resetYaw();
        }

        if (fieldCentric){
            double heading = Gyro.getYawRadians();

            double rotX = x * Math.cos(-heading) - y * Math.sin(-heading);
            double rotY = x * Math.sin(-heading) + y * Math.cos(-heading);
            rotX = rotX * strafeMultiplier;

            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rotation), 1);
            leftFrontPower = (rotY + rotX + rotation) / denominator;
            rightFrontPower = (rotY - rotX - rotation) / denominator;
            leftBackPower = (rotY - rotX + rotation) / denominator;
            rightBackPower = (rotY + rotX - rotation) / denominator;
        } else {

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rotation), 1);
            leftFrontPower = (y + x*strafeMultiplier + rotation) / denominator;
            rightFrontPower = (y - x*strafeMultiplier + rotation) / denominator;
            leftBackPower = (y - x*strafeMultiplier - rotation) / denominator;
            rightBackPower = (y + x*strafeMultiplier - rotation) / denominator;

        }

        double speed = slowMode ? speedDivider : 1.0;
        leftFront.setPower(leftFrontPower/speed);
        rightFront.setPower(rightFrontPower/speed);
        leftBack.setPower(leftBackPower/speed);
        rightBack.setPower(rightBackPower/speed);

        opmode.telemetry.addData("Front Left Power", leftFrontPower);
        opmode.telemetry.addData("Front Right Power", rightFrontPower);
        opmode.telemetry.addData("Back Left Power", leftBackPower);
        opmode.telemetry.addData("Back Right Power", rightBackPower);

        opmode.telemetry.addData("Front Left Amps", leftFront.getCurrent(CurrentUnit.AMPS));
        opmode.telemetry.addData("Front Right Amps", rightFront.getCurrent(CurrentUnit.AMPS));
        opmode.telemetry.addData("Back Left Amps", leftBack.getCurrent(CurrentUnit.AMPS));
        opmode.telemetry.addData("Back Right Amps", rightBack.getCurrent(CurrentUnit.AMPS));


    }

}