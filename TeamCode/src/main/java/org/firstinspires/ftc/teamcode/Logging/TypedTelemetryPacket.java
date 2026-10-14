package org.firstinspires.ftc.teamcode.Logging;

import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TypedTelemetryPacket extends TelemetryPacket {
    private long timestamp;
    public SortedMap<String, String> data;
    public SortedMap<String, String> dataTypes;
    public SortedMap<String, Double> dataTime;
    private List<String> log;
    private Canvas field;
    private Canvas fieldOverlay;
    private OpMode opMode;

    private static final Canvas DEFAULT_FIELD = new Canvas();

    static {
        DEFAULT_FIELD.setAlpha(0.4);
        DEFAULT_FIELD.drawImage("/dash/decode.webp", 0, 0, 144, 144);
        DEFAULT_FIELD.setAlpha(1.0);
        DEFAULT_FIELD.drawGrid(0, 0, 144, 144, 7, 7);
    }

    /**
     * Creates a new telemetry packet.
     */
    public TypedTelemetryPacket(boolean drawDefaultField, OpMode opMode) {
        this.opMode = opMode;
        data = new TreeMap<>();
        dataTypes = new TreeMap<>();
        dataTime = new TreeMap<>();
        log = new ArrayList<>();
        field = new Canvas();
        fieldOverlay = new Canvas();

        if (drawDefaultField) {
            field.getOperations().addAll(DEFAULT_FIELD.getOperations());
        }
    }


    public TypedTelemetryPacket(OpMode opMode) {
        this(true, opMode);
    }

    /**
     * Stores a single key-value pair.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, Object value) {
        if (value instanceof Double || value instanceof Float || value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte) {
            dataTypes.put(key, "Number");
            data.put(key, value.toString());
        } else if (value instanceof Boolean) {
            dataTypes.put(key, "Bool");
            data.put(key, value.toString());
        } else if (value instanceof NormalizedRGBA) {
            dataTypes.put(key, "Color");
            data.put(key, String.valueOf(((NormalizedRGBA) value).toColor()));
        }
        dataTime.put(key, opMode.getRuntime());
    }

    /**
     * Stores all entries of the provided map.
     *
     * @param map
     */
    @Override
    public void putAll(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds a line to the telemetry log.
     *
     * @param line
     */
    @Override
    public void addLine(String line) {
        log.add(line);
    }

    /**
     * Clears the telemetry log.
     */

    @Override
    public void clearLines() {
        log.clear();
    }

    /**
     * Adds and returns the current timestamp to the packet. This is called automatically when the
     * packet is sent (and any previous timestamp will be overwritten).
     */
    @Override
    public long addTimestamp() {
        timestamp = System.currentTimeMillis();
        return timestamp;
    }

    /**
     * Returns the field overlay canvas.
     */
    @Override
    public Canvas fieldOverlay() {
        return fieldOverlay;
    }

    @Override
    public Canvas field() {
        return field;
    }
}