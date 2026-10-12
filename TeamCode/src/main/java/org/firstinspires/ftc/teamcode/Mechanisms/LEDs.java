package org.firstinspires.ftc.teamcode.Mechanisms;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Enums.LEDColor;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;

public class LEDs {
    private static DigitalChannel frontLeftLEDGreen;
    private static DigitalChannel frontRightLEDGreen;
    private static DigitalChannel backLeftLEDGreen;
    private static DigitalChannel backRightLEDGreen;

    private static DigitalChannel frontLeftLEDRed;
    private static DigitalChannel frontRightLEDRed;
    private static DigitalChannel backLeftLEDRed;
    private static DigitalChannel backRightLEDRed;


    private static LEDColor frontLeftLED = LEDColor.OFF;
    private static LEDColor frontRightLED = LEDColor.OFF;
    private static LEDColor backLeftLED = LEDColor.OFF;
    private static LEDColor backRightLED = LEDColor.OFF;

    private static OpMode opmode;

    public static void init(OpMode opmode) { //TODO: this absurd config
        frontLeftLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "frontLeftLEDGreen"); //Port n on ___ hub
        frontLeftLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "frontLeftLEDRed"); //Port n+1 on ___ hub
        frontRightLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "frontRightLEDGreen"); //Port n on ___ hub
        frontRightLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "frontRightLEDRed"); //Port n+1 on ___ hub
        backLeftLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "backLeftLEDGreen"); //Port n on ___ hub
        backLeftLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "backLeftLEDRed"); //Port n+1 on ___ hub
        backRightLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "backRightLEDGreen"); //Port n on ___ hub
        backRightLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "backRightLEDRed"); //Port n+1 on ___ hub

        frontLeftLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        frontLeftLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        frontRightLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        frontRightLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        backLeftLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        backLeftLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        backRightLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        backRightLEDRed.setMode(DigitalChannel.Mode.OUTPUT);

        LEDs.opmode = opmode;
    }

    public static void setColor(DigitalChannel LED, LEDColor newColor) {
        //TODO: make work
        LED.setState(true); //just turns on
    }

    public static void update() { //TODO: add reading states, ex. blinking, cycling, solid X color, ect.

        opmode.telemetry.addData("LEDs Programmed thus far", 0); //TODO: add telem for LEDS
    }

    public static Action autoLEDs() { //TODO: add parameters
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                //TODO: add behavior
                return false/* && timer.seconds() <= 3.0*/; //Timeout, can change
            }
        };
    }
}