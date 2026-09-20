package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

public final class CustomActions {

    private CustomActions() {
    }
    public static void runBlocking(Action a) {
        final FtcDashboard dash = FtcDashboard.getInstance();
        final Canvas c = new Canvas();
        a.preview(c);

        boolean b = true;
        while (b && !Thread.currentThread().isInterrupted()) {
            final TelemetryPacket p = new TelemetryPacket();
            p.fieldOverlay().getOperations().addAll(c.getOperations());

            b = a.run(p);

            dash.sendTelemetryPacket(p);
        }
    }

    public static void runBlockingBulkRead(Action a, List<LynxModule> allHubs) {
        final FtcDashboard dash = FtcDashboard.getInstance();
        final Canvas c = new Canvas();
        a.preview(c);

        boolean b = true;
        while (b && !Thread.currentThread().isInterrupted()) {
            for (LynxModule hub : allHubs) { //Bulk read first thing
//                double voltage = hub.getInputVoltage(VoltageUnit.VOLTS); //Log at some point
//                double current = hub.getCurrent(CurrentUnit.AMPS); //Log at some point
                hub.clearBulkCache();
            }

            final TelemetryPacket p = new TelemetryPacket();
            p.fieldOverlay().getOperations().addAll(c.getOperations());

            b = a.run(p);

            dash.sendTelemetryPacket(p);
        }
    }
}