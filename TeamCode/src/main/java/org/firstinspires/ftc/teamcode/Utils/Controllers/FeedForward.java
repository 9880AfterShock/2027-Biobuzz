package org.firstinspires.ftc.teamcode.Utils.Controllers;

public class FeedForward implements Controller {
    public double kS;
    public double kV;
    public double kP;
    public FeedForward(double kS, double kV, double kP) {
        this.kS = kS;
        this.kV = kV;
        this.kP = kP;
    }
    @Override
    public double step(double target, double current, double deltaTime) {
        return (kS + kV * target) + kP * (target - current);
    }
}
