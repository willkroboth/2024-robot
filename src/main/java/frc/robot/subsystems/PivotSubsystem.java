// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

//Best angle for amp rn: 0.344
//Best speed for amp rn: 0.40

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkAbsoluteEncoder;
import com.revrobotics.CANSparkBase.ControlType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ShooterConstants;

public class PivotSubsystem extends SubsystemBase {

    private CANSparkMax pivotMotor;
    private SparkAbsoluteEncoder pivotEncoder;

    private final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
    private final NetworkTable table = ntInstance.getTable("/components/arm");

    private final NetworkTableEntry ntPivotPosition = table.getEntry("pivotPosition");
    private final NetworkTableEntry ntTargetPivotPosition = table.getEntry("targetPivotPosition");
    private final NetworkTableEntry ntLockPivotPosition = table.getEntry("lockPivotPosition");
    private final NetworkTableEntry ntFF = table.getEntry("FF Volts");
    private final NetworkTableEntry ntP = table.getEntry("P Volts");
    private final NetworkTableEntry ntI = table.getEntry("I Volts");
    private final NetworkTableEntry ntD = table.getEntry("D Volts");
    private final NetworkTableEntry ntError = table.getEntry("Pivot Error");

    private double PVal = 15;
    private double IVal = 4;
    private double DVal = 0;

    private PIDController pivotPidController = new PIDController(PVal, IVal, DVal);
    private PIDController P = new PIDController(PVal, 0, 0);
    private PIDController I = new PIDController(0, IVal, 0);
    private PIDController D = new PIDController(0, 0, DVal);

    private double kG = 0.53;

    private ArmFeedforward armFeedforward = new ArmFeedforward(0, kG, 0);

    private double targetPos;
    private double lockPos;

    public PivotSubsystem(CANSparkMax pivotMotor) {
        this.pivotMotor = pivotMotor;
        this.pivotEncoder = pivotMotor.getAbsoluteEncoder(SparkAbsoluteEncoder.Type.kDutyCycle);
        this.pivotEncoder.setZeroOffset(ShooterConstants.PIVOT_OFFSET);

        pivotPidController.enableContinuousInput(0, 1);

        targetPos = MathUtil.clamp(pivotEncoder.getPosition(), 0.25, 0.4);
        lockPos = MathUtil.clamp(pivotEncoder.getPosition(), 0.25, 0.4);
    }

    public void setPivotMotor(double speed) {
        pivotMotor.set(speed);
    }

    public void setPivotPosition(double pos) {
        pivotMotor.setVoltage(armFeedforward.calculate(pos, 0) - pivotPidController.calculate(pivotEncoder.getPosition(), pos));
        ntFF.setDouble(armFeedforward.calculate(pos, 0));
        ntP.setDouble(-P.calculate(pivotEncoder.getPosition(), pos));
        ntI.setDouble(-I.calculate(pivotEncoder.getPosition(), pos));
        ntD.setDouble(-D.calculate(pivotEncoder.getPosition(), pos));
        ntError.setDouble((pos-pivotEncoder.getPosition()));
    }

    public void updatePivotPosition() {
        ntPivotPosition.setDouble(pivotEncoder.getPosition());
    }

    public double getPivotPosition() {
        return ntPivotPosition.getDouble(0);
    }

    @Override
    public void periodic() {
        ntPivotPosition.setDouble(pivotEncoder.getPosition());
        ntTargetPivotPosition.setDouble(targetPos);
        ntLockPivotPosition.setDouble(lockPos);
    }

    public void setTargetPos(double targetPos) {
        this.targetPos = targetPos;
    }

    public double getTargetPos(){
        return targetPos;
    }

    public void setLockPos(double lockPos) {
        this.lockPos = lockPos;
    }

    public double getLockPos() {
        return lockPos;
    }
}
