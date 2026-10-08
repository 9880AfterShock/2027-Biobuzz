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


        int posMultiplier = 1;
        double driveSpeed = 60.0; //Probably max we can do
        double intakeSpeed = 15.0; //For pickup on garden
        double shotTime = 3.0; //Very on safe side for now
        double intakeTime = 1.0; //For garden
        double flowerIntakeTime = 1.0; //For flower pickup
        Pose2d startPos = new Pose2d(posMultiplier*63.303150, posMultiplier*-51.391043, posMultiplier*Math.toRadians(-90.0)); //Temp until we fine a way of aligning that we like (right now its 1 inch from first garden pollen to front bumper)
        Pose2d gardenPos = new Pose2d(posMultiplier*63.303150, posMultiplier*-60.542169, posMultiplier*Math.toRadians(-90.0));
        Pose2d gardenClearPos = new Pose2d(gardenPos.position.x - posMultiplier*5, gardenPos.position.y + posMultiplier*5, gardenPos.heading.toDouble());
        Pose2d preflowerPos1 = new Pose2d(posMultiplier*-52.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        Pose2d flowerPos1 = new Pose2d(posMultiplier*-57.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        Pose2d preflowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-52.0, posMultiplier*Math.toRadians(270.0));
        Pose2d flowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-57.0, posMultiplier*Math.toRadians(270.0));
        Pose2d shootPos4 = new Pose2d(posMultiplier*40.0, posMultiplier*-40.0, posMultiplier*Math.toRadians(180.0));

        Pose2d parkPos1 = new Pose2d(posMultiplier*-20, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));
        Pose2d parkPos2 = new Pose2d(posMultiplier*-50, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));

        myBot.runAction(
                new SequentialAction(
                        Paths.wait(myBot, shotTime), //preloads, 1st tip
                        Paths.directPath(myBot, startPos, gardenPos, intakeSpeed), //pickup garden
                        Paths.wait(myBot, intakeTime), //wait for balls, should not be long
                        Paths.tangentSplinePath(myBot, gardenPos, Math.toRadians(165.0), preflowerPos1, Math.toRadians(150.0), driveSpeed), //Go to 1st flower, other side to shoot
                        Paths.wait(myBot, shotTime), //1st half of 2nd tip
                        Paths.directPath(myBot, preflowerPos1, flowerPos1, driveSpeed), //Press into 1st flower
                        Paths.wait(myBot, flowerIntakeTime), //Wait for balls
                        Paths.wait(myBot, shotTime), //2nd half of 2nd tip
                        Paths.directPath(myBot, flowerPos1, preflowerPos2, driveSpeed), //to 2nd flower
                        Paths.directPath(myBot, preflowerPos2, flowerPos2, driveSpeed), //Press into 2nd flower
                        Paths.wait(myBot, flowerIntakeTime), //Wait for balls

                        //For 3rd tip
                        Paths.wait(myBot, intakeTime), //intake, may need to do smth special for match loader
                        Paths.directPath(myBot, flowerPos2, shootPos4, driveSpeed), //go to shoot, will need partner to use preloads for final tip
                        Paths.wait(myBot, shotTime), //partial 3rd tip, partner preloads should finish
                        Paths.directPath(myBot, shootPos4, parkPos1, driveSpeed) //to park
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