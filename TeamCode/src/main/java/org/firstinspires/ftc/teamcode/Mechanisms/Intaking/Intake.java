package org.firstinspires.ftc.teamcode.Mechanisms.Intaking;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Config
public class Intake {
    private static DcMotorEx roller;
    private static Servo pivot;
    private static DigitalChannel limitSwitch;
    private static OpMode opmode;
    private static boolean extended;
    private static double rollerPower;
    private static boolean enabled;
    private static boolean dropIntake;

    public static final double startingPos = 0.8; //0.85 if we want to almost touch the side plates //maybe not anymore for the 0.85
    public static final double retractedPos = 0.4;
    public static final double extendedPos = 0.0;
    public static final double intakingPower = 1.0;
    public static final double ejectingPower = -1.0;
    public static final double idlePower = 0.0;

    public static void init(OpMode opmode) {
        enabled = true;
        roller = opmode.hardwareMap.get(DcMotorEx.class, "intakeRoller"); //Port 3 on expansion hub
        pivot = opmode.hardwareMap.get(Servo.class, "intakePivot"); //Port 0 on control hub
        limitSwitch = opmode.hardwareMap.get(DigitalChannel.class, "limitSwitch"); //Port 1 (0/1 pair) on control hub
        limitSwitch.setMode(DigitalChannel.Mode.INPUT);

        roller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
//        roller.setDirection(DcMotorSimple.Direction.REVERSE);
        roller.setPower(0.0);

        Intake.opmode = opmode;
        dropIntake = false;
    }

    public static void prime(){
        pivot.setPosition(startingPos);
        extended = false;
    }

    public static void retract() {
        if (extended) {
            pivot.setPosition(retractedPos);
            extended = false;
        }
    }

    public static void extend() {
        if (!extended) {
            pivot.setPosition(extendedPos);
            extended = true;
        }
    }
    public static void setPivotPowered(boolean powered) {
        if (powered != enabled){
            if (powered) {
                pivot.getController().pwmEnable();
                enabled = true;
            } else {
                pivot.getController().pwmDisable();
                enabled = false;
            }
        }
    }

    public static void setRollerPower(double power) {
        power = Range.clip(power, -1, 1);
        if (rollerPower != power) {
            rollerPower = power;
            roller.setPower(power);
        }
    }

    public static boolean lowered(){
        return limitSwitch.getState();
    }

    public static void update(boolean intaking, boolean ejecting, boolean retract, boolean depower) {
        if (!retract){
            extend();
        } else{
            retract();
        }

        if (depower) {
            dropIntake = !dropIntake;
        }

//        setPivotPowered(!lowered()); //Disable servo at lowest point

        //TODO: deadzone for nectar

        if (intaking){
            setRollerPower(intakingPower);
        } else {
            if (ejecting){
                setRollerPower(ejectingPower);
            } else {
                setRollerPower(idlePower);
            }
            setPivotPowered(dropIntake);
        }

        opmode.telemetry.addData("Extended", extended);
        opmode.telemetry.addData("Intaking", intaking);
        opmode.telemetry.addData("Ejecting", ejecting);

        opmode.telemetry.addData("Lowered (Limit Switch)", lowered());

        opmode.telemetry.addData("Intake Roller Amps", roller.getCurrent(CurrentUnit.AMPS));
    }

    public static Action autoIntakeDown(boolean checkSwitch) {
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                extend();
//                setPivotPowered(!lowered());
                return (!lowered() && timer.seconds() <= 3.0) && checkSwitch; //Timeout, can change
            }
        };
    }

    public static Action autoIntakeUp(boolean checkSwitch) {
        return new Action() {
            private boolean first = true;
            ElapsedTime timer = new ElapsedTime();;
            public boolean run(@NonNull TelemetryPacket packet) {
                if (first){
                    timer.reset();
                    first = false;
                }
                retract();
                return (lowered() && timer.seconds() <= 3.0) && checkSwitch; //Timeout, can change
            }
        };
    }

    public static Action autoIntakeOn() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setRollerPower(intakingPower);
                return false;
            }
        };
    }

    public static Action autoIntakeEject() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setRollerPower(ejectingPower);
                return false;
            }
        };
    }

    public static Action autoIntakeOff() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setRollerPower(idlePower);
                return false;
            }
        };
    }
}