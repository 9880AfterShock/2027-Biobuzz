package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class Paths {
    public static Action directPath(MecanumDrive drive, Pose2d startPos, Pose2d endPose, double minSpeed){
        double tangent = Math.atan2(endPose.position.y - startPos.position.y, endPose.position.x - startPos.position.x);
        return drive.actionBuilder(startPos)
                .setTangent(tangent)
                .splineToLinearHeading(endPose, tangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
    public static Action tangentPath(MecanumDrive drive, Pose2d startPos, double leaveTangent, Pose2d endPose, double arriveTangent, double minSpeed){ //Really just a shortcut for spline to linear
        return drive.actionBuilder(startPos)
                .setTangent(leaveTangent)
                .splineToLinearHeading(endPose, arriveTangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
    public static Action wait(MecanumDrive drive, double seconds){
        Pose2d throwawayPos = new Pose2d(0.0, 0.0, Math.toRadians(0.0));
        return drive.actionBuilder(throwawayPos)
                .waitSeconds(seconds)
                .build();
    }
}