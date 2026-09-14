package com.example.meepmeepvisualizer;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MeepMeepVisualizer {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 343.45/25.4)
                .setDimensions(17.0,427.175/25.4)
                .build();


        double driveSpeed = 60.0; //zoom
        double intakeSpeed = 15.0; //TBD, prob don't need
        double shotTime = 3.0; //very on safe side
        Pose2d startPos = new Pose2d(60.0, -15.0, Math.toRadians(0.0)); //prob 59, 14.smth
        Pose2d gardenPos = new Pose2d(-15.0, -62.0, Math.toRadians(0.0));
        Pose2d shootPos2 = new Pose2d(-57, -24, Math.toRadians(180.0));
        Pose2d flowerPos1 = new Pose2d(-57, -24, Math.toRadians(180.0));

        Pose2d endPos = new Pose2d(0, 0, Math.toRadians(0.0));

        myBot.runAction(
                new SequentialAction(
                        Paths.wait(myBot, shotTime),
                        Paths.directPath(myBot, startPos, gardenPos, driveSpeed),
                        Paths.wait(myBot, shotTime),
                        Paths.directPath(myBot, gardenPos, shootPos2, driveSpeed),
                        Paths.wait(myBot, shotTime),
                        Paths.directPath(myBot, shootPos2, flowerPos1, driveSpeed)

                )
        );

//        Pose2d startPos = new Pose2d(0.0, 0.0, Math.toRadians(0.0));
//        Pose2d endPos = new Pose2d(24.0, 24.0, Math.toRadians(90.0));
//
//        myBot.runAction(
//                Paths.directPath(myBot, startPos, endPos, 60)
//        );

//        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(0, 0, 0))
//                .lineToX(30)
//                .turn(Math.toRadians(90))
//                .lineToY(30)
//                .turn(Math.toRadians(90))
//                .lineToX(0)
//                .turn(Math.toRadians(90))
//                .lineToY(0)
//                .turn(Math.toRadians(90))
//                .build());


        Image img = null;
        try { img = ImageIO.read(new File("Fields/Biobuzz-Juice.png")); }
        catch(IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();

//        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
//                .setDarkMode(true)
//                .setBackgroundAlpha(0.95f)
//                .addEntity(myBot)
//                .start();
    }
}