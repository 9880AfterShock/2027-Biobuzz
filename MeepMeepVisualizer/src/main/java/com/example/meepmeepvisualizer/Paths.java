package com.example.meepmeepvisualizer;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;


public class Paths {
    public static Action directPath(RoadRunnerBotEntity drive, Pose2d startPos, Pose2d endPose, double minSpeed){
        double tangent = Math.atan2(endPose.position.y - startPos.position.y, endPose.position.x - startPos.position.x);
        return drive.getDrive().actionBuilder(startPos)
                .setTangent(tangent)
                .splineToLinearHeading(endPose, tangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
    public static Action tangentPath(RoadRunnerBotEntity drive, Pose2d startPos, double leaveTangent, Pose2d endPose, double arriveTangent, double minSpeed){ //Really just a shortcut for spline to linear
        return drive.getDrive().actionBuilder(startPos)
                .setTangent(leaveTangent)
                .splineToLinearHeading(endPose, arriveTangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
    public static Action wait(RoadRunnerBotEntity drive, double seconds){
        Pose2d throwawayPos = new Pose2d(0.0, 0.0, Math.toRadians(0.0));
        return drive.getDrive().actionBuilder(throwawayPos)
                .waitSeconds(seconds)
                .build();
    }
}