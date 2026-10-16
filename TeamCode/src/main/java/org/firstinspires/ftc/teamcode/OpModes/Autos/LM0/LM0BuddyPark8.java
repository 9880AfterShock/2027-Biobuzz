package org.firstinspires.ftc.teamcode.OpModes.Autos.LM0;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.RaceAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.CustomActions;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;
import org.firstinspires.ftc.teamcode.OpModes.Autos.Paths;
import org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import java.util.List;

@Disabled
@Autonomous(name = "12 ball solo and buddy park")
public class LM0BuddyPark8 extends LinearOpMode {
    private static List<LynxModule> allHubs;
    //Currently no plan to ever disable preloads, this saves me work
    public static SideColor allianceColor = SideColor.BLUE;  //Default to blue because of red favored in bracket
    private int posMultiplier = -1; //Default to blue because of red favored in bracket
    Pose2d startPos;
    Pose2d preGardenPos;
    Pose2d gardenPos;
    Pose2d traversePos;
    Pose2d shootPos2;
    Pose2d postShootPos2;
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

        Intake.prime();
        BallGate.prime();
        Shooter.prime();
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
        double ramSpeed = 20.0; //Playing it safer
        double intakeSpeed = 5.0; //For pickup on garden
        double shotTime = 4.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden

        posMultiplier = 1;

        //Poses here
        startPos = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posMultiplier*Math.toRadians(0.0));
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-63.303150, posMultiplier*Math.toRadians(0.0));
        traversePos = new Pose2d(posMultiplier*36, posMultiplier*-10, posMultiplier*Math.toRadians(180.0)); //Avoid the hive
        shootPos2 = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posMultiplier*Math.toRadians(180.0)); //Opposite side of the field

        postShootPos2 = new Pose2d(posMultiplier*-24, posMultiplier*-36, posMultiplier*Math.toRadians(180.0));
        preParkPos = new Pose2d(posMultiplier*24, posMultiplier*-48, posMultiplier*Math.toRadians(180.0));
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

//        Turret.prime(); //Here to be safe on vibrations offsetting the turret and causing the internal PID to go

        TeleOp.afterAutoPose = startPos; //incase something goes wrong
        TeleOp.alliance = allianceColor;
        TeleOp.afterAuto = true;

        if (isStopRequested()) return;

        CustomActions.runBlockingBulkRead(
                new RaceAction(
                        new SequentialAction(
                                Shooter.autoSetTargetRPM(2000),
                                Shooter.autoWaitForSpeed(50, 6),
                                shootSequence,

                                Paths.directPath(drive, startPos, preGardenPos, driveSpeed), //to pre pickup
                                Intake.autoIntakeOn(),
                                Intake.autoIntakeDown(),
                                Paths.directPath(drive, preGardenPos, gardenPos, intakeSpeed), //pickup garden
                                Paths.wait(drive, intakeTime), //wait for vectoring

                                Paths.directPath(drive, gardenPos, traversePos, driveSpeed),
                                Paths.directPath(drive, traversePos, shootPos2, driveSpeed),//continue to shoot
                                shootSequence,
                                Shooter.autoSetTargetRPM(0),

                                //to buddy park
                                Paths.tangentLinearPath(drive, shootPos2, posMultiplier*Math.toRadians(0.0), postShootPos2, posMultiplier*Math.toRadians(0.0), driveSpeed), //leave shooting
                                Paths.directPath(drive, postShootPos2, preParkPos, driveSpeed), //get ready to ram
                                Paths.directPath(drive, preParkPos, parkPos, ramSpeed) //to park
                        ),
                        tickFunctions
                ), allHubs
        );

        TeleOp.afterAutoPose = drive.localizer.getPose(); //Feed ending pos to teleop
    }
}