package org.firstinspires.ftc.teamcode.Mechanisms.Intaking;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class MatchLoader {
    private static Servo matchLoaderServo;
    private static OpMode opmode;
    public static boolean deployed;
    public static final double retractedPos = 0.0;
    public static final double deployedPos = 1.0;

    public static void init(OpMode opmode) {
        matchLoaderServo = opmode.hardwareMap.get(Servo.class, "matchLoader"); //Port _ on ___ hub
        MatchLoader.opmode = opmode;
    }

    public static void prime() {
        matchLoaderServo.setPosition(retractedPos);
        deployed = false;
    }

    public static void deploy() {
        if (!deployed) {
            matchLoaderServo.setPosition(deployedPos);
            deployed = true;
        }
    }

    public static void retract() {
        if (deployed) {
            matchLoaderServo.setPosition(retractedPos);
            deployed = false;
        }
    }

    public static void update(boolean deploy) {
        if (deploy){
            deploy();
        } else {
            retract();
        }
        opmode.telemetry.addData("MatchLoader Deployed", deployed);
    }

    public static Action autoDeploy() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                deploy();
                return false; //TODO: add 4th wire checking
            }
        };
    }
    public static Action autoRetract() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                retract();
                return false; //TODO: add 4th wire checking
            }
        };
    }
}