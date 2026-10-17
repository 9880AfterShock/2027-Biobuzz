package org.firstinspires.ftc.teamcode.OpModes.Tests;

import android.service.autofill.FieldClassification;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Shooter;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallGate;
import org.firstinspires.ftc.teamcode.Mechanisms.Transfering.BallTunnel;

import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="SHOOTER RPM TESTING")
public class ShooterTuningTest extends LinearOpMode {
    public static double targetRPM = 0.0;
    public static List<LynxModule> allHubs;
    ElapsedTime runtime = new ElapsedTime();
    int loops = 0;

    @Override
    public void runOpMode() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class); //Set for bulk read
        LynxModule controlHub = hardwareMap.get(LynxModule.class, "Control Hub"); //Individual for voltage readings
        LynxModule expansionHub = hardwareMap.get(LynxModule.class, "Expansion Hub 2");

        //Init Functions
//        Turret.init(this);
        Shooter.init(this);
        BallTunnel.init(this);
        Intake.init(this);
        BallGate.init(this);

        waitForStart();
        runtime.reset();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
            hub.clearBulkCache();
        }

//        Turret.prime();
        Shooter.prime();

        double deltaTime = 1.0; //Not 0 as that might cause some things to be multiplied by 0 that shouldn't be
        double lastLoopTimestamp = 0.0;

        while (opModeIsActive()) {
            for (LynxModule hub : allHubs) { //Bulk read first thing
                hub.clearBulkCache();
            }
//            telemetry.addData("Control Hub Voltage", controlHub.getInputVoltage(VoltageUnit.VOLTS));
//            telemetry.addData("Expansion Hub Voltage", expansionHub.getInputVoltage(VoltageUnit.VOLTS));

            if (gamepad1.dpadUpWasPressed()) {
                targetRPM += 50;
            }
            if (gamepad1.dpadDownWasPressed()) {
                targetRPM -= 50;
            }

            if (gamepad1.dpadLeftWasPressed()) {
                targetRPM += 500;
            }
            if (gamepad1.dpadRightWasPressed()) {
                targetRPM -= 500;
            }

            if (gamepad1.bWasPressed()) {
                targetRPM = 0;
            }
            if (gamepad1.aWasPressed()) {
                targetRPM = 6000;
            }

            targetRPM = Range.clip(targetRPM, 0, 6000);

            if (gamepad1.right_bumper) {
                Shooter.setTargetRPM(0);
            } else {
                Shooter.setTargetRPM(targetRPM);
            }
            Shooter.update(deltaTime);

            BallTunnel.update(gamepad1.right_trigger > 0, gamepad1.right_bumper);
            Intake.update(gamepad1.left_trigger > 0.5, false, false);
            BallGate.update(true);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();

            //Deltatime
            deltaTime = runtime.milliseconds() - lastLoopTimestamp;
            lastLoopTimestamp = runtime.milliseconds();
        }
    }
}