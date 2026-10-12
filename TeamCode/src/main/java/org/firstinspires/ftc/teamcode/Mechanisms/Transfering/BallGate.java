package org.firstinspires.ftc.teamcode.Mechanisms.Transfering;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class BallGate {
    private static Servo gateServo;
    private static OpMode opmode;
    public static boolean open = false;
    public static final double openPos = 0.0;
    public static final double closedPos = 1.0;

    public static void init(OpMode opmode) {
        gateServo = opmode.hardwareMap.get(Servo.class, "gate"); //Port _ on ___ hub
        BallGate.opmode = opmode;
    }

    public static void prime() {
        gateServo.setPosition(closedPos);
        open = false;
    }

    public static void open() {
        if (!open) {
            gateServo.setPosition(openPos);
            open = true;
        }
    }

    public static void close() {
        if (open) {
            gateServo.setPosition(closedPos);
            open = false;
        }
    }

    public static void update(boolean shoot) {
        //TODO: make it go when shooter can fire and driver presses trigger, and then close when driver stops shooting or empty + cannot shoot

        opmode.telemetry.addData("Shooter Gate Open", open);
    }

    public static Action autoOpen() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                open();
                return false;
            }
        };
    }
    public static Action autoClose() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                close();
                return false;
            }
        };
    }
}