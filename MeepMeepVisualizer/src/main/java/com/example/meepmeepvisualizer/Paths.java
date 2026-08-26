package com.example.meepmeepvisualizer;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


public class Paths {
    public static Action directPath(RoadRunnerBotEntity drive, Pose2d startPos, Pose2d endPose, double minSpeed){
        double tangent = Math.atan2(endPose.position.y - startPos.position.y, endPose.position.x - startPos.position.x);
        return drive.getDrive().actionBuilder(startPos)
                .setTangent(tangent)
                .splineToLinearHeading(endPose, tangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
}