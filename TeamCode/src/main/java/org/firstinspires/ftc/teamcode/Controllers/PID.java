package org.firstinspires.ftc.teamcode.Controllers;

import java.util.Optional;

@SuppressWarnings({"all"})
public class PID implements Controller {
    public double p;
    public double i;
    public double d;
    private Optional<Double> lastDiff = Optional.empty();
    private double iAccum = 0;
    public PID(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }
    @Override
    public double step(double target, double current) {
        double diff = target - current;
        double last = lastDiff.orElse(diff);
        double out = diff * p + diff-last + i * iAccum;
        lastDiff = Optional.of(diff);
        iAccum += diff;
        return out;
    }
}
