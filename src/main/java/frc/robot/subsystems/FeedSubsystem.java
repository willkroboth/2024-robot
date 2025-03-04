// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FeedSubsystem extends SubsystemBase {

    private CANSparkMax feedMotor;

    public FeedSubsystem(CANSparkMax feedMotor) {
        this.feedMotor = feedMotor;
    }

    public void feed(double speed) {
        feedMotor.set(speed);
    }
}
