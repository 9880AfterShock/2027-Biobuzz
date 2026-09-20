package org.firstinspires.ftc.teamcode.Sensors;

import static org.firstinspires.ftc.teamcode.MecanumDrive.PARAMS;

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

    public static void init(OpMode opmode, HardwareMap hardwareMap, Pose2d pose) {
        localizer = new PinpointLocalizer(hardwareMap, PARAMS.inPerTick, pose);
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

    public static void update() {
        localizer.update();
    }

    public static void draw(TelemetryPacket packet) {
        packet.fieldOverlay().setStroke("#00ffee");
        Drawing.drawRobot(packet.fieldOverlay(), localizer.getPose());
    }

    public static double normalize(double angle, double rotation){
        return ((angle % rotation) + rotation) % rotation;
    }
}