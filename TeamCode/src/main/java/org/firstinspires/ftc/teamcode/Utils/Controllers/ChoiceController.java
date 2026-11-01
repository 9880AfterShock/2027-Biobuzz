package org.firstinspires.ftc.teamcode.Utils.Controllers;

import java.util.ArrayList;
import java.util.ListIterator;

public class ChoiceController implements Controller {
    private ArrayList<Double> maxError;
    private ArrayList<Controller> controllers;

    public ChoiceController() {
        maxError = new ArrayList<>();
        controllers = new ArrayList<>();
    }

    public void addController(Controller controller, double maxError) {
        this.maxError.add(maxError);
        controllers.add(controller);
    }

    @Override
    public double step(double target, double current, double deltaTime) {
        double error = current-target;
        for (int i = 0; i < maxError.size(); i++) {
            if (Math.abs(error) < maxError.get(i)) {
                return controllers.get(i).step(target,current,deltaTime);
            }
        }
        return 0;
    }
}
