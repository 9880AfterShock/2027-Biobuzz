package org.firstinspires.ftc.teamcode.Mechanisms.Templates;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Config
public class PowerMotor {
    private static DcMotorEx motor;
    private static OpMode opmode;
    private static double targetPower;

    public static void init(OpMode opmode) {
        motor = opmode.hardwareMap.get(DcMotorEx.class, "motor"); //Port _ on ___ hub

        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
//        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setPower(0.0);

        PowerMotor.opmode = opmode;
    }

    public static void setMotorPower(double power) {
        power = Range.clip(power, -1, 1);
        if (targetPower != power) {
            targetPower = power;
            motor.setPower(power);
        }
    }

    public static void update() {
        opmode.telemetry.addData("Motor Power", targetPower);
        opmode.telemetry.addData("Motor Amps", motor.getCurrent(CurrentUnit.AMPS));
    }
}