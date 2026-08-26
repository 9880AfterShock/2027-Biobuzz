package org.firstinspires.ftc.teamcode.OpModes.Autos;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class Paths {
    public Action directPath(MecanumDrive drive, Pose2d startPos, Pose2d endPose, double minSpeed){
        double tangent = Math.atan2(endPose.position.y - startPos.position.y, endPose.position.x - startPos.position.x);
        return drive.actionBuilder(startPos)
                .setTangent(tangent)
                .splineToLinearHeading(endPose, tangent, new TranslationalVelConstraint(minSpeed))
                .build();
    }
}