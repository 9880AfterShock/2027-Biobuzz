package org.firstinspires.ftc.teamcode.Mechanisms.Shooting;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Config
public class Turret {
    private static DcMotorEx turret;
    private static OpMode opmode;
    private static double turretPower;
    private static double targetPos;

    public static void init(OpMode opmode) {
        turret = opmode.hardwareMap.get(DcMotorEx.class, "turret"); //Port _ on ___ hub

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setPower(0.0);

        Turret.opmode = opmode;
    }

    public static void setTurretPower(double power) {
        power = Range.clip(power, -1, 1);
        if (turretPower != power) {
            turretPower = power;
            turret.setPower(power);
        }
    }

    public static void update() {
        opmode.telemetry.addData("Turret Power", turretPower);
        opmode.telemetry.addData("Turret Amps", turret.getCurrent(CurrentUnit.AMPS));
    }
}