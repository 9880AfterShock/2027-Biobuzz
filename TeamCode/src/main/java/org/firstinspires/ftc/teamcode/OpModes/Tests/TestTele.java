package org.firstinspires.ftc.teamcode.OpModes.Tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.Intaking.Intake;
import org.firstinspires.ftc.teamcode.Mechanisms.Tests.TestDrivetrain;
import org.firstinspires.ftc.teamcode.Mechanisms.Tests.TestIntake;
import org.firstinspires.ftc.teamcode.Sensors.Gyro;

import java.util.List;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Test TeleOp")
@Disabled
public class TestTele extends LinearOpMode {
    public static List<LynxModule> allHubs;

    // Declare OpMode members.
    ElapsedTime runtime = new ElapsedTime();
    int loops = 0;

    @Override
    public void runOpMode() {
        //Below is for if you want FTC dashboard telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class);
        LynxModule controlHub = hardwareMap.get(LynxModule.class, "Control Hub");
        LynxModule expansionHub = hardwareMap.get(LynxModule.class, "Expansion Hub 2");

        //Init Functions
        Gyro.init(this);
        TestDrivetrain.init(this);
//        TestIntake.init(this);
        Intake.init(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        Intake.prime();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

//        telemetry.setMsTransmissionInterval(100);

        while (opModeIsActive()) {
            for (LynxModule hub : allHubs) {
                hub.clearBulkCache();
            }
            telemetry.addData("Control Hub Voltage", controlHub.getInputVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Expansion Hub Voltage", expansionHub.getInputVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Control Hub Aux Rail Voltage", controlHub.getAuxiliaryVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Expansion Hub Aux Rail Voltage", expansionHub.getAuxiliaryVoltage(VoltageUnit.VOLTS));
            //Tick Functions
            TestDrivetrain.updateDrive(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.right_trigger > 0.5, gamepad1.back);
//            TestIntake.update(gamepad1.left_trigger > 0.5, gamepad1.left_bumper, gamepad1.right_bumper, true/*gamepad1.right_trigger > 0.5*/);
            Intake.update(gamepad1.left_trigger > 0.5, gamepad1.left_bumper, gamepad1.right_bumper, false);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();
        }
    }

}