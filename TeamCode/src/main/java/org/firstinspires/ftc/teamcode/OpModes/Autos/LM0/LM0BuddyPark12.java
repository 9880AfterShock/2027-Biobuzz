package org.firstinspires.ftc.teamcode.OpModes.Autos.LM0;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;
import org.firstinspires.ftc.teamcode.OpModes.Autos.Paths;
import org.firstinspires.ftc.teamcode.OpModes.TeleOp;
import org.firstinspires.ftc.teamcode.Sensors.Pinpoint;

import java.util.List;

@Autonomous(name = "12 ball solo and buddy park")
public class LM0BuddyPark12 extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = -1; //Default to blue because of red favored in bracket
    Pose2d startPos;
    Pose2d preGardenPos;
    Pose2d gardenPos;
    Pose2d traversePos;
    Pose2d shootPos2;
    Pose2d parkPos1;
    Pose2d parkPos2;


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

        double delay = 0;

        while (!isStopRequested() && !opModeIsActive()) {

            if (gamepad1.dpadUpWasPressed()){
                delay += 1;
            }
            if (gamepad1.dpadDownWasPressed()){
                delay -= 1;
            }
            delay = Range.clip(delay, 0.0, 20.0);

            telemetry.addLine("Use DPad to change delay");
            telemetry.addData("No delay", delay);
            telemetry.update();
        }

        //Consts here
        double driveSpeed = 60.0; //Probably max we can do
        double intakeSpeed = 5.0; //For pickup on garden
        double shotTime = 4.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden

        //Poses here
        startPos = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posMultiplier*Math.toRadians(0.0));
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        traversePos = new Pose2d(posMultiplier*-24, posMultiplier*-36, posMultiplier*Math.toRadians(180.0)); //Avoid the hive
        shootPos2 = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posMultiplier*Math.toRadians(180.0)); //Opposite side of the field

        preParkPos = new Pose2d(posMultiplier*12, posMultiplier*-56, posMultiplier*Math.toRadians(180.0));
        parkPos = new Pose2d(posMultiplier*-25, posMultiplier*-60, posMultiplier*Math.toRadians(180.0));

        MecanumDrive drive = new MecanumDrive(hardwareMap, startPos);





        Action tickFunctions = new ParallelAction(
                Shooter.autoUpdateLoop()
        );

        Action shootSequence = new SequentialAction(
                Shooter.autoWaitForSpeed(50, 4),
                BallGate.autoOpen(),
                BallTunnel.autoBalltunnelShoot(),
                Paths.wait(drive, shotTime), //Hardcoded wait for shooting rn
                BallGate.autoClose(),
                BallTunnel.autoBalltunnelIdle()
        );





        waitForStart();
        TeleOp.afterAutoPose = startPos; //incase something goes wrong
        TeleOp.alliance = allianceColor;
        TeleOp.afterAuto = true;

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                shootSequence,
                                //to pre pickup
                                Intake.autoIntakeOn(),
                                Intake.autoIntakeDown(),
                                //pickup
                                Paths.wait(drive, intakeTime)
                                //continue to shoot
                        ),
                        tickFunctions
                ), allHubs
        );

        TeleOp.afterAutoPose = drive.localizer.getPose(); //Feed ending pos to teleop
    }
}