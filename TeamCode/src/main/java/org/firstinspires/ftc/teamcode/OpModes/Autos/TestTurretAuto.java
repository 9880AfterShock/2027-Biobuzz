package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;

import java.util.List;

//@Disabled
@Autonomous(name = "Turret Test Auto")
public class TestTurretAuto extends LinearOpMode {
    public static List<LynxModule> allHubs;
    @Override
    public void runOpMode() {
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Init
        Turret.init(this);
        Shooter.init(this);

        while (!isStopRequested() && !opModeIsActive()) {
            //put preselection stuff here, and pos multiplier if needed
            telemetry.update();
        }

        //Poses here
        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0.0));
        Pose2d targetPosA = new Pose2d(100, 100, Math.toRadians(0.0));
        Pose2d targetPosB = new Pose2d(-100, 100, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart(); //after here put any stuff (such as after auto states) for teleop

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        Turret.autoUpdateLoop(SideColor.BLUE),
                        Shooter.autoUpdateLoop(),
                        new SequentialAction(
                                Shooter.autoSetTargetRPM(3000),
                                Turret.setTarget(startPos, targetPosA, false),
                                Paths.wait(drive, 5),
                                Turret.setTarget(startPos, targetPosB, false),
                                Paths.wait(drive, 10)
                        )
                ), allHubs
        );
    }
}