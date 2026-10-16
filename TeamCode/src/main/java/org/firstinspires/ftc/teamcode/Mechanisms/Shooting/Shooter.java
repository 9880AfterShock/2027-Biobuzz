package org.firstinspires.ftc.teamcode.Mechanisms.Shooting;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Utils.Controllers.Controller;
import org.firstinspires.ftc.teamcode.Utils.Controllers.FeedForward;
import org.firstinspires.ftc.teamcode.Utils.Controllers.PID;

@Config
public class Shooter {
    private static DcMotorEx flywheel;
    private static VoltageSensor hubVoltageSensor;
    private static Servo hoodAngleServo; //may need to split off a nectar one
    private static Servo flywheelSlideServo;
    private static Servo hoodSlideServo;
    private static OpMode opmode;
    private static FeedForward pollenFFController;
    private static PID pollenPIDController;
    private static FeedForward nectarFFController;
    private static PID nectarPIDController;
    private static double shooterPower;
    private static double targetRPM;
    private static double hoodAngle;
    public static double flywheelPollenPos = 0.0; //TODO: add final to all servo poses after tuned (and errorMargin)
    public static double hoodPollenPos = 0.0;
    public static double flywheelNectarPos = 1.0;
    public static double hoodNectarPos = 1.0;
    public static double errorMargin = 50; //error margin in RPM to be allowed to shooter, maybe make into a scalar at some point? (more lenience at higher speeds)
    private final static double hoodRatio = 7; //TBD, number to multiply a degree target by to get that degree on the hood. will need other consts for the hood as well
    private final static double hoodMin = 12; //tbd, hood min and max in degrees
    private final static double hoodMax = 24;
    private final static double ticksPerRotation = 28; //1:1 sprocket
    private static final boolean nectar = false; //Change + un-comment out other code later

    public static double kS = 0;

    public static double kV = 0;

    public static double kA = 0;

    public static double P = 0;

    public static double I = 0;

    public static double D = 0;

    public static void init(OpMode opmode) {
        flywheel = opmode.hardwareMap.get(DcMotorEx.class, "flywheel"); //Port 1 on expansion hub
        hoodAngleServo = opmode.hardwareMap.get(Servo.class, "hoodAngler"); //Port 5 on expansion hub
       // flywheelSlideServo = opmode.hardwareMap.get(Servo.class, "flywheelSlider"); //Port _ on ___ hub
     //   hoodSlideServo = opmode.hardwareMap.get(Servo.class, "hoodSlider"); //Port _ on ___ hub
//        hubVoltageSensor = opmode.hardwareMap.get(VoltageSensor.class, "Control Hub");

        pollenFFController = new FeedForward(kS, kV, kA);
        pollenPIDController = new PID(P, I, D);

        nectarFFController = new FeedForward(0.0, 0.0, 0.0); //Not using yet
        nectarPIDController = new PID(0.0, 0.0, 0.0);

        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setPower(0.0);

//        nectar = false;

        Shooter.opmode = opmode;
    }

    public static void prime() {
        hoodAngle = hoodMax; //reset incase already on min
        setHoodAngle(hoodMin);

        shooterPower = 0.0;

//        flywheelSlideServo.setPosition(flywheelPollenPos);
//        hoodSlideServo.setPosition(hoodPollenPos);
//        nectar = false;
    }

    public static void setHoodAngle(double angle) {
        angle = (Range.clip(angle, hoodMin, hoodMax) - hoodMin) * hoodRatio;
        if (hoodAngle != angle){
            hoodAngleServo.setPosition(angle/360); //may to need to reverse to be counting down from 1 based on orientation of servo
            hoodAngle = angle;
        }
    }

    public static void setFlywheelPower(double power) {
        power = Range.clip(power, -1, 1);
        if (shooterPower != power) {
            shooterPower = power;
            flywheel.setPower(power);
        }
    }

    private static void updateFlywheel(double deltaTime) {
//        double voltage = hubVoltageSensor.getVoltage();
        if (targetRPM == 0) { //Allow setting to 0 to just drop power, rather than waste power trying to force the flywheel back to a stop
            setFlywheelPower(0.0);
            return;
        }
        if (nectar) {
            setFlywheelPower(nectarFFController.step(targetRPM, flywheel.getVelocity() / ticksPerRotation, deltaTime));
        } else {
            setFlywheelPower(pollenFFController.step(targetRPM, flywheel.getVelocity() / ticksPerRotation, deltaTime));
        }
    }

    public static void setTargetRPM(double rpm) {
        targetRPM = rpm;
    }

    public static double getCurrentRPM() { // this is not used for controlling the flywheel as smoothing the rpm is not ideal for control systems
        return flywheel.getVelocity() / ticksPerRotation;
    }

    public static boolean canShoot() {
        return Math.abs(getCurrentRPM() - targetRPM) < errorMargin;
    }

    public static void update(double deltaTime) {
        //TEMP for tuning
        pollenFFController.kS = kS;
        pollenFFController.kV = kV;
        pollenFFController.kA = kA;
        pollenPIDController.p = P;
        pollenPIDController.i = I;
        pollenPIDController.d = D;

        setHoodAngle(hoodMax);

        //TODO: Add logic for taking getTargetRPM() and running PID with setFlywheelPower()
        //TODO: Make it have 2 PIDS, one for pollen and one for nectar (switch based on the nectar boolean, and make sure switch is smooth and doesnt cause any issues if it swaps frequently). Nectar will always be false for now, so don't worry about tuning that yet
        updateFlywheel(deltaTime);

        //Temp for tuning only, remove later
        if (opmode.gamepad1.right_bumper) {setTargetRPM(300);} else {setTargetRPM(0);}

        opmode.telemetry.addData("Shooter Target RPM", targetRPM);
        opmode.telemetry.addData("Shooter Current RPM", getCurrentRPM());

        opmode.telemetry.addData("Flywheel Power", shooterPower);
        opmode.telemetry.addData("Flywheel Amps", flywheel.getCurrent(CurrentUnit.AMPS));
        opmode.telemetry.addData("Hood Angle Position", hoodAngle);
        opmode.telemetry.addData("Hood Angle Degrees", hoodAngle / hoodRatio + hoodMin);
        opmode.telemetry.addData("Nectar Mode", nectar);
    }

    public static Action autoSetTargetRPM(double RPM) {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setTargetRPM(RPM);
                return false;
            }
        };
    }

    public static Action autoUpdateLoop() {
        return new Action() {
            private double deltaTime = 1.0; //Not 0 as that might cause some things to be multiplied by 0 that shouldn't be
            private double lastLoopTimestamp = 0.0;
            private ElapsedTime shooterLooptimer = new ElapsedTime();
            private boolean first = true;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    shooterLooptimer.reset();
                    first = false;
                }
                updateFlywheel(deltaTime);
                deltaTime = shooterLooptimer.milliseconds() - lastLoopTimestamp;
                lastLoopTimestamp = shooterLooptimer.milliseconds();
                return true;
            }
        };
    }

    public static Action autoWaitForSpeed(double margin, double timeOut) {
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                return Math.abs(targetRPM - getCurrentRPM()) > margin && timer.seconds() <= timeOut; //Timeout, can change
            }
        };
    }









    //Following are for split shooter, do not use for now (Also: decide on encoder with hood switching, seems like it may not be needed ATM)
//
//    public static void nectarMode() {
//        if (!nectar) {
//            flywheelSlideServo.setPosition(flywheelNectarPos);
//            hoodSlideServo.setPosition(hoodNectarPos);
//            nectar = true;
//        }
//    }
//
//    public static void pollenMode() {
//        if (nectar) {
//            flywheelSlideServo.setPosition(flywheelPollenPos);
//            hoodSlideServo.setPosition(hoodPollenPos);
//            nectar = false;
//        }
//    }
}