package org.firstinspires.ftc.teamcode.Mechanisms.Templates;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class PositionServo {
    private static Servo servo;
    private static OpMode opmode;
    public static double targetPos;
    public static boolean enabled;

    public static final double pos1 = 0.0;
    public static final double pos2 = 1.0;

    public static void init(OpMode opmode) {
        servo = opmode.hardwareMap.get(Servo.class, "servo"); //Port _ on ___ hub
        PositionServo.opmode = opmode;
        //Put in starting setting of pos and powered, make function to call on start if needed
    }

    public static void pos1() {
        if (targetPos != pos1) {
            servo.setPosition(pos1);
            targetPos = pos1;
        }
    }

    public static void pos2() {
        if (targetPos != pos2) {
            servo.setPosition(pos2);
            targetPos = pos2;
        }
    }

    public static void setPowered(boolean powered) {
        if (powered != enabled){
            if (powered) {
                servo.getController().pwmEnable();
            } else {
                servo.getController().pwmDisable();
            }
        }
    }

    public static void update() {
        opmode.telemetry.addData("Servo Target Pos", targetPos);
        opmode.telemetry.addData("Servo PWM Status", enabled);
    }
}