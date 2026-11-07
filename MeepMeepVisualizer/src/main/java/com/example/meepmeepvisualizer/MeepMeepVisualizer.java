package com.example.meepmeepvisualizer;

import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public class MeepMeepVisualizer {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 343.45/25.4)
                .setDimensions(17.0,427.175/25.4)
                .build();



        //Right Side pre+buddy park
        /*Pose2d startPos;
        Pose2d preGardenPos;
        Pose2d gardenPos;
        Pose2d traversePos;
        Pose2d shootPos2;
        Pose2d postShootPos2;
        Pose2d preParkPos;
        Pose2d parkPos;

        int posMultiplier = 1;

        double driveSpeed = 60.0; //Probably max we can do
        double ramSpeed = 20.0; //Being safer
        double shotTime = 6.0; //Very on safe side for now

        startPos = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posMultiplier*Math.toRadians(0.0));

        preParkPos = new Pose2d(posMultiplier*24, posMultiplier*-48, posMultiplier*Math.toRadians(0.0));
        parkPos = new Pose2d(posMultiplier*-25, posMultiplier*-60, posMultiplier*Math.toRadians(0.0));

        myBot.runAction(
                new SequentialAction(
                        Paths.wait(myBot, shotTime), //preloads

                        Paths.directPath(myBot, startPos, preParkPos, driveSpeed), //get ready to ram
                        Paths.directPath(myBot, preParkPos, parkPos, ramSpeed) //to park
                )
        );*/

        //Left Side preloads + park w/ delay
        /*
        Pose2d startPos;
        Pose2d parkPos;

        int posMultiplier = 1;

        double driveSpeed = 60.0; //Probably max we can do
        double shotTime = 4.0; //Very on safe side for now

        startPos = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posMultiplier*Math.toRadians(180.0)); //Opposite side of the field
        parkPos = new Pose2d(posMultiplier*-48, posMultiplier*-60, posMultiplier*Math.toRadians(180.0));


        myBot.runAction(
                new SequentialAction(
                        Paths.wait(myBot, shotTime), //half of 2nd tip

                        //Buddy Park
                        Paths.tangentLinearPath(myBot, startPos, posMultiplier*Math.toRadians(0.0), parkPos, posMultiplier*Math.toRadians(270.0), driveSpeed) //Park
                )
        );*/

        //LM0 8 Buddy Park (4 left)
        /*
        Pose2d startPosRight;
        Pose2d startPosLeft;
        Pose2d preGardenPos;
        Pose2d gardenPos;
        Pose2d traversePos;
        Pose2d shootPosLeft;
        Pose2d shootPosRight;
        Pose2d rotatePosRight;
        Pose2d preParkPosRight;
        Pose2d parkPosRight;
        Pose2d clearPosLeft;
        Pose2d preParkPosLeft;
        Pose2d parkPosLeft;

        int posMultiplier = -1;
        double posRotation = Math.toRadians(90 - posMultiplier*90);

        double driveSpeed = 60.0; //Probably max we can do
        double ramSpeed = 20.0; //Being safer
        double shotTime = 5.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden

        startPosRight = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posRotation+Math.toRadians(0.0));
        startPosLeft = new Pose2d(posMultiplier*-63.591043, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        preGardenPos = new Pose2d(posMultiplier*57.742, posMultiplier*-63.303150, posRotation+Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.542169, posMultiplier*-65, posRotation+Math.toRadians(0.0));
        shootPosLeft = new Pose2d(posMultiplier*-60.542169, posMultiplier*-10, posRotation+Math.toRadians(180.0)); //Opposite side of the field
        shootPosRight = new Pose2d(posMultiplier*60.542169, posMultiplier*-10, posRotation+Math.toRadians(0.0)); //Same as start but intake is out now
        rotatePosRight = new Pose2d(posMultiplier*40, posMultiplier*-10, posRotation+Math.toRadians(180.0));

        preParkPosRight = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(0.0));
        parkPosRight = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(0.0));
        clearPosLeft = new Pose2d(posMultiplier*-24, posMultiplier*-40, posRotation+Math.toRadians(180.0));
        preParkPosLeft = new Pose2d(posMultiplier*24, posMultiplier*-48, posRotation+Math.toRadians(180.0));
        parkPosLeft = new Pose2d(posMultiplier*-25, posMultiplier*-60, posRotation+Math.toRadians(180.0));


        myBot.runAction(
                new SequentialAction(
//                        /*RIGHT START
                        Paths.wait(myBot, shotTime), //preloads, 1st tip
                        Paths.directPath(myBot, startPosRight, preGardenPos, driveSpeed),
                        //Intake On
                        Paths.directPath(myBot, preGardenPos, gardenPos, driveSpeed), //pickup garden
                        Paths.wait(myBot, intakeTime), //wait for vectoring

                        Paths.directPath(myBot, gardenPos, shootPosRight, driveSpeed),

                        //If hive is already tipped
                        Paths.directSplinePath(myBot, shootPosRight, rotatePosRight, driveSpeed), //LIFT INTAKE HERE
                        Paths.directPath(myBot, rotatePosRight, shootPosLeft, driveSpeed),

                        Paths.wait(myBot, shotTime), //half of 2nd tip
                        //RIGHT END */
                        /*LEFT START
                        //Wait for delay, check LL to see if partner worked
                        //if partner failed
                        Paths.directPath(myBot, shootPosLeft, rotatePosRight, driveSpeed), //Left intake, avoid picking up from 1st pick
                        Paths.directSplinePath(myBot, rotatePosRight, shootPosRight, driveSpeed),


                        Paths.wait(myBot, shotTime), //Just shooting and parking
                        //LEFT END*/
        /*

                        //Park from left side
                        Paths.tangentLinearPath(myBot, shootPosLeft, posRotation+Math.toRadians(0.0), clearPosLeft, posRotation+Math.toRadians(0.0), driveSpeed), //leave shooting
                        //With Buddy Park
                        Paths.directPath(myBot, clearPosLeft, preParkPosLeft, driveSpeed), //get ready to ram
                        Paths.directPath(myBot, preParkPosLeft, parkPosLeft, ramSpeed) //to park
                        //Without
//                        Paths.tangentLinearPath(myBot, clearPosLeft,posRotation+Math.toRadians(0.0), parkPosLeft,posRotation+Math.toRadians(180.0), ramSpeed) //At push speed to be safe, if partner takes up a lot of the zone


                        //Buddy Park from right side (Without is just a slower speed
//                        Paths.directPath(myBot, shootPosRight, preParkPosRight, driveSpeed), //get ready to ram
//                        Paths.directPath(myBot, preParkPosRight, parkPosRight, ramSpeed) //to park
                )
        );//*/


        //LM1 Adaptive Auto
/*
        Pose2d startPos;
        Pose2d gardenPos;
        Pose2d cornerPos;
//    Pose2d gardenClearPos;
        Pose2d preflowerPos1;
        Pose2d flowerPos1;
        Pose2d preflowerPos2;
        Pose2d flowerPos2;
        Pose2d shootPos4;
        Pose2d parkPos1;
        Pose2d parkPos2;

        int posMultiplier = 1;

        double driveSpeed = 60.0; //Probably max we can do
        double intakeSpeed = 5.0; //For pickup on garden
        double shotTime = 4.0; //Very on safe side for now
        double intakeTime = 0.5; //For garden
        double flowerIntakeTime = 3.0; //For flower pickup

        startPos = new Pose2d(posMultiplier*63.303150, posMultiplier*-49.242169, posMultiplier*Math.toRadians(-90.0)); //Temp until we fine a way of aligning that we like (right now its 0.1 inch from first garden pollen to front bumper)
        gardenPos = new Pose2d(posMultiplier*65, posMultiplier*-64, posMultiplier*Math.toRadians(-90.0));
        cornerPos = new Pose2d(posMultiplier*(72.0-Pinpoint.leftDistance), posMultiplier*(-72.0+Pinpoint.frontDistance), posMultiplier*Math.toRadians(-90.0));
//        gardenClearPos = new Pose2d(gardenPos.position.x - posMultiplier*5, gardenPos.position.y + posMultiplier*5, gardenPos.heading.toDouble());
        preflowerPos1 = new Pose2d(posMultiplier*-52.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        flowerPos1 = new Pose2d(posMultiplier*-57.0, posMultiplier*-24.0, posMultiplier*Math.toRadians(180.0));
        preflowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-52.0, posMultiplier*Math.toRadians(270.0));
        flowerPos2 = new Pose2d(posMultiplier*24.0, posMultiplier*-57.0, posMultiplier*Math.toRadians(270.0));
        shootPos4 = new Pose2d(posMultiplier*40.0, posMultiplier*-40.0, posMultiplier*Math.toRadians(180.0));

        parkPos1 = new Pose2d(posMultiplier*-20, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));
        parkPos2 = new Pose2d(posMultiplier*-50, posMultiplier*-55, posMultiplier*Math.toRadians(180.0));


        myBot.runAction(
                new SequentialAction(
                        Paths.wait(myBot, shotTime), //preloads, 1st tip
                        Paths.directPath(myBot, startPos, gardenPos, intakeSpeed), //pickup garden
                        Paths.wait(myBot, intakeTime), //wait for balls, should not be long
                        Paths.tangentSplinePath(myBot, cornerPos, Math.toRadians(165.0), preflowerPos1, Math.toRadians(150.0), driveSpeed), //Go to 1st flower, other side to shoot
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
        ); */







        //LM2 Adaptive
        ///*
        Pose2d centerStartPosRight;
        Pose2d startPosRight;
        Pose2d startPosLeft;

        Pose2d preGardenPos;
        Pose2d gardenPos;

        Pose2d prePartnerSavePos;
        Pose2d partnerSavePos;

        Pose2d leftTransitPos;
        Pose2d leftFlowerTransitPos;
        Pose2d rightTransitPos;
        Pose2d rightFlowerTransitPos;

        Pose2d preLeftFlowerPos;
        Pose2d leftFlowerPos;
        Pose2d preRightFlowerPos;
        Pose2d rightFlowerPos;

        Pose2d preParkPosRightFlower;
        Pose2d parkPosRightFlower;

        Pose2d preParkPosRight;
        Pose2d parkPosRight;
        Pose2d preParkPosLeft;
        Pose2d parkPosLeft;

        int posMultiplier = 1;
        double posRotation = Math.toRadians(90 - posMultiplier*90);

        double driveSpeed = 80.0; //Probably max we can do
        double ramSpeed = 20.0; //Being safer
        double flowerSpeed = 15.0; //For ramming the matchloader into the flower
        double shotTime = 5.0; //Very on safe side for now
        double intakeTime = 3.0; //For garden vectoring. If we do change to sideways we want a speed of 5 in/sec to vector them in well
        double flowerTime = 2.0; //How long to wait at flower for balls to come down? Maybe just have addition to shoot time

        centerStartPosRight = new Pose2d(posMultiplier*63.591043, posMultiplier*-12.75, posRotation+Math.toRadians(0.0));
        startPosRight = new Pose2d(posMultiplier*57.3816067182, posMultiplier*-63.01968019, posRotation+Math.toRadians(0.0)); //3 inches away from the intake touching the wall when it is down
        startPosLeft = new Pose2d(posMultiplier*-63.591043, posMultiplier*-10.0, posRotation+Math.toRadians(180.0));

        preGardenPos = new Pose2d(posMultiplier*56.0, posMultiplier*-63.01968019, posRotation+Math.toRadians(0.0));
        gardenPos = new Pose2d(posMultiplier*60.3816067182, posMultiplier*-63.01968019, posRotation+Math.toRadians(0.0)); //Consider moving outwards in each direction to make it center on the corner

        prePartnerSavePos = new Pose2d(posMultiplier*36.0, posMultiplier*-10.0, posRotation+Math.toRadians(180.0));
        partnerSavePos = new Pose2d(posMultiplier*58.0, posMultiplier*-12.75, posRotation+Math.toRadians(270.0));

        leftTransitPos = new Pose2d(posMultiplier*-30.0, posMultiplier*-36.0, posRotation+Math.toRadians(180.0));
        leftFlowerTransitPos = new Pose2d(posMultiplier*-50, posMultiplier*-10, posRotation+Math.toRadians(180.0));
        rightTransitPos = new Pose2d(posMultiplier*40.0, posMultiplier*-36.0, posRotation+Math.toRadians(180.0));
        rightFlowerTransitPos = new Pose2d(posMultiplier*-25.0, posMultiplier*-36.0, posRotation+Math.toRadians(270.0));

        preLeftFlowerPos = new Pose2d(posMultiplier*-52, posMultiplier*-24.0, posRotation+Math.toRadians(180.0));
        leftFlowerPos = new Pose2d(posMultiplier*-55.426695, posMultiplier*-24.0, posRotation+Math.toRadians(180.0));

        preRightFlowerPos = new Pose2d(posMultiplier*24.0, posMultiplier*-52, posRotation+Math.toRadians(270.0));
        rightFlowerPos = new Pose2d(posMultiplier*24.0, posMultiplier*-55.426695, posRotation+Math.toRadians(270.0));

        preParkPosRightFlower = new Pose2d(posMultiplier*10.0, posMultiplier*-55.0, posRotation+Math.toRadians(270.0));
        parkPosRightFlower = new Pose2d(posMultiplier*-25.0, posMultiplier*-58.0, posRotation+Math.toRadians(270.0));

        preParkPosLeft = new Pose2d(posMultiplier*24.0, posMultiplier*-48.0, posRotation+Math.toRadians(180.0));
        parkPosLeft = new Pose2d(posMultiplier*-25.0, posMultiplier*-60.0, posRotation+Math.toRadians(180.0));


        myBot.runAction(
                new SequentialAction(
                        /*RIGHT START
                        //Set turret + shooter stuff
                        Paths.wait(myBot, shotTime), //preloads, 1st tip
                        Paths.directPath(myBot, centerStartPosRight, preGardenPos, driveSpeed), //put actual deploy here



                        //Intake On + Down (Down during shooter?)
                        Paths.directPath(myBot, startPosRight, gardenPos, driveSpeed), //Pickup garden, reset ODO after this
                        Paths.wait(myBot, intakeTime), //wait for vectoring
                        //Raise intake
                        //Check hive tip, if didn't tip shoot

                        //Garden to left side:
                        Paths.tangentSplinePath(myBot, gardenPos, posRotation+Math.toRadians(120.0), rightTransitPos, posRotation+Math.toRadians(180.0), driveSpeed),
                        //Intake down here
                        Paths.directPath(myBot, rightTransitPos, leftTransitPos, driveSpeed),
                        Paths.tangentSplinePath(myBot, leftTransitPos, posRotation+Math.toRadians(180.0), preLeftFlowerPos, posRotation+Math.toRadians(135.0), driveSpeed),
                        Paths.wait(myBot, shotTime), //Shoot if tipped, otherwise go straight to pickup

                        //Pickup Left Flower
                        Paths.directPath(myBot, preLeftFlowerPos, leftFlowerPos, flowerSpeed),
                        Paths.wait(myBot, shotTime), //2nd tip if everything worked (if partner shot preloads on left we might have extra shots on goal here)



                        Paths.tangentLinearPath(myBot, leftFlowerPos, posRotation+Math.toRadians(320.0), preParkPosLeft, posRotation+Math.toRadians(340.0), driveSpeed),
//                        Paths.directPath(myBot, leftFlowerPos, leftTransitPos, driveSpeed),
//                        Paths.directPath(myBot, leftTransitPos, preParkPosLeft, driveSpeed), //get ready to ram
                        Paths.directPath(myBot, preParkPosLeft, parkPosLeft, ramSpeed)




                        //Cutting for reliable park

                        /* Left Flower to Right Flower
                        Paths.tangentSplinePath(myBot, leftFlowerPos, posRotation+Math.toRadians(315.0), rightFlowerTransitPos, posRotation+Math.toRadians(0.0), driveSpeed),
//                        Paths.directPath(myBot, leftTransitPos, rightTransitPos, driveSpeed),
                        Paths.tangentLinearPath(myBot, rightFlowerTransitPos, posRotation+Math.toRadians(0.0), preRightFlowerPos, posRotation+Math.toRadians(270.0), driveSpeed),

                        //Pickup Right Flower
                        Paths.directPath(myBot, preRightFlowerPos, rightFlowerPos, flowerSpeed)//,
                        //Check hive vision? Do we have time for either side + buddy park?

                        //To Shoot Left


                        /*Direct Buddy Park
                        Paths.directPath(myBot, rightFlowerPos, preParkPosRightFlower, driveSpeed),
                        //Raise matchloader
                        Paths.directPath(myBot, preParkPosRightFlower, parkPosRightFlower, ramSpeed) //*/

                        //* Left Side
                        Paths.wait(myBot, shotTime), //Wait for vision

                        /*/IF worked fine:
                        Paths.directPath(myBot, startPosLeft, leftFlowerTransitPos, driveSpeed), //Right after here lower intake + matchloader (or do while pulling away?)
                        Paths.directPath(myBot, leftFlowerTransitPos, preLeftFlowerPos, driveSpeed),
//                        Paths.tangentLinearPath(myBot, startPosLeft, posRotation+Math.toRadians(0.0), preLeftFlowerPos, posRotation+Math.toRadians(270.0), driveSpeed), //Don't use, cuts flower too close
                        Paths.directPath(myBot, preLeftFlowerPos, leftFlowerPos, flowerSpeed),
                        Paths.wait(myBot, shotTime),

                        //Buddy park from left flower (dupe) TOOD: cut if we need time, as buddy park from left side is probably unneeded (or they will be way on the right start pos)
                        Paths.tangentLinearPath(myBot, leftFlowerPos, posRotation+Math.toRadians(320.0), preParkPosLeft, posRotation+Math.toRadians(340.0), driveSpeed),
                        Paths.directPath(myBot, preParkPosLeft, parkPosLeft, ramSpeed) //*/

                        //*/IF partner failed, instead:
                        Paths.directPath(myBot, startPosLeft, prePartnerSavePos, driveSpeed), //Right after here lower intake + matchloader
                        Paths.directPath(myBot, prePartnerSavePos, partnerSavePos, driveSpeed),
                        Paths.wait(myBot, shotTime), //Shoot here to cover partner missing

                        Paths.tangentSplinePath(myBot, partnerSavePos, posRotation+Math.toRadians(240.0), rightTransitPos, posRotation+Math.toRadians(180.0), driveSpeed),
                        //Intake down here
                        Paths.directPath(myBot, rightTransitPos, leftTransitPos, driveSpeed),
                        Paths.tangentSplinePath(myBot, leftTransitPos, posRotation+Math.toRadians(180.0), preLeftFlowerPos, posRotation+Math.toRadians(135.0), driveSpeed),

                        //Do left flower like normal, (same as end of the if it worked auto:
                        Paths.directPath(myBot, preLeftFlowerPos, leftFlowerPos, flowerSpeed),
                        Paths.wait(myBot, shotTime),
                        //Buddy park from left flower (dupe) TOOD: cut if we need time, as buddy park from left side is probably unneeded (or they will be way on the right start pos)
                        Paths.tangentLinearPath(myBot, leftFlowerPos, posRotation+Math.toRadians(320.0), preParkPosLeft, posRotation+Math.toRadians(340.0), driveSpeed),
                        Paths.directPath(myBot, preParkPosLeft, parkPosLeft, ramSpeed) //*/
                )
        );//*/

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