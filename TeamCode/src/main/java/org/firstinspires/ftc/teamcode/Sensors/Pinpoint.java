package org.firstinspires.ftc.teamcode.Sensors;

import static org.firstinspires.ftc.teamcode.MecanumDrive.PARAMS;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Drawing;
import org.firstinspires.ftc.teamcode.Localizer;
import org.firstinspires.ftc.teamcode.PinpointLocalizer;

public class Pinpoint {
    private static Localizer localizer;
    private static ElapsedTime sinceReset = new ElapsedTime();
    private static OpMode opmode;
    private static double yawOffsetOverride = 0.0; //Radians

    //For calculating corner pose alignments, inches from center of robot to wall when braced against on that side
    public static final double frontDistance = 0.0393701 * (291.10703 + 4);
    public static final double leftDistance = 0.0393701 * (215.90000 + 15); //TODO: make sure the side values reflect matchloader (plate is the ~215)
    public static final double rightDistance = 0.0393701 * (215.90000 + 15); //TODO: make sure the side values reflect matchloader (plate is the ~215)
    public static final double backDistance = 0.0393701 * (213.50939 + 4);
    //Field Constants
    private static final double fieldSize = 72; //Distance in both directions from center of the field to the walls
    private static final double north = Math.toRadians(90.0);
    private static final double south = Math.toRadians(270.0); //-90 if you normalize -pi to pi instead of 0 to 2pi
    private static final double east = Math.toRadians(0.0);
    private static final double west = Math.toRadians(180.0);

    public static void init(OpMode opmode, HardwareMap hardwareMap, Pose2d pose) {
        localizer = new PinpointLocalizer(hardwareMap, PARAMS.inPerTick, pose); //Bus 0 on Control Hub
        sinceReset.reset();
        yawOffsetOverride = 0.0;
        Pinpoint.opmode = opmode;
    }

    public static Pose2d getPose() {
        return localizer.getPose();
    }

    public static double getYawRadians() {
        return normalize(localizer.getPose().heading.toDouble() + yawOffsetOverride, Math.PI*2);
    }

    public static void setOverrideYaw(){
        yawOffsetOverride = -localizer.getPose().heading.toDouble();
    }

    public static double getStalenessSeconds(){
        return sinceReset.seconds();
    }

    public static void setPose(Pose2d newPose) {
        localizer.setPose(newPose);
        sinceReset.reset();
        yawOffsetOverride = 0.0;
    }

    public static Pose2d snapPose(Pose2d approximatePos) { //TODO: make sure when approx. pos is fed it using any driver IMU correction?
        double heading = (Math.round(approximatePos.heading.toDouble() / (Math.PI/2)) % 4) * (Math.PI/2);
        double x;
        double y;
        double northOffset;
        double southOffset;
        double eastOffset;
        double westOffset;
        if (heading == north) {
            northOffset = frontDistance;
            southOffset = backDistance;
            eastOffset = rightDistance;
            westOffset = leftDistance;
        } else
        if (heading == south) {
            northOffset = backDistance;
            southOffset = frontDistance;
            eastOffset = leftDistance;
            westOffset = rightDistance;
        } else
        if (heading == east) {
            northOffset = leftDistance;
            southOffset = rightDistance;
            eastOffset = frontDistance;
            westOffset = backDistance;
        } else
        /*if (heading == west) */{
            northOffset = rightDistance;
            southOffset = leftDistance;
            eastOffset = backDistance;
            westOffset = frontDistance;
        }

        if (approximatePos.position.x > 0){
            x = fieldSize - eastOffset;
        } else {
            x = -fieldSize + westOffset;
        }
        if (approximatePos.position.y > 0){
            y = fieldSize - northOffset;
        } else {
            y = -fieldSize + southOffset;
        }

        return new Pose2d(x, y, heading);
    }

    public static void update(boolean snap) {
        localizer.update();
        if (snap){
            localizer.setPose(snapPose(localizer.getPose()));
        }

        opmode.telemetry.addData("PINPOINT Yaw", Math.toDegrees(getYawRadians()));
        opmode.telemetry.addData("PINPOINT Yaw Offset Override", Math.toDegrees(yawOffsetOverride));
        opmode.telemetry.addData("PINPOINT Raw Yaw", Math.toDegrees(localizer.getPose().heading.toDouble()));

        TelemetryPacket drawing = new TelemetryPacket();
        draw(drawing);
        FtcDashboard.getInstance().sendTelemetryPacket(drawing);
    }

    public static void draw(TelemetryPacket packet) {
        packet.fieldOverlay().setStroke("#00ffee");
        Drawing.drawRobot(packet.fieldOverlay(), localizer.getPose());
    }

    public static double normalize(double angle, double rotation){
        return ((angle % rotation) + rotation) % rotation;
    }
}