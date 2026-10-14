package org.firstinspires.ftc.teamcode.Mechanisms.Transfering;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Config
public class BallTunnel {
    public static boolean full = false; //TODO: Use sensors to determine when the ball tunnel is full
    private static DcMotorEx feeder;
    private static OpMode opmode;
    public static double feederPower;

    public static double transferPower = 1.0;
    public static double holdPower = 0.5; //to tune (and maybe the others here)
    public static double ejectPower = -1.0;

    public static void init(OpMode opmode) {
        feeder = opmode.hardwareMap.get(DcMotorEx.class, "ballTunnelFeeder"); //Port _ on ___ hub

        feeder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        feeder.setDirection(DcMotorSimple.Direction.FORWARD);
        feeder.setPower(0.0);

        BallTunnel.opmode = opmode;
    }

    public static void setFeederPower(double power) {
        power = Range.clip(power, -1, 1);
        if (feederPower != power) {
            feederPower = power;
            feeder.setPower(power);
        }
    }

    public static void update(boolean shooting, boolean ejecting) {
        if (shooting){
            setFeederPower(transferPower);
        } else {
            if (ejecting) {
                setFeederPower(ejectPower);
            } else {
                setFeederPower(holdPower);
            }
        }

        opmode.telemetry.addData("Feeder Power", feederPower);
        opmode.telemetry.addData("Feeder Amps", feeder.getCurrent(CurrentUnit.AMPS));
    }

    public static Action autoBalltunnelShoot() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setFeederPower(transferPower);
                return false;
            }
        };
    }

    public static Action autoBalltunnelIdle() {
        return new Action() {
            public boolean run(@NonNull TelemetryPacket packet) {
                setFeederPower(holdPower);
                return false;
            }
        };
    }
}