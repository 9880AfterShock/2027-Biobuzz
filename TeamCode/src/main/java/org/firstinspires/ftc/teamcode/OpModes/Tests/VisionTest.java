package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.Enums.SideColor;
import org.firstinspires.ftc.teamcode.Sensors.Vision.Limelight;

import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="Vision Test")
public class VisionTest extends LinearOpMode {
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
        Limelight.initDetection(this);

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
        //GO HERE

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            for (LynxModule hub : allHubs) { //Bulk read first thing
                hub.clearBulkCache();
            }

            //Tick Functions
            Limelight.update();
            telemetry.addData("Seeing RED tags", Limelight.hiveSideUp(SideColor.RED));
            telemetry.addData("Seeing BLUE tags", Limelight.hiveSideUp(SideColor.BLUE));


            telemetry.addData("Status", "Run Time: " + runtime.toString());
            loops += 1;
            telemetry.addData("Looptime (MS per Loop)", runtime.milliseconds()/loops);
            telemetry.update();
        }
    }

}