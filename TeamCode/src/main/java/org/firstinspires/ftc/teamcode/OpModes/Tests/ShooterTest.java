package org.firstinspires.ftc.teamcode.OpModes.Tests;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Mechanisms.Tests.TestShooter;

import java.util.List;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Shooter Test")
public class ShooterTest extends LinearOpMode {
    public static List<LynxModule> allHubs;

    // Declare OpMode members.
    ElapsedTime runtime = new ElapsedTime();
    int loops = 0;

    @Override
    public void runOpMode() {
        allHubs = hardwareMap.getAll(LynxModule.class);
        //Init Functions
        TestShooter.init(this);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            for (LynxModule hub : allHubs) { //Bulk read first thing
//                double voltage = hub.getInputVoltage(VoltageUnit.VOLTS); //Log at some point
//                double current = hub.getCurrent(CurrentUnit.AMPS); //Log at some point
                hub.clearBulkCache();
            }

            //Tick Functions
            TestShooter.update(gamepad1.bWasPressed(), gamepad1.a);


            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();
        }
    }

}