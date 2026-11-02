package org.firstinspires.ftc.teamcode.Mechanisms.Shooting;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Sensors.Pinpoint;
import org.firstinspires.ftc.teamcode.Utils.Controllers.ChoiceController;
import org.firstinspires.ftc.teamcode.Utils.Controllers.PID;

@Config
public class Turret {
    private static final double turretPositionOffset = -85.38998 * 0.0393701; //How far (currently backwards) the turret is from the center of the robot in inches
    //Finals for each hive pos? or just flip based on color/side?
    private static DcMotorEx turret;
    private static OpMode opmode;
    private static double turretPower;
    private static double targetPos;
    private static final double minRotation = -150; //TBD
    private static final double maxRotation = 150; //TBD
    private static final double absOffset = 0.0; //Set to offset of a raw abs reading and center position (home)

    private static PID nearTurretPIDController;
    private static PID farTurretPIDController;
    private static ChoiceController turretController;
    public static double nearP = 0.003;
    public static double nearI = 0;
    public static double nearD = 0;
    public static double farP = 0.003;
    public static double farI = 0;
    public static double farD = 0;
    public static double switchValue = 15; //Value where the PID switches
    public static double kStatic = 0.09; //Temp for dashboard
    public static double errorMargin = 0.5; //Dead band for kStatic
    public static Pose2d goalPos = new Pose2d(440.890786 * 0.0393701, 323.850000 * 0.0393701, 0.0); //Blue goal audience side at the moment

    public static void init(OpMode opmode) {
        turret = opmode.hardwareMap.get(DcMotorEx.class, "turret"); //Port 0 on expansion hub, Rev Throughbore is plugged in instead of normal
        //Rev encoder is in encoder port for the turret motor

        nearTurretPIDController = new PID(nearP, nearI, nearD);
        farTurretPIDController = new PID(farP, farI, farD);
        turretController = new ChoiceController();
        turretController.addController(nearTurretPIDController,switchValue);
        turretController.addController(farTurretPIDController, Double.MAX_VALUE);

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

    private static void setTurretPower(double power) {
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

    public static double calcTurretAngle(Pose2d robotPos, Pose2d goalPos) { //In radians, be careful
        return Math.atan2(goalPos.position.y - calcTurretPosition(robotPos).position.y, goalPos.position.x - calcTurretPosition(robotPos).position.x);
    }

    public static double calcGoalDistance(Pose2d robotPos, Pose2d goalPos) {
        return Math.hypot(goalPos.position.x - calcTurretPosition(robotPos).position.x, goalPos.position.y - calcTurretPosition(robotPos).position.y);
    }

    public static Pose2d calcTurretPosition(Pose2d robotPos){
        double x = (turretPositionOffset * Math.cos(robotPos.heading.toDouble())) + robotPos.position.x;
        double y = (turretPositionOffset * Math.sin(robotPos.heading.toDouble())) + robotPos.position.y;
        return new Pose2d(x, y,  robotPos.heading.toDouble() + Math.toRadians(getCurrentPosition()) + Math.toRadians(180));
    }

    private static void updateTurretPower(double deltaTime) {
        double staticBoost = kStatic * Math.signum(targetPos - getCurrentPosition());
        if (Math.abs(targetPos - getCurrentPosition()) < errorMargin) {
            staticBoost = 0.0;
        }
        setTurretPower(turretController.step(targetPos, getCurrentPosition(), deltaTime) + staticBoost);
    }

    public static void update(double deltaTime) {
        //TODO: encoder tracking, abs and quad, read and reset and update

        updateTurretPower(deltaTime);

        opmode.telemetry.addData("Turret Target Rotation", targetPos);
        opmode.telemetry.addData("Turret Current Rotation", getCurrentPosition());
        opmode.telemetry.addData("Turret Power", turretPower);
        opmode.telemetry.addData("Turret Amps", turret.getCurrent(CurrentUnit.AMPS));
    }

    public static void updateTest(double x, double y, boolean change, double deltaTime) { //Ignore this, temp wrapper for the test program
        nearTurretPIDController.p = nearP;
        nearTurretPIDController.i = nearI;
        nearTurretPIDController.d = nearD;

        farTurretPIDController.p = farP;
        farTurretPIDController.i = farI;
        farTurretPIDController.d = farD;


        //TODO: fix aiming with the wrong side of the turret (may have fixed, need to test)
        setTurretTarget(Math.toDegrees(normalizeNegative(calcTurretAngle(Pinpoint.getPose(), goalPos) - (Pinpoint.getPose().heading.toDouble() + Math.PI*2), Math.PI*2)));

//        if (change) {
//            if (x == 0 && y == 0) {
//                setTurretTarget(0);
//            } else {
//                setTurretTarget(Math.toDegrees(Math.atan2(-x,-y)));
//            }
//        }
        update(deltaTime);

        opmode.telemetry.addData("GOAL DISTANCE", calcGoalDistance(Pinpoint.getPose(), goalPos));
        opmode.telemetry.addData("GOAL ANGLE", calcTurretAngle(Pinpoint.getPose(), goalPos));
    }
    //TODO: add tick actions with delta time in auto
    //Caden I should probably do this
    public static double normalizeNegative(double angle, double rotation){
        return ((angle % rotation) + rotation) % rotation - (rotation / 2);
    }
}