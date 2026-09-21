package org.firstinspires.ftc.teamcode.Mechanisms.Templates;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class PositionServo {
    private static Servo servo;
    private static OpMode opmode;
    private static double targetPos;
    private static boolean enabled;

    public static double pos1 = 0.0; //TODO: change to private final after tuned
    public static double pos2 = 1.0;

    public static void init(OpMode opmode) {
        enabled = true;
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
                enabled = true;
            } else {
                servo.getController().pwmDisable();
                enabled = false;
            }
        }
    }

    public static void update() {
        opmode.telemetry.addData("Servo Target Pos", targetPos);
        opmode.telemetry.addData("Servo PWM Status", enabled);
    }
}