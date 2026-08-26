package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name = "TestAuto")
public class TestAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        while (!isStopRequested() && !opModeIsActive()) {
            //put preselection stuff here, and pos multiplier if needed
            telemetry.update();
        }

        double speed = 60;

        Pose2d startPos = new Pose2d(0.0, 0.0, Math.toRadians(0.0));
        Pose2d endPos = new Pose2d(24.0, 24.0, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                Paths.directPath(drive, startPos, endPos, speed)
        );
    }
}