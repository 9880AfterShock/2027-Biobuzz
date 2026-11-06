package org.firstinspires.ftc.teamcode.OpModes.Autos;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.Enums.LEDColor;
import org.firstinspires.ftc.teamcode.Enums.Side;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.MatchLoader;
import org.firstinspires.ftc.teamcode.Mechanisms.LEDs;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;
import org.firstinspires.ftc.teamcode.OpModes.TeleOp;
import org.firstinspires.ftc.teamcode.Sensors.I2CDistance;
import org.firstinspires.ftc.teamcode.Sensors.Vision.Limelight;

import java.util.List;

//@Disabled
@Autonomous(name = "Adaptive Auto LM2, (Use Gamepad Input)")
public class LM2AdaptiveAuto extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    private static Boolean gardenSource = true;
    private static Boolean leftFlower = true; //Alliance Side Perspective
    private static Boolean rightFlower = true; //Alliance Side Perspective
    private static Boolean partnerSource = false; //Picking up against the side of partner who cannot shoot, to be figured out
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = 1; //Default to blue because of red favored in bracket
    private double posRotation = Math.toRadians(90 - posMultiplier*90); //Also default to blue for same reason
    private static Boolean useVision = true; //Use vision to determine hive tips
    private static Boolean useDistance = true; //Use I2C distance sensors to determine when intaking or shooting is done
    private static Boolean buddyPark = true; //Make sure we push our partner into the park zone
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


    public static Action updateTeleOpPos(MecanumDrive drive) {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                TeleOp.afterAutoPose = drive.localizer.getPose(); //Feed ending pos to teleop
                return true;
            }
        };
    }

    @Override
    public void runOpMode() {
        //Dashboard Telem
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Init
        Intake.init(this);
        MatchLoader.init(this);
        BallTunnel.init(this);
        BallGate.init(this);
        Turret.init(this);
        Shooter.init(this);
//        if (useVision) {
        Limelight.initDetection(this);
//        }
//        if (useDistance) {
        I2CDistance.init(this);
//        }
        LEDs.init(this);

        Intake.prime();
        MatchLoader.prime();
        BallGate.prime();
        Turret.prime();
        Shooter.prime();
        LEDs.update(LEDColor.AMBER, false, runtime);

        double delay = 0;

        while (!isStopRequested() && !opModeIsActive()) {
            if (gamepad1.backWasPressed()) {
                buddyPark = !buddyPark;
            }

            if (gamepad1.yWasPressed()) {
                useVision = !useVision;
            }

            if (gamepad1.x){
                allianceColor = SideColor.BLUE;
                posMultiplier = 1;
                posRotation = Math.toRadians(90 - posMultiplier*90);
            }
            if (gamepad1.b){
                allianceColor = SideColor.RED;
                posMultiplier = -1;
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

            telemetry.addData("Use vision to check tips (Toggle with Y)", useVision);
            telemetry.addLine();

            telemetry.addData("Buddy Park (Toggle with back button)", buddyPark);

            telemetry.addData("Timer Delay before shooting without vision (LEFT SIDE ONLY)", delay);

//            telemetry.addLine("Intake Sources: (Use DPAD to toggle)");
//            telemetry.addData("Garden (Up)", gardenSource);
//            telemetry.addData("Left Flower (Left)", leftFlower);
//            telemetry.addData("Right Flower (Right)", rightFlower);
//            telemetry.addData("Alliance Partner Side (Down)", partnerSource);
            telemetry.update();
        }

        //Consts here
        double startPosRPM = 2700;
        double wallRPM = 2650;

        double driveSpeed = 60.0; //Probably max we can do
        double ramSpeed = 20.0; //Being safer
        if (!buddyPark && startingSide == Side.RIGHT) { //Speed up right side park
            ramSpeed = driveSpeed;
        }
        double shotTime = 3.0; //Playing more dangerous
        double intakeTime = 3.0; //For garden

        //Poses here
        startPosRight = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posRotation+Math.toRadians(0.0));
        startPosLeft = new Pose2d(posMultiplier*-63.591043, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posRotation+Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-65, posRotation+Math.toRadians(0.0));
        shootPosLeft = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        shootPosRight = new Pose2d(posMultiplier*60.542169, posMultiplier*-10, posRotation+Math.toRadians(0.0)); //Same as start but intake is out now
        rotatePosRight = new Pose2d(posMultiplier*40, posMultiplier*-10, posRotation+Math.toRadians(180.0));

        preParkPosRight = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(0.0));
        parkPosRight = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(0.0));
        clearPosLeft = new Pose2d(posMultiplier*-24, posMultiplier*-40, posRotation+Math.toRadians(180.0));
        preParkPosLeft = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(180.0));
        parkPosLeft = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(180.0));

        MecanumDrive drive;
        if (startingSide == Side.RIGHT) {
            drive = new MecanumDrive(hardwareMap, startPosRight);
        } else {
            drive = new MecanumDrive(hardwareMap, startPosLeft);
        }





        Action tickFunctions = new ParallelAction(
                Shooter.autoUpdateLoop(),
                Turret.autoUpdateLoop(allianceColor),
                updateTeleOpPos(drive)
        );

        Action shootSequence = new SequentialAction(
                BallGate.autoOpen(),
                BallTunnel.autoBallTunnelShoot(),
                I2CDistance.waitToEmpty(shotTime, useDistance), //Constant I2C reads should be fine bc nothing other then the pinpoint is tracking
                BallGate.autoClose(),
                BallTunnel.autoBallTunnelIdle()
        );

        Action shootSequence2 = new SequentialAction(
                BallGate.autoOpen() //TODO: figure out why dupes are breaking
        );






        Action rightSideStart = new RaceAction(
                new SequentialAction(
                        Shooter.autoSetTargetRPM(startPosRPM),
                        Shooter.autoWaitForSpeed(50, 4),
                        shootSequence,
                        Intake.autoIntakeDown(false),
                        Shooter.autoSetTargetRPM(wallRPM)
                ),
                tickFunctions
        );

        Action rightSideGardenPickup = new RaceAction(
                new SequentialAction(
                        Intake.autoIntakeOn(),
                        BallTunnel.autoBallTunnelIntake(),
                        Paths.directPath(drive, startPosRight, preGardenPos, driveSpeed),
                        Paths.directPath(drive, preGardenPos, gardenPos, driveSpeed),
                        Paths.wait(drive, intakeTime),
                        Intake.autoIntakeOff(),
                        Paths.directPath(drive, gardenPos, shootPosRight, driveSpeed)
                ),
                tickFunctions
        );

        Action rightSideVisionCheck = new RaceAction(
                new SequentialAction(
                        Limelight.setFacing(false),
                        Limelight.checkTip(allianceColor, 60, 3)
                ),
                tickFunctions
        );

        Action rightSideGardenToLeft = new RaceAction(
                new SequentialAction(
//                                        Intake.autoIntakeUp(false), //Raise up to avoid over possession
                        Paths.directSplinePath(drive, shootPosRight, rotatePosRight, driveSpeed),
                        Paths.directPath(drive, rotatePosRight, shootPosLeft, driveSpeed),
                        Intake.autoIntakeUp(false), //Raise up to avoid over possession
//                                    Intake.autoIntakeDown(false) //DISABLING for now, we want to keep intake up in case balls gather
                        shootSequence2
                ),
                tickFunctions
        );



        //LEFT SIDE BELOW, RIGHT SIDE ABOVE



        Action leftSideVisionWait = new RaceAction(
                new ParallelAction(
                        Shooter.autoSetTargetRPM(startPosRPM),
                        new RaceAction(
                                Paths.wait(drive, delay), //TODO: make race action or just be polling LL here
                                new SequentialAction(
                                        Limelight.waitForTag(allianceColor),
                                        Paths.wait(drive, 2) //Because we see tags about 1 sec early, being safe
                                )
                        ),
                        Shooter.autoWaitForSpeed(50, 4)
                ),
                tickFunctions
        );

        Action leftSideBlindWait = new RaceAction(
                new ParallelAction(
                        Shooter.autoSetTargetRPM(startPosRPM),
                        Paths.wait(drive, delay),
                        Shooter.autoWaitForSpeed(50, 0.5)
                ),
                tickFunctions
        );

        Action leftSideVisionCheck = new RaceAction(
                new SequentialAction(
                        Limelight.setFacing(false),
                        Limelight.checkTip(allianceColor, 20, 2) //TODO: see about not waiting at all since this only triggers if our partner has not tipped anyway
                ),
                tickFunctions
        );

        Action leftSideCrossField = new RaceAction(
                new SequentialAction(
                        Shooter.autoSetTargetRPM(wallRPM),
                        Intake.autoIntakeUp(false),
                        Paths.directPath(drive, shootPosLeft, rotatePosRight, driveSpeed), //TODO: make it just park if there isnt time to go cross-field
                        Paths.directSplinePath(drive, rotatePosRight, shootPosRight, driveSpeed),
                        shootSequence
                ),
                tickFunctions
        );





        //Park Below

        Action rightSidePark = new RaceAction(
                new SequentialAction(
                        Intake.autoIntakeDown(false),
                        Paths.directPath(drive, shootPosRight, preParkPosRight, driveSpeed), //get ready to ram
                        Paths.directPath(drive, preParkPosRight, parkPosRight, ramSpeed) //to park
                ),
                tickFunctions
        );

        Action leftSideStartBuddyPark = new RaceAction(
                new SequentialAction(
                        Paths.tangentLinearPath(drive, startPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                        Intake.autoIntakeDown(false),
                        Paths.directPath(drive, clearPosLeft, preParkPosLeft, driveSpeed), //get ready to ram
                        Paths.directPath(drive, preParkPosLeft, parkPosLeft, ramSpeed)
                ),
                tickFunctions
        );

        Action leftSideStartPark = new RaceAction(
                new SequentialAction(
                        Paths.tangentLinearPath(drive, startPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                        Paths.tangentLinearPath(drive, clearPosLeft,posRotation+Math.toRadians(0.0), parkPosLeft,posRotation+Math.toRadians(180.0), ramSpeed)
                ),
                tickFunctions
        );

        Action leftSideBuddyPark = new RaceAction( //NOT left start pos buddy park, that's above
                new SequentialAction(
                        Paths.tangentLinearPath(drive, shootPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                        Intake.autoIntakeDown(false),
                        Paths.directPath(drive, clearPosLeft, preParkPosLeft, driveSpeed), //get ready to ram
                        Paths.directPath(drive, preParkPosLeft, parkPosLeft, ramSpeed)
                ),
                tickFunctions
        );

        Action leftSidePark = new RaceAction( //NOT left start pos park, that's above
                new SequentialAction(
                        Paths.tangentLinearPath(drive, shootPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed),
                        Paths.tangentLinearPath(drive, clearPosLeft,posRotation+Math.toRadians(0.0), parkPosLeft,posRotation+Math.toRadians(180.0), ramSpeed)
                ),
                tickFunctions
        );




                waitForStart();
        Side currentSide = startingSide;

        Shooter.setHoodAngle(24);
        LEDs.update(LEDColor.GREEN, false, runtime);
        runtime.reset();

        TeleOp.afterAutoPose = drive.localizer.getPose(); //incase something goes wrong
        TeleOp.alliance = allianceColor;
        TeleOp.afterAuto = true;

        if (isStopRequested()) return;

        if (startingSide == Side.RIGHT) { //RIGHT SIDE
            CustomActions.runBlockingBulkRead(rightSideStart, allHubs);

            if (gardenSource){ //RN a given. IDK what happens if this is false ATM
                CustomActions.runBlockingBulkRead(rightSideGardenPickup, allHubs);
            }

            boolean missedTip; //Is the hive facing us still
            if (useVision) { //Update LL to check for hive
                CustomActions.runBlockingBulkRead(rightSideVisionCheck, allHubs);
                missedTip = Limelight.hiveFacing;
            } else {
                missedTip = true;
            }
            telemetry.addData("Missed Tip", missedTip);
            telemetry.update();

            if (!missedTip) {
                currentSide = Side.LEFT;
                if (true) {  //TODO: check if we have time for this, if not just hold preloads and park (don't run this block) (we usually have time right now even with buddy park)
                    CustomActions.runBlockingBulkRead(rightSideGardenToLeft, allHubs);
                }

            } else {
                CustomActions.runBlockingBulkRead(
                        new RaceAction(
                                shootSequence2, //TODO: test if need to declare as new to reset memory pointer?
                                tickFunctions
                        ), allHubs
                );
            }} else { //LEFT SIDE

            if (useVision) {
                CustomActions.runBlockingBulkRead(leftSideVisionWait, allHubs);
            } else {
                CustomActions.runBlockingBulkRead(leftSideBlindWait, allHubs);
            }


            boolean partnerTipped;
            if (useVision) { //Update LL to check for hive
                CustomActions.runBlockingBulkRead(leftSideVisionCheck, allHubs);
                partnerTipped = !Limelight.hiveFacing;
            } else {
                partnerTipped = true;
            }
            telemetry.addData("Partner Tipped", partnerTipped);
            telemetry.update();

            if (!partnerTipped) {
                currentSide = Side.RIGHT;
                CustomActions.runBlockingBulkRead(leftSideCrossField, allHubs);
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

        }


        //Park
        if (currentSide == Side.RIGHT) {
            CustomActions.runBlockingBulkRead(rightSidePark, allHubs);
        } else {
            if (startingSide == Side.LEFT) {
                if (buddyPark) {
                    CustomActions.runBlockingBulkRead(leftSideStartBuddyPark, allHubs);
                } else {
                    CustomActions.runBlockingBulkRead(leftSideStartPark, allHubs);
                }
            } else {
                if (buddyPark) {
                    CustomActions.runBlockingBulkRead(leftSideBuddyPark, allHubs);
                } else {
                    CustomActions.runBlockingBulkRead(leftSidePark, allHubs);
                }
            }
        }
        LEDs.update(LEDColor.RED, false, runtime);
    }
}