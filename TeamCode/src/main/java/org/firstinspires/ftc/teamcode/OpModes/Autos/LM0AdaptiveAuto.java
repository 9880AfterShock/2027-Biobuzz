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




    private static final Action tickFunctions = new ParallelAction(
            //Infinite loop tick actions; turret pid and shooter pid
    );

    private static final Action shootSequence = new SequentialAction(
            //Open gate after time
            //Run ball tunnel
            //Close gate
    );




    @Override
    public void runOpMode() {
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Init
        Intake.init(this);
        MatchLoader.init(this);

        Intake.prime();
        MatchLoader.prime();

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
        double speed = 60;

        //Poses here
        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);


        waitForStart(); //after here put any stuff (such as after auto states) for teleop

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                //Lock on turret and set target rpm
                                //Wait for initial spin-up time (measure)
                                shootSequence,
                                Intake.autoIntakeDown()
                        ),
                        tickFunctions
                ), allHubs
        );
    }
}