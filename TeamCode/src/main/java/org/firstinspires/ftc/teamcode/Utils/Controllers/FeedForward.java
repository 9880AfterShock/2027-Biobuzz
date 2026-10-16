package org.firstinspires.ftc.teamcode.Utils.Controllers;

public class FeedForward implements Controller {
    public double kS;
    public double kV;
//    public double kA;
    public FeedForward(double kS, double kV/*, double kA*/) {
        this.kS = kS;
        this.kV = kV;
//        this.kA = kA;
    }
    @Override
    public double step(double target, double current, double deltaTime) {
        return (kS * Math.signum(target)) + (kV * target) /*+ (kA * ((target - current) / deltaTime))*/; //kS is only for getting up to speed, PID takes care of it otherwise
    }
}
