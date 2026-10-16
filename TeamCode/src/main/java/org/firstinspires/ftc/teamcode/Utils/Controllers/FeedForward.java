package org.firstinspires.ftc.teamcode.Utils.Controllers;

public class FeedForward implements Controller {
    public double kS;
    public double kV;
    public double kA;
    public FeedForward(double kS, double kV, double kA) {
        this.kS = kS;
        this.kV = kV;
        this.kA = kA;
    }
    @Override
    public double step(double target, double current, double deltaTime) {
        double sign = Math.signum(target);
        if (target != 0) {
            return (kS * sign + kV * target) + kA * ((target - current) / deltaTime);
        } else {
            return 0;
        }
    }
}
