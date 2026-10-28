package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.Mechanisms.Shooting.Turret;

import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Turret Test")
public class TurretTest extends LinearOpMode {
    public static List<LynxModule> allHubs;

    // Declare OpMode members.
    ElapsedTime runtime = new ElapsedTime();
    int loops = 0;

    @Override
    public void runOpMode() {
        //Below is for if you want FTC dashboard telemetry
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        allHubs = hardwareMap.getAll(LynxModule.class); //Set for bulk read
        LynxModule controlHub = hardwareMap.get(LynxModule.class, "Control Hub"); //Individual for voltage readings
        LynxModule expansionHub = hardwareMap.get(LynxModule.class, "Expansion Hub 2");

        //Init Functions
        Turret.init(this);

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
        Turret.prime();

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

            //Tick Functions
            Turret.updateTest(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.left_stick_button, deltaTime);


            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();

            deltaTime = runtime.milliseconds() - lastLoopTimestamp;
            lastLoopTimestamp = runtime.milliseconds();
        }
    }

}