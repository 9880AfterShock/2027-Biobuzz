package org.firstinspires.ftc.teamcode.OpModes;

import android.service.autofill.FieldClassification;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Sensors.Gyro;
import org.firstinspires.ftc.teamcode.Utils.Logging.DualLogger;
import org.firstinspires.ftc.teamcode.Utils.Logging.Logger;
import org.firstinspires.ftc.teamcode.Mechanisms.Drivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.MatchLoader;
import org.firstinspires.ftc.teamcode.Mechanisms.LEDs;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;
import org.firstinspires.ftc.teamcode.Sensors.Pinpoint;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="2027 TeleOp BIOBUZZ")
public class TeleOp extends LinearOpMode {
    public static boolean afterAuto;
    public static Pose2d afterAutoPose;
    public static SideColor alliance;
    public static List<LynxModule> allHubs;
//    public static DualLogger dLogger;
//    public static Logger logger;
    ElapsedTime runtime = new ElapsedTime();
    int loops = 0;

    @Override
    public void runOpMode() {
        //Below is for if you want FTC dashboard telemetry
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

//        logger = new Logger(this,"TeleOP");
//        dLogger = new DualLogger(logger, this.telemetry);

        allHubs = hardwareMap.getAll(LynxModule.class); //Set for bulk read
        LynxModule controlHub = hardwareMap.get(LynxModule.class, "Control Hub"); //Individual for voltage readings
        LynxModule expansionHub = hardwareMap.get(LynxModule.class, "Expansion Hub 2");

        if (afterAutoPose == null) { //If no auto was run, assume we are in the center so we can safely reset in either corner opposite of the side we started on
            afterAutoPose = new Pose2d(0.0, 0.0, 0.0);
        }
        if (alliance == null) { //Default to blue, as it is the lower bracket side
            alliance = SideColor.BLUE;
        }

        //Init Functions
        Drivetrain.init(this);
        Pinpoint.init(this, this.hardwareMap, afterAutoPose);
        Intake.init(this);
//        MatchLoader.init(this);
        BallTunnel.init(this);
        BallGate.init(this);
        Turret.init(this);
        Shooter.init(this);
//        LEDs.init(this);
//        Gyro.init(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

        //Prime Functions (check if auto and if there are alt functions, ex. deploy intake)
        Intake.prime();
        Intake.extend();
    //    MatchLoader.prime();
        BallGate.prime();
//        Turret.prime();
        Shooter.prime();

        double wallRPM = 2600; //TODO: remove just having a single RPM value and get an actual aiming system
        Shooter.targetRPM = wallRPM;

        double deltaTime = 1.0; //Not 0 as that might cause some things to be multiplied by 0 that shouldn't be
        double lastLoopTimestamp = 0.0;

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            for (LynxModule hub : allHubs) { //Bulk read first thing
                hub.clearBulkCache();
            }
            telemetry.addData("Control Hub Voltage", controlHub.getInputVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Expansion Hub Voltage", expansionHub.getInputVoltage(VoltageUnit.VOLTS));
//            telemetry.addData("Control Hub Aux Rail Voltage", controlHub.getAuxiliaryVoltage(VoltageUnit.VOLTS));
//            telemetry.addData("Expansion Hub Aux Rail Voltage", expansionHub.getAuxiliaryVoltage(VoltageUnit.VOLTS));





            //Controls
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;
            boolean slowMode = false;

            boolean intakeButton = gamepad1.left_trigger > 0.5;
            boolean ejectButton = gamepad1.left_bumper;

            boolean shootButton = gamepad1.right_trigger > 0.5;

            boolean matchLoaderButton = gamepad1.a; //TODO: switch to something on GP2

            //Overrides/Safeties
            boolean relocalize = gamepad1.back;

            boolean intakePivotOverride = gamepad1.right_bumper;

            boolean ballTunnelReverse = false; //TODO: determine control (GP2 or when reverse intake is held with a modifier)

            boolean fudgeUp = gamepad2.dpadUpWasPressed();
            boolean fudgeDown = gamepad2.dpadDownWasPressed();
            boolean resetRPM = gamepad2.dpad_left;
            boolean zeroRPM = gamepad2.start;
            if (fudgeUp) { Shooter.targetRPM += 50; }
            if (fudgeDown) { Shooter.targetRPM -= 50; }
            if (resetRPM) { Shooter.targetRPM = wallRPM; }
            if (zeroRPM || ((runtime.seconds() > 121))) { Shooter.targetRPM = 0; } //Auto de-power shooter at timer end incase we are slow on the DS


            //Tick Functions
            Drivetrain.updateDrive(strafe, drive, turn, slowMode, relocalize);
            Pinpoint.update(false);  //TODO: fix and make work later
            Intake.update(intakeButton, ejectButton, (/*BallTunnel.full || */intakePivotOverride));
//            MatchLoader.update(matchLoaderButton);
            BallTunnel.update(shootButton, ballTunnelReverse, intakeButton);
            BallGate.update(shootButton);
            Shooter.update(deltaTime);
//            LEDs.update(tbd, runtime.seconds() > 110, runtime); //Flash at last 10 sec for parking reminder

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();
//            dLogger.update();

            //Deltatime
            deltaTime = runtime.milliseconds() - lastLoopTimestamp;
            lastLoopTimestamp = runtime.milliseconds();
        }
//        LEDs.turnOff();
//        try {
//            logger.write();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}