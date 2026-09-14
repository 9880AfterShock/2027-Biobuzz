package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Disabled
@Autonomous(name = "Template Auto")
public class AutoTemplate extends LinearOpMode {
    @Override
    public void runOpMode() {
        while (!isStopRequested() && !opModeIsActive()) {
            //put preselection stuff here, and pos multiplier if needed
            telemetry.update();
        }

        //Consts here
        double speed = 60;

        //Poses here
        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                new SequentialAction(
                        //auto code here
                )
        );
    }
}