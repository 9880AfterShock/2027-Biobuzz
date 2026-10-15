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

@Config
public class Shooter {
    private static DcMotorEx flywheel;
    private static VoltageSensor hubVoltageSensor;
    private static Servo hoodAngleServo; //may need to split off a nectar one
    private static Servo flywheelSlideServo;
    private static Servo hoodSlideServo;
    private static OpMode opmode;
    private static Controller nectarController;
    private static Controller pollenController;
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
    private static boolean nectar; //maybe make final (false) and comment out other code about switching modes for now if needed

    public static void init(OpMode opmode) {
        flywheel = opmode.hardwareMap.get(DcMotorEx.class, "flywheel"); //Port 1 on expansion hub
        hoodAngleServo = opmode.hardwareMap.get(Servo.class, "hoodAngler"); //Port 5 on expansion hub
       // flywheelSlideServo = opmode.hardwareMap.get(Servo.class, "flywheelSlider"); //Port _ on ___ hub
     //   hoodSlideServo = opmode.hardwareMap.get(Servo.class, "hoodSlider"); //Port _ on ___ hub
//        hubVoltageSensor = opmode.hardwareMap.get(VoltageSensor.class, "Control Hub");
        nectarController = new FeedForward(0.1, 0.1, 0.0);
        pollenController = new FeedForward(0.1, 0.01, 0.0);

        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setPower(0.0);

        nectar = false;

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

    private static void updateFlywheel() {
//        double voltage = hubVoltageSensor.getVoltage();
        if (nectar) {
            setFlywheelPower(nectarController.step(targetRPM, flywheel.getVelocity() / ticksPerRotation, 1.0)); //TODO: add delta time
        } else {
            setFlywheelPower(pollenController.step(targetRPM, flywheel.getVelocity() / ticksPerRotation, 1.0)); //TODO: add delta time
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

    public static void update() {
        setHoodAngle(hoodMax);

        //TODO: Add logic for taking getTargetRPM() and running PID with setFlywheelPower()
        //TODO: Make it have 2 PIDS, one for pollen and one for nectar (switch based on the nectar boolean, and make sure switch is smooth and doesnt cause any issues if it swaps frequently). Nectar will always be false for now, so don't worry about tuning that yet
   //     updateFlywheel();
        setFlywheelPower(.3);

        opmode.telemetry.addData("Flywheel Power", shooterPower);
        opmode.telemetry.addData("Flywheel Amps", flywheel.getCurrent(CurrentUnit.AMPS));
        opmode.telemetry.addData("Hood Angle Position", hoodAngle);
        opmode.telemetry.addData("Hood Angle Degrees", hoodAngle / hoodRatio + hoodMin);
        opmode.telemetry.addData("Nectar Mode", nectar);
    }

    public static Action autoUpdateLoop() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
//                updateFlywheel();
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

    public static void nectarMode() {
        if (!nectar) {
            flywheelSlideServo.setPosition(flywheelNectarPos);
            hoodSlideServo.setPosition(hoodNectarPos);
            nectar = true;
        }
    }

    public static void pollenMode() {
        if (nectar) {
            flywheelSlideServo.setPosition(flywheelPollenPos);
            hoodSlideServo.setPosition(hoodPollenPos);
            nectar = false;
        }
    }
}