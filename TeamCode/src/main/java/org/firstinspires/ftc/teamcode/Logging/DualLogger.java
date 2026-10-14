package org.firstinspires.ftc.teamcode.Logging;

import android.os.Build;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.Objects;

public class DualLogger {
    private final Logger logger;
    private Telemetry telemetry;
    public DualLogger(Logger logger, Telemetry telemetry) {
        this.logger = logger;
        this.telemetry = telemetry;
    }

    public void log(@NonNull String name, @NonNull String type, Object data) {
        logger.log(name,type,data);
        telemetry.addData(name, data);
    }

    public void log(@NonNull String name, String data) {
        logger.log(name,data);
    }
    public void log(@NonNull String name, byte data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, short data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, int data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, @NonNull NormalizedRGBA data) {
        logger.log(name,data);
    }
    public void log(@NonNull String name, long data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name) {
        logger.log(name);
    }

    public void log(@NonNull String name, float data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, double data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, boolean data) {
        logger.log(name,data);
    }

    public void log(@NonNull String name, FieldState data) {
        logger.log(name,data);
    }

    public void update() {
        logger.update();
        telemetry.update();
    }

    public void write() throws IOException {
        logger.write();
    }

}