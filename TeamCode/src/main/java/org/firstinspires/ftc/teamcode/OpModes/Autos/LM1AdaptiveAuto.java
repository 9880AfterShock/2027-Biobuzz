package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.Enums.LEDColor;
import org.firstinspires.ftc.teamcode.Enums.Side;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Mechanisms.LEDs;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;
import org.firstinspires.ftc.teamcode.OpModes.TeleOp;
import org.firstinspires.ftc.teamcode.Sensors.Pinpoint;

import java.util.List;

//@Disabled
@Autonomous(name = "Adaptive Auto LM!, (Use Gamepad Input)")
public class LM1AdaptiveAuto extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    private static Boolean gardenSource = true;
    private static Boolean leftFlower = true; //Alliance Side Perspective
    private static Boolean rightFlower = true; //Alliance Side Perspective
    private static Boolean partnerSource = false; //Picking up against the side of partner who cannot shoot, to be figured out
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = -1; //Default to blue because of red favored in bracket
    private double posRotation = Math.toRadians(90 - posMultiplier*90);
    private static Boolean useVision = false; //Use vision to determine hive tips
    private static Boolean buddyPark = false;
    private static Side startingSide = Side.RIGHT; //Default to solo auto
    ElapsedTime runtime = new ElapsedTime();

    Pose2d startPosRight;
    Pose2d startPosLeft;
    Pose2d preGardenPos;
    Pose2d gardenPos;
    Pose2d traversePos;
    Pose2d shootPosLeft;
    Pose2d shootPosRight;
    Pose2d rotatePosRight;
    Pose2d preParkPosRight;
    Pose2d parkPosRight;
    Pose2d clearPosLeft;
    Pose2d preParkPosLeft;
    Pose2d parkPosLeft;


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
        BallTunnel.init(this);
        BallGate.init(this);
        Turret.init(this);
        Shooter.init(this);
        LEDs.init(this);

        Intake.prime();
        BallGate.prime();
        Shooter.prime();
//        MatchLoader.prime();
        LEDs.update(LEDColor.AMBER, false, runtime);

        double delay = 0;

        while (!isStopRequested() && !opModeIsActive()) {
            if (gamepad1.backWasPressed()) {
                buddyPark = !buddyPark;
            }

            if (gamepad1.x){
                allianceColor = SideColor.BLUE;
                posMultiplier = -1;
                posRotation = Math.toRadians(90 - posMultiplier*90);
            }
            if (gamepad1.b){
                allianceColor = SideColor.RED;
                posMultiplier = 1;
                posRotation = Math.toRadians(90 - posMultiplier*90);
            }

            if (gamepad1.dpad_left){
                startingSide = Side.LEFT;
            }
            if (gamepad1.dpad_right){
                startingSide = Side.RIGHT;
            }

            if (gamepad1.dpadUpWasPressed()){
                delay += 1;
            }
            if (gamepad1.dpadDownWasPressed()){
                delay -= 1;
            }
            delay = Range.clip(delay, 0, 25);
//            if (gamepad1.dpadUpWasPressed()){
//                gardenSource = !gardenSource;
//            }
//            if (gamepad1.dpadLeftWasPressed()){
//                leftFlower = !leftFlower;
//            }
//            if (gamepad1.dpadRightWasPressed()){
//                rightFlower = !rightFlower;
//            }
//            if (gamepad1.dpadDownWasPressed()){
//                partnerSource = !partnerSource;
//            }

            telemetry.addData("Alliance Color (Press B or X)", allianceColor.getCapitalized());
            telemetry.addLine();

            telemetry.addData("Auto Side (DPAD to change)", startingSide.getCapitalized());
            telemetry.addLine();

            telemetry.addData("Buddy Park (Toggle with back button)", buddyPark);

            telemetry.addData("Timer Delay before shooting (LEFT SIDE ONLY)", delay);

//            telemetry.addLine("Intake Sources: (Use DPAD to toggle)");
//            telemetry.addData("Garden (Up)", gardenSource);
//            telemetry.addData("Left Flower (Left)", leftFlower);
//            telemetry.addData("Right Flower (Right)", rightFlower);
//            telemetry.addData("Alliance Partner Side (Down)", partnerSource);
            telemetry.update();
        }

        //Consts here
        double driveSpeed = 60.0; //Probably max we can do
        double ramSpeed = 20.0; //Being safer
        if (!buddyPark && startingSide == Side.RIGHT) { //Speed up right side park
            ramSpeed = driveSpeed;
        }
        double shotTime = 5.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden

        //Poses here
        startPosRight = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posRotation+Math.toRadians(0.0));
        startPosLeft = new Pose2d(posMultiplier*-63.591043, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posRotation+Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-63.303150, posRotation+Math.toRadians(0.0));
        shootPosLeft = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        shootPosRight = new Pose2d(posMultiplier*60.542169, posMultiplier*-12.7, posRotation+Math.toRadians(0.0)); //Same as start but intake is out now
        rotatePosRight = new Pose2d(posMultiplier*40, posMultiplier*-12.7, posRotation+Math.toRadians(180.0));

        preParkPosRight = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(0.0));
        parkPosRight = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(0.0));
        clearPosLeft = new Pose2d(posMultiplier*-24, posMultiplier*-36, posRotation+Math.toRadians(180.0));
        preParkPosLeft = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(180.0));
        parkPosLeft = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(180.0));

        MecanumDrive drive;
        if (startingSide == Side.RIGHT) {
            drive = new MecanumDrive(hardwareMap, startPosRight);
        } else {
            drive = new MecanumDrive(hardwareMap, startPosLeft);
        }





        Action tickFunctions = new ParallelAction(
                Shooter.autoUpdateLoop()
        );

        Action shootSequence = new SequentialAction(
                BallGate.autoOpen(),
                BallTunnel.autoBalltunnelShoot(),
                Paths.wait(drive, shotTime), //Hardcoded wait for shooting rn
                BallGate.autoClose(),
                BallTunnel.autoBalltunnelIdle()
        );





        waitForStart();
        Side currentSide = startingSide;

        Shooter.setHoodAngle(24);
        runtime.reset();

        TeleOp.afterAutoPose = drive.localizer.getPose(); //incase something goes wrong
        TeleOp.alliance = allianceColor;
        TeleOp.afterAuto = true;

        if (isStopRequested()) return;

        if (startingSide == Side.RIGHT) { //RIGHT SIDE
        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                Shooter.autoWaitForSpeed(50, 4),
                                shootSequence,
                                Intake.autoIntakeDown(false)
                        ),
                        tickFunctions
                ), allHubs
        );

        if (gardenSource){
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                        new SequentialAction(
                                Intake.autoIntakeOn(),
                                Paths.directPath(drive, startPosRight, preGardenPos, driveSpeed),
                                Paths.wait(drive, intakeTime),
                                Intake.autoIntakeOff(),
                                Paths.directPath(drive, gardenPos, shootPosRight, driveSpeed)
                        ),
                        tickFunctions
                    ), allHubs
            );
        }

        if (useVision) { //TODO: it check ALSO if the LL is reading no tags
            currentSide = Side.LEFT;
            if (true) {  //TODO: check if we have time for this, if not just hold preloads and park (don't run this block)
                CustomActions.runBlockingBulkRead(
                        new RaceAction(
                                new SequentialAction(
                                        Intake.autoIntakeUp(false),
                                        Paths.directSplinePath(drive, shootPosRight, rotatePosRight, driveSpeed),
                                        Paths.directPath(drive, rotatePosRight, shootPosLeft, driveSpeed),
//                                    Intake.autoIntakeDown(false) //DISABLING for now, we want to keep intake up incase balls gather
                                        shootSequence
                                ),
                                tickFunctions
                        ), allHubs
                );
            }
        } else {
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                            new SequentialAction(
                                    shootSequence
                            ),
                            tickFunctions
                    ), allHubs
            );
        }} //else { //LEFT SIDE
        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new ParallelAction(
                                Paths.wait(drive, delay),
                                Shooter.autoWaitForSpeed(50, 4)
                        ),
                        tickFunctions
                ), allHubs
        );
        //TODO: Put actual LL logic here as well, make sure its useVision AND null reading after the time
        if (useVision) {
            currentSide = Side.RIGHT;
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                            new SequentialAction(
                                    Paths.directPath(drive, shootPosLeft, rotatePosRight, driveSpeed),
                                    Paths.directSplinePath(drive, rotatePosRight, shootPosRight, driveSpeed),
                                    shootSequence
                            ),
                            tickFunctions
                    ), allHubs
            );
        } else {
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                            new SequentialAction(
                                    shootSequence
                            ),
                            tickFunctions
                    ), allHubs
            );
        }

        //}


        //Park
        if (currentSide == Side.RIGHT) {
            CustomActions.runBlockingBulkRead(
                    new RaceAction(
                            new SequentialAction(
                                    Intake.autoIntakeDown(false),
                                    Paths.directPath(drive, shootPosRight, preParkPosRight, driveSpeed), //get ready to ram
                                    Paths.directPath(drive, preParkPosRight, parkPosRight, ramSpeed) //to park
                            ),
                            tickFunctions
                    ), allHubs
            );
        } else {
            if (buddyPark) {
                CustomActions.runBlockingBulkRead(
                        new RaceAction(
                                new SequentialAction(
                                        Paths.tangentLinearPath(drive, shootPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                                        Intake.autoIntakeDown(false),
                                        Paths.directPath(drive, clearPosLeft, preParkPosLeft, driveSpeed), //get ready to ram
                                        Paths.directPath(drive, preParkPosLeft, parkPosLeft, ramSpeed)
                                ),
                                tickFunctions
                        ), allHubs
                );
            } else {
                CustomActions.runBlockingBulkRead(
                        new RaceAction(
                                new SequentialAction(
                                        Paths.tangentLinearPath(drive, shootPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                                        Paths.tangentLinearPath(drive, clearPosLeft,posRotation+Math.toRadians(0.0), parkPosLeft,posRotation+Math.toRadians(180.0), ramSpeed)
                                ),
                                tickFunctions
                        ), allHubs
                );
            }
        }

//        if (leftFlower) {
//            if (gardenSource) {
//
//            } else {
//
//            }
//
//        }

        TeleOp.afterAutoPose = drive.localizer.getPose(); //Feed ending pos to teleop
    }
}