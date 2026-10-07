package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.*;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.MecanumDrive;

import java.util.List;

@Disabled
@Autonomous(name = "Template Auto with Bulk Reading")
public class AutoTemplateBulkRead extends LinearOpMode {
    public static List<LynxModule> allHubs;
    @Override
    public void runOpMode() {
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Init
        //INIT FUNCTIONS HERE
        //PRIME FUNCTIONS HERE

        while (!isStopRequested() && !opModeIsActive()) {
            //put preselection stuff here, and pos multiplier if needed
            telemetry.update();
        }

        //Consts here
        double speed = 60;

        //Poses here
        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart(); //after here put any stuff (such as after auto states) for teleop

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new SequentialAction(
                        //auto code here
                ), allHubs
        );
    }
}