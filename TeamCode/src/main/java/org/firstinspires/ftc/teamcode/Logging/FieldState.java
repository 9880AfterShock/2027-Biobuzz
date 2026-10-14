package org.firstinspires.ftc.teamcode.Logging;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class FieldState {
    public enum ObjectType {
        ROBOT,
        APRIL_TAG,
        MARKER,
    }

    public static class FieldObject {
        public int color;
        public double xPos;
        public double yPos;
        public double rot;
        public ObjectType type;
        public String name;

        public FieldObject(String name, ObjectType type, int color, double xPos, double yPos, double rot) {
            this.name = name;
            this.type = type;
            this.color = color;
            this.xPos = xPos;
            this.yPos = yPos;
            this.rot = rot;
        }

        @NonNull
        @Override
        public String toString() {
            return name+","+type+","+color+","+xPos+","+yPos+","+rot;
        }
    }

    private final ArrayList<FieldObject> fieldObjects = new ArrayList<>();

    public void addObject(FieldObject object) {
        fieldObjects.add(object);
    }

    @NonNull
    @Override
    public String toString() {
        return fieldObjects.stream().map(String::valueOf).collect(Collectors.joining("|"));
    }
}