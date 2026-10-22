package org.firstinspires.ftc.teamcode.Sensors.Vision;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.Enums.SideColor;

import java.util.List;

public class Limelight {
    private static OpMode opmode;
    private static Limelight3A limelight;
    private static LLResult lastResult;
    public static boolean hiveFacing = false;
    private static final double METER_TO_INCH = 39.3701;

    public static void initDetection(OpMode opmode) {
        limelight = opmode.hardwareMap.get(Limelight3A.class, "limelight"); //USB 3.0 on control hub
        // telemetry.setMsTransmissionInterval(11); //TODO: decide on this
        limelight.pipelineSwitch(0);

        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second) TODO: decide this

        limelight.start();

        Limelight.opmode = opmode;

        hiveFacing = false;
    }

    public static void update(){
        lastResult = limelight.getLatestResult();

        //Smaller update commands can be here if we want

        LLStatus status = limelight.getStatus();
        opmode.telemetry.addData("Name", "%s",
                status.getName());
        opmode.telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                status.getTemp(), status.getCpu(),(int)status.getFps());
        opmode.telemetry.addData("Pipeline", "Index: %d, Type: %s",
                status.getPipelineIndex(), status.getPipelineType());
    }

    public static boolean hiveSideUp(SideColor alliance) {
        List<LLResultTypes.FiducialResult> fiducials = lastResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fiducial : fiducials) {
            if (alliance == SideColor.BLUE) {
                if (fiducial.getFiducialId() >= 38 && fiducial.getFiducialId() <= 45) {
                    return true;
                }
            } else {
                if (fiducial.getFiducialId() >= 30 && fiducial.getFiducialId() <= 37) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void takeScreenShot(String fileName) {
        limelight.captureSnapshot(fileName);
    }
    public static void clearScreenShots() {
        limelight.deleteSnapshots();
    }






    public static Action checkTip(SideColor alliance, int checks, int timeOut) { //checks vs time, whichever completes FIRST wins
        return new Action() {
            private boolean first = true;
            double scanTime;
            int loops = 0;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    scanTime = opmode.getRuntime();
                    first = false;
                }
                update();
                if (hiveSideUp(alliance)) {
                    hiveFacing = true;
                }
                loops += 1;
                return scanTime <= timeOut && loops <= checks;
            }
        };
    }

    public static Action setFacing(boolean facing) {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                hiveFacing = facing;
                return false;
            }
        };
    }

    public static Action waitForTag(SideColor alliance) { //checks vs time, whichever completes FIRST wins
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                update();
                if (hiveSideUp(alliance)) {
                    hiveFacing = true;
                }
                return !hiveFacing;
            }
        };
    }
}