package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.MatchLoader;
import org.firstinspires.ftc.teamcode.Enums.SideColor;

import java.util.List;

@Autonomous(name = "Adaptive Auto (WIP) [ROBOT MOVES UPON INITIALIZATION]")
public class LM0AdaptiveAuto extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    private static Boolean gardenSource = true;
    private static Boolean leftFlower = true; //Alliance Side Perspective
    private static Boolean rightFlower = true; //Alliance Side Perspective
    private static Boolean partnerSource = false; //Picking up against the side of partner who cannot shoot, to be figured out
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = -1; //Default to blue because of red favored in bracket
    private static Boolean useVision = true; //Use vision to determine hive tips

    Pose2d startPos;
    Pose2d gardenPos;
    Pose2d gardenClearPos;
    Pose2d preflowerPos1;
    Pose2d flowerPos1;
    Pose2d preflowerPos2;
    Pose2d flowerPos2;
    Pose2d shootPos4;

    Pose2d parkPos1;
    Pose2d parkPos2;



    private static final Action tickFunctions = new ParallelAction(
            //Infinite loop tick actions; turret pid and shooter pid
            //return false;
    );

    private static final Action shootSequence = new SequentialAction(
            //Open gate after time
            //Run ball tunnel
            //Close gate
    );




    @Override
    public void runOpMode() {
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Init
        Intake.init(this);
//        MatchLoader.init(this);

        Intake.prime();
//        MatchLoader.prime();

        while (!isStopRequested() && !opModeIsActive()) {
            if (gamepad1.x){
                allianceColor = SideColor.BLUE;
                posMultiplier = -1;

            }
            if (gamepad1.b){
                allianceColor = SideColor.RED;
                posMultiplier = 1;
            }

            if (gamepad1.dpadUpWasPressed()){
                gardenSource = !gardenSource;
            }
            if (gamepad1.dpadLeftWasPressed()){
                leftFlower = !leftFlower;
            }
            if (gamepad1.dpadRightWasPressed()){
                rightFlower = !rightFlower;
            }
            if (gamepad1.dpadDownWasPressed()){
                partnerSource = !partnerSource;
            }

            telemetry.addData("Alliance Color (Press B or X)", allianceColor.getCapitalized());
            telemetry.addLine();
            telemetry.addLine("Intake Sources: (Use DPAD to toggle)");
            telemetry.addData("Garden (Up)", gardenSource);
            telemetry.addData("Left Flower (Left)", leftFlower);
            telemetry.addData("Right Flower (Right)", rightFlower);
            telemetry.addData("Alliance Partner Side (Down)", partnerSource);
            telemetry.update();
        }

        //Consts here
        double driveSpeed = 60.0; //Probably max we can do
        double intakeSpeed = 15.0; //For pickup on garden
        double shotTime = 3.0; //Very on safe side for now
        double intakeTime = 1.0; //For garden
        double flowerIntakeTime = 1.0; //For flower pickup

        //Poses here
        startPos = new Pose2d(posMultiplier*63.303150, posMultiplier*-51.391043, posMultiplier*Math.toRadians(-90.0)); //Temp until we fine a way of aligning that we like (right now its 1 inch from first garden pollen to front bumper)
        gardenPos = new Pose2d(posMultiplier*63.303150, posMultiplier*-60.542169, posMultiplier*Math.toRadians(-90.0));
        gardenClearPos = new Pose2d(gardenPos.position.x - posMultiplier*5, gardenPos.position.y + posMultiplier*5, gardenPos.heading.toDouble());
        preflowerPos1 = new Pose2d(posMultiplier*-52.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        flowerPos1 = new Pose2d(posMultiplier*-57.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        preflowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-52.0, posMultiplier*Math.toRadians(270.0));
        flowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-57.0, posMultiplier*Math.toRadians(270.0));
        shootPos4 = new Pose2d(posMultiplier*40.0, posMultiplier*-40.0, posMultiplier*Math.toRadians(180.0));

        Pose2d parkPos1 = new Pose2d(posMultiplier*-20, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));
        Pose2d parkPos2 = new Pose2d(posMultiplier*-50, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart(); //after here put any stuff (such as after auto states) for teleop

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                shootSequence,
                                Intake.autoIntakeDown()
                        ),
                        tickFunctions
                ), allHubs
        );

        if (gardenSource){
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                            Intake.autoIntakeOn(),
                            Paths.directPath(drive, startPos, gardenPos, intakeSpeed),
                            Paths.wait(drive,30.0),
                            tickFunctions
                    ), allHubs
            );
        }

    }
}