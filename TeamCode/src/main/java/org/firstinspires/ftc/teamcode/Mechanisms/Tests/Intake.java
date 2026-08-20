package org.firstinspires.ftc.teamcode.Mechanisms.Tests;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class Intake {
    private static DcMotorEx roller;
    private static Servo servo;
    private static OpMode opmode;
    public static double targetPos;
    public static double targetPower;

    public static final double retractedPos = 0.0;
    public static final double extendedPos = 1.0;
    public static final double intakingPower = 1.0;
    public static final double ejectingPower = -1.0;
    public static final double idlePower = 0.0;

    public static void init(OpMode opmode) {
        roller = opmode.hardwareMap.get(DcMotorEx.class, "intakeRoller"); //Port _ on ___ hub
        servo = opmode.hardwareMap.get(Servo.class, "intakeServo"); //Port _ on ___ hub

        roller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        roller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        roller.setDirection(DcMotorSimple.Direction.FORWARD);
//        roller.setDirection(DcMotorSimple.Direction.REVERSE);

        Intake.opmode = opmode;
    }

    public static void retract() {
        if (targetPos != retractedPos) {
            servo.setPosition(retractedPos);
            targetPos = retractedPos;
        }
    }

    public static void extend() {
        if (targetPos != extendedPos) {
            servo.setPosition(extendedPos);
            targetPos = extendedPos;
        }
    }

    public static void update(boolean intaking, boolean ejecting, boolean restArm) {
        if (intaking || restArm){
            extend();
        } else{
            retract();
        }

        if (intaking){
            if (targetPower != intakingPower){
                targetPower = intakingPower;
                roller.setPower(intakingPower);
            }
        } else {
            if (ejecting){
                if (targetPower != ejectingPower){
                    targetPower = ejectingPower;
                    roller.setPower(ejectingPower);
                }
            } else {
                if (targetPower != idlePower){
                    targetPower = idlePower;
                    roller.setPower(idlePower);
                }
            }
        }

        opmode.telemetry.addData("Intaking", intaking);
        opmode.telemetry.addData("Resting", intaking);
        opmode.telemetry.addData("Ejecting", intaking);
    }
}