package org.firstinspires.ftc.teamcode.Mechanisms.Shooting;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
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
    private static final double minRotation = -200; //TBD
    private static final double maxRotation = 200; //TBD
    private static final double absOffset = 0.0; //Set to offset of a raw abs reading and center position (home)

    public static void init(OpMode opmode) {
        turret = opmode.hardwareMap.get(DcMotorEx.class, "turret"); //Port 0 on expansion hub
        //Rev encoder is in encoder port for the turret motor

        //TEMP FOR LOCK
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setPower(0.0);

        Turret.opmode = opmode;
    }

    public static void prime() {
        //TEMP locking code
        turret.setTargetPosition(0);
        turret.setPower(1.0);

        //TODO: read last encoder (or 0 if after auto) + abs encoder to get exact pos
    }

    public static void setTurretPower(double power) {
        power = Range.clip(power, -1, 1);
        if (turretPower != power) {
            turretPower = power;
            turret.setPower(power);
        }
    }

    public static void setTurretTarget(double target) { //Degrees
        targetPos = Range.clip(target, minRotation, maxRotation);
    }

    public static double calcTurretAngle(Pose2d robotPos, Pose2d goalPos) {
        //TODO: make this work, calc offset of turret on robot
        return 0.0;
    }

    public static void update() {
        //TODO: encoder tracking, abs and quad, read and reset and update

        //TODO: implement PID on setTurretPower using targetPos
        opmode.telemetry.addData("Turret REV Current Ticks", turret.getCurrentPosition());

        opmode.telemetry.addData("Turret Target", targetPos);
        opmode.telemetry.addData("Turret Power", turretPower);
        opmode.telemetry.addData("Turret Amps", turret.getCurrent(CurrentUnit.AMPS));
    }
}