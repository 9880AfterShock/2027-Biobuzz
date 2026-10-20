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

    private static double lastBlink = 0; //TODO: remove this temp solution for blinking all

    private static OpMode opmode;

    public static void init(OpMode opmode) { //TODO: this absurd config
        frontLeftLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "frontLeftLEDGreen"); //Port 2 on control hub
        frontLeftLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "frontLeftLEDRed"); //Port 3 on control hub
        frontRightLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "frontRightLEDGreen"); //Port 0 on expansion hub
        frontRightLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "frontRightLEDRed"); //Port 1 on expansion hub
        backLeftLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "backLeftLEDGreen"); //Port 4 on control hub
        backLeftLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "backLeftLEDRed"); //Port 5 on control hub
        backRightLEDGreen = opmode.hardwareMap.get(DigitalChannel.class, "backRightLEDGreen"); //Port 2 on expansion hub
        backRightLEDRed = opmode.hardwareMap.get(DigitalChannel.class, "backRightLEDRed"); //Port 3 on expansion hub

        frontLeftLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        frontLeftLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        frontRightLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        frontRightLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        backLeftLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        backLeftLEDRed.setMode(DigitalChannel.Mode.OUTPUT);
        backRightLEDGreen.setMode(DigitalChannel.Mode.OUTPUT);
        backRightLEDRed.setMode(DigitalChannel.Mode.OUTPUT);

        lastBlink = 0.0;

        LEDs.opmode = opmode;
    }

    public static void setColor(DigitalChannel LEDGreen, DigitalChannel LEDRed, LEDColor newColor, LEDColor oldColor) {
        if (newColor.greenPower() != oldColor.greenPower()) {
            LEDGreen.setState(newColor.greenPower());
        }
        if (newColor.redPower() != oldColor.redPower()) {
            LEDGreen.setState(newColor.redPower());
        }
    }

    private static void setFrontLeftColor(LEDColor color){ //TODO: add proper rate limiting of 5 hertz to make it always legal (vars for last timestamp swtiched to avoid looptime impacting)
        setColor(frontLeftLEDGreen, frontLeftLEDRed, color, frontLeftLED);
        frontLeftLED = color;
    }

    private static void setFrontRightColor(LEDColor color){
        setColor(frontRightLEDGreen, frontRightLEDRed, color, frontRightLED);
        frontRightLED = color;
    }

    private static void setBackLeftColor(LEDColor color){
        setColor(backLeftLEDGreen, backLeftLEDRed, color, backLeftLED);
        backLeftLED = color;
    }

    private static void setBackRightColor(LEDColor color){
        setColor(backRightLEDGreen, backRightLEDRed, color, backRightLED);
        backRightLED = color;
    }

    public static void turnOff(){
        setFrontLeftColor(LEDColor.OFF);
        setFrontRightColor(LEDColor.OFF);
        setBackLeftColor(LEDColor.OFF);
        setBackRightColor(LEDColor.OFF);
    }

    public static void update(LEDColor allColor, boolean blinking, ElapsedTime runtime) { //TODO: add more reading states, ex. blinking, cycling, spinning, ect.
        setFrontLeftColor(allColor);
        setFrontRightColor(allColor);
        setBackLeftColor(allColor);
        setBackRightColor(allColor);

        opmode.telemetry.addData("Front Left", frontLeftLED.state());
        opmode.telemetry.addData("Front Right", frontRightLED.state());
        opmode.telemetry.addData("Back Left", backLeftLED.state());
        opmode.telemetry.addData("Back Right", backRightLED.state());
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