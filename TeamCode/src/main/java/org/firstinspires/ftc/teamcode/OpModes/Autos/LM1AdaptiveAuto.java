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

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.Enums.LEDColor;
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
@Autonomous(name = "Adaptive Auto (WIP) [ROBOT MOVES UPON INITIALIZATION]")
public class LM1AdaptiveAuto extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    private static Boolean gardenSource = true;
    private static Boolean leftFlower = true; //Alliance Side Perspective
    private static Boolean rightFlower = true; //Alliance Side Perspective
    private static Boolean partnerSource = false; //Picking up against the side of partner who cannot shoot, to be figured out
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = -1; //Default to blue because of red favored in bracket
    private static Boolean useVision = false; //Use vision to determine hive tips
    private static Boolean buddyPark = false;
    ElapsedTime runtime = new ElapsedTime();

    Pose2d startPos;
    Pose2d preGardenPos;
    Pose2d gardenPos;
    Pose2d traversePos;
    Pose2d shootPosLeft;
    Pose2d shootPosRight;
    Pose2d preParkPos;
    Pose2d parkPos;


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
        posMultiplier = 1; //TODO: fix transforms here

        while (!isStopRequested() && !opModeIsActive()) {
            if (gamepad1.backWasPressed()) {
                buddyPark = !buddyPark;
            }

//            if (gamepad1.x){
//                allianceColor = SideColor.BLUE;
//                posMultiplier = -1;
//
//            }
//            if (gamepad1.b){
//                allianceColor = SideColor.RED;
//                posMultiplier = 1;
//            }

//            if (gamepad1.dpadUpWasPressed()){
//                delay += 1;
//            }
//            if (gamepad1.dpadDownWasPressed()){
//                delay -= 1;
//            }
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

            telemetry.addData("Buddy Park (Toggle with back button)", buddyPark);

//            telemetry.addData("Timer Delay before shooting", delay);

//            telemetry.addData("Alliance Color (Press B or X)", allianceColor.getCapitalized());
//            telemetry.addLine();
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
        if (!buddyPark) { //Remove Ramming
            ramSpeed = driveSpeed;
        }
        double shotTime = 5.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden

        //Poses here
        startPos = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posMultiplier*Math.toRadians(0.0));
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        shootPosLeft = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posMultiplier*Math.toRadians(180.0)); //Opposite side of the field
        shootPosRight = new Pose2d(posMultiplier*60.542169, posMultiplier*-12.7, posMultiplier*Math.toRadians(0.0)); //Same as start but intake is out now

        preParkPos = new Pose2d(posMultiplier*24, posMultiplier*-48, posMultiplier*Math.toRadians(0.0));
        parkPos = new Pose2d(posMultiplier*-25, posMultiplier*-60, posMultiplier*Math.toRadians(0.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);





        Action tickFunctions = new ParallelAction(
                Shooter.autoUpdateLoop()
        );

        Action shootSequence = new SequentialAction(
                Shooter.autoWaitForSpeed(50, 4),
                Paths.wait(drive, delay),
                BallGate.autoOpen(),
                BallTunnel.autoBalltunnelShoot(),
                Paths.wait(drive, shotTime), //Hardcoded wait for shooting rn
                BallGate.autoClose(),
                BallTunnel.autoBalltunnelIdle()
        );





        waitForStart();
        Shooter.setHoodAngle(24);
        runtime.reset();

        TeleOp.afterAutoPose = startPos; //incase something goes wrong
        TeleOp.alliance = allianceColor;
        TeleOp.afterAuto = true;

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
                        new SequentialAction(
                                Intake.autoIntakeOn(),
                                Paths.directPath(drive, startPos, preGardenPos, driveSpeed),
                                Paths.wait(drive, intakeTime),
                                Intake.autoIntakeOff(),
                                Paths.directPath(drive, gardenPos, shootPosRight, driveSpeed)
                        ),
                        tickFunctions
                    ), allHubs
            );
        }

        if (useVision) { //later make it check ALSO if the LL is reading no tags

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

        //Park
        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                Paths.directPath(drive, shootPosRight, preParkPos, driveSpeed), //get ready to ram
                                Paths.directPath(drive, preParkPos, parkPos, ramSpeed) //to park
                        ),
                        tickFunctions
                ), allHubs
        );

        //check vison

        if (leftFlower) {
            if (gardenSource) {

            } else {

            }

        }

        TeleOp.afterAutoPose = drive.localizer.getPose(); //Feed ending pos to teleop
    }
}