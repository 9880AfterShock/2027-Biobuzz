package org.firstinspires.ftc.teamcode.Logging;

import android.os.Build;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Logger {
    private String prefix = "";

    private int cycleNum = 0;
    private final OpMode opmode;
    private final ArrayList<String> loggedData = new ArrayList<>();
    private final HashMap<String, Boolean> boolMap = new HashMap<>();

    public Logger(OpMode opMode) {
        this.opmode = opMode;
    }
    public Logger(OpMode opMode, String prefix) {
        this.opmode = opMode;
        this.prefix = prefix+" - ";
        log("Cycle Start", "CycleNumber" ,cycleNum);
    }

    public void log(@NonNull String name, @NonNull String type, Object data) {
        String event =  "["+opmode.getRuntime()+"] "+name+" | "+type+" : "+data.toString();
        loggedData.add(event);
    }

    public void log(@NonNull String name, String data) {
        log(name, "String", data);
    }
    public void log(@NonNull String name, byte data) {
        log(name, "Number", data);
    }

    public void log(@NonNull String name, short data) {
        log(name, "Number", data);
    }

    public void log(@NonNull String name, int data) {
        log(name, "Number", data);
    }

    public void log(@NonNull String name, @NonNull NormalizedRGBA data) {
        log(name, "Color", data.toColor());
    }
    public void log(@NonNull String name, long data) {
        log(name, "Number", data);
    }

    public void log(@NonNull String name) {
        log(name, "Event", "");
    }

    public void log(@NonNull String name, float data) {
        log(name, "Number", data);
    }

    public void logTypedTelemetryPacket(TypedTelemetryPacket telemetryPacket) {
        for (Map.Entry<String, Double> entry : telemetryPacket.dataTime.entrySet().stream().sorted().collect(Collectors.toList())) {
            String event =  "["+entry.getValue()+"] "+entry.getKey()+" | "+telemetryPacket.dataTypes.get(entry.getKey())+" : "+telemetryPacket.data.get(entry.getKey());
            loggedData.add(event);
        }
    }

    public void log(@NonNull String name, double data) {
        log(name, "Number", data);
    }

    public void log(@NonNull String name, boolean data) {
        if (Objects.equals(boolMap.get(name), data)) {
            log(name, "Bool", data);
            boolMap.put(name, data);
        }
    }

    public void log(@NonNull String name, FieldState data) {
        log(name, "FieldState", data);
    }

    public void update() {
        cycleNum += 1;
        log("Cycle Start", "CycleNumber", cycleNum);
    }

    public void write() throws IOException {
        String filename;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Clock clock = Clock.systemDefaultZone();
            filename = clock.instant().toString() + ".log";
        } else {
            DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
            Date date = new Date();
            filename = formatter.format(date) + ".log";
        }
        File logFile = new File(opmode.hardwareMap.appContext.getFilesDir(),prefix+filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
        for (String line : loggedData) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
}