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


        double speed = 60;
        double tileSize = 12;
        Pose2d startPos = new Pose2d(tileSize*0, tileSize*0, Math.toRadians(0.0));
        Pose2d pos1 = new Pose2d(tileSize*1, tileSize*0, Math.toRadians(0.0));
        Pose2d pos2 = new Pose2d(tileSize*1, tileSize*1, Math.toRadians(0.0));
        Pose2d pos3 = new Pose2d(tileSize*0, tileSize*1, Math.toRadians(0.0));
        Pose2d pos4 = new Pose2d(tileSize*0, tileSize*2, Math.toRadians(0.0));
        Pose2d pos5 = new Pose2d(tileSize*1, tileSize*2, Math.toRadians(0.0));
        Pose2d pos6 = new Pose2d(tileSize*1, tileSize*3, Math.toRadians(0.0));
        Pose2d pos7 = new Pose2d(tileSize*0, tileSize*3, Math.toRadians(0.0));
        Pose2d pos8 = new Pose2d(tileSize*0, tileSize*4, Math.toRadians(0.0));
        Pose2d pos9 = new Pose2d(tileSize*2, tileSize*4, Math.toRadians(90.0));
        Pose2d pos10 = new Pose2d(tileSize*2, tileSize*0, Math.toRadians(180.0));
        Pose2d endPos = new Pose2d(tileSize*0, tileSize*0, Math.toRadians(0.0));

        myBot.runAction(
                new SequentialAction(
                        Paths.directPath(myBot, startPos, pos1, speed),
                        Paths.directPath(myBot, pos1, pos2, speed),
                        Paths.directPath(myBot, pos2, pos3, speed),
                        Paths.directPath(myBot, pos3, pos4, speed),
                        Paths.directPath(myBot, pos4, pos5, speed),
                        Paths.directPath(myBot, pos5, pos6, speed),
                        Paths.directPath(myBot, pos6, pos7, speed),
                        Paths.directPath(myBot, pos7, pos8, speed),
                        Paths.directPath(myBot, pos8, pos9, speed),
                        Paths.directPath(myBot, pos9, pos10, speed),
                        Paths.directPath(myBot, pos10, endPos, speed)
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
        try { img = ImageIO.read(new File("Fields/Blank_Field_6_by_6.png")); }
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