package org.firstinspires.ftc.teamcode.Mechanisms.Shooting;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Utils.Controllers.PID;

@Config
public class Turret {
    private static DcMotorEx turret;
    private static OpMode opmode;
    private static double turretPower;
    private static double targetPos;
    private static final double minRotation = -150; //TBD
    private static final double maxRotation = 150; //TBD
    private static final double absOffset = 0.0; //Set to offset of a raw abs reading and center position (home)

    private static PID turretPIDController;
    public static double P = 0.0;
    public static double I = 0;
    public static double D = 0;
    public static double kStatic = 0.16; //Temp for dashboard
    public static double errorMargin = 0.5; //Dead band for kStatic

    public static void init(OpMode opmode) {
        turret = opmode.hardwareMap.get(DcMotorEx.class, "turret"); //Port 0 on expansion hub, Rev Throughbore is plugged in instead of normal
        //Rev encoder is in encoder port for the turret motor

        turretPIDController = new PID(P, I, D);

        //TEMP FOR LOCK
//        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        turret.setTargetPosition(0);
//        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //This means no encoder to enforce internal PID, not that we do not get readings

        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setPower(0.0);

        Turret.opmode = opmode;
    }

    public static void prime() {
        //TEMP locking code

        //TODO: read last encoder (or 0 if after auto) + abs encoder to get exact pos //Caden, I will do this later
    }

    public static void setTurretPower(double power) {
        power = -Range.clip(power, -1, 1);
        if (turretPower != power) {
            turretPower = power;
            turret.setPower(power);
        }
    }

    public static void setTurretTarget(double target) { //Degrees
        targetPos = Range.clip(target, minRotation, maxRotation);
    }

    public static double getCurrentPosition(){ //Doing conversion here because it means the PID will not need to be modified on a different encoder
        return turret.getCurrentPosition() * (36.0/105.0) / 8192.0 * 360; //Multiply by the pulley ratio, divide by encoder resolution, times by 360 to get degrees
    }

    public static double calcTurretAngle(Pose2d robotPos, Pose2d goalPos) {
        //TODO: make this work, calc offset of turret on robot
        //Caden i'll take care of this, need to wait on some tuning data first
        return 0.0;
    }

    public static void update(double deltaTime) {
        //TODO: encoder tracking, abs and quad, read and reset and update
        //Caden I need to get smth figured out on the sensorange first, just take the existing getPosition function for now


        //TODO: CADEN, implement PID on setTurretPower using targetPos and getTurretPosition();
        //I know the getTurretPosition(); is kind of useless right now but it will change later to use ABS and a reset, so please just use it for now

        double staticBoost = kStatic * Math.signum(targetPos - getCurrentPosition());
        if (Math.abs(targetPos - getCurrentPosition()) < errorMargin) {
            staticBoost = 0.0;
        }
        setTurretPower(turretPIDController.step(targetPos, getCurrentPosition(), deltaTime) + staticBoost);

        //We want a PD with but NOT a feedforward (no I)
        //We also want a static friction constant (easy to tune, test just it until the turret TWITCHES, not nessasarily MOVES, that goes in either direction, and is 0 if the target is within like 0.5 degrees


        opmode.telemetry.addData("Turret Target Rotation", targetPos);
        opmode.telemetry.addData("Turret Current Rotation", getCurrentPosition());
        opmode.telemetry.addData("Turret Power", turretPower);
        opmode.telemetry.addData("Turret Amps", turret.getCurrent(CurrentUnit.AMPS));
    }

    public static void updateTest(double x, double y, boolean change, double deltaTime) { //Ignore this, temp wrapper for the test program
        turretPIDController.p = P;
        turretPIDController.i = I;
        turretPIDController.d = D;

        if (change) {
            if (x == 0 && y == 0) {
                setTurretTarget(0);
            } else {
                setTurretTarget(Math.toDegrees(Math.atan2(-x,-y)));
            }
        }

        update(deltaTime);
    }

    //TODO: add tick actions with delta time in auto
    //Caden I should probably do this
}