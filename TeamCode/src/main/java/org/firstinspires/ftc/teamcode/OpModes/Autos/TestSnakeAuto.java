package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Config
@Autonomous(name = "Test Snake Auto")
public class TestSnakeAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        while (!isStopRequested() && !opModeIsActive()) {
            //put preselection stuff here, and pos multiplier if needed
            telemetry.update();
        }

        double speed = 60;

        double tileSize = 24;
        Pose2d startPos = new Pose2d(tileSize*0, tileSize*0, Math.toRadians(0.0));
        Pose2d pos1 = new Pose2d(tileSize*1, tileSize*0, Math.toRadians(0.0));
        Pose2d pos2 = new Pose2d(tileSize*1, tileSize*1, Math.toRadians(0.0));
        Pose2d pos3 = new Pose2d(tileSize*0, tileSize*1, Math.toRadians(0.0));
        Pose2d pos4 = new Pose2d(tileSize*0, tileSize*2, Math.toRadians(0.0));
        Pose2d pos5 = new Pose2d(tileSize*1, tileSize*2, Math.toRadians(0.0));
        Pose2d pos6 = new Pose2d(tileSize*1, tileSize*3, Math.toRadians(0.0));
        Pose2d pos7 = new Pose2d(tileSize*0, tileSize*3, Math.toRadians(0.0));
        Pose2d pos8 = new Pose2d(tileSize*0, tileSize*4, Math.toRadians(0.0));
        Pose2d pos9 = new Pose2d(tileSize*2, tileSize*4, Math.toRadians(90.0));
        Pose2d pos10 = new Pose2d(tileSize*2, tileSize*0, Math.toRadians(180.0));
        Pose2d endPos = new Pose2d(tileSize*0, tileSize*0, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                new SequentialAction(
                        Paths.directPath(drive, startPos, pos1, 60),
                        Paths.directPath(drive, pos1, pos2, 60),
                        Paths.directPath(drive, pos2, pos3, 60),
                        Paths.directPath(drive, pos3, pos4, 60),
                        Paths.directPath(drive, pos4, pos5, 60),
                        Paths.directPath(drive, pos5, pos6, 60),
                        Paths.directPath(drive, pos6, pos7, 60),
                        Paths.directPath(drive, pos7, pos8, 60),
                        Paths.directPath(drive, pos8, pos9, 60),
                        Paths.directPath(drive, pos9, pos10, 60),
                        Paths.directPath(drive, pos10, endPos, 60)
                )
        );
    }
}