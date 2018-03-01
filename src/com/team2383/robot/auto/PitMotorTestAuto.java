package com.team2383.robot.auto;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.constants;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.Constants;
import com.team2383.robot.subsystems.Drive;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class PitMotorTestAuto extends CommandGroup {
	
	private class MotorDirectionTest extends Command {
		private boolean isReversed;
		private boolean invert;
		private BaseMotorController motor;
		private TalonSRX master;
		private ControlMode oldMode;
		private String key;
		
		public MotorDirectionTest(BaseMotorController motor, TalonSRX master, String key, boolean invert) {
			super(1.0);
			this.motor = motor;
			this.master = master;
			this.key = key;
			this.invert = invert;
		}
		
		public MotorDirectionTest(TalonSRX master, String key, boolean invert) {
			super(1.0);
			this.motor = master;
			this.master = master;
			this.key = key;
			this.invert = invert;
		}

		@Override
		protected void execute() {
			motor.set(ControlMode.PercentOutput, 0.8);
			if (master.getSelectedSensorPosition(0) < 0) {
				isReversed = !invert;
			}
		}

		@Override
		protected boolean isFinished() {
			return this.isTimedOut();
		}

		@Override
		protected void initialize() {
			oldMode = motor.getControlMode();
			drive.resetEncoders();
			this.isReversed = false;
		}

		@Override
		protected void end() {
			motor.set(ControlMode.PercentOutput, 0);
			
			if(Math.abs(master.getSelectedSensorPosition(0)) < Constants.motorTestMinRotations*4096) {
				DriverStation.reportError("FAILED MOTOR ID: " + motor.getDeviceID(), false);
			}
			drive.resetEncoders();
			
			/*
			 * get it driving in the correct direction
			 */
			constants.setConstant(key, !isReversed);
			constants.saveChangesToFile();
		}

		@Override
		protected void interrupted() {
		}
	}

	public PitMotorTestAuto() {
		requires(drive);
		requires(lift);
		
		addSequential(WPILambdas.runUntil(() -> {
			drive.leftMaster.setInverted(false);
			drive.leftFollowerA.setInverted(false);
			drive.leftFollowerB.setInverted(false);
			drive.leftFollowerC.setInverted(false);
			
			drive.leftMaster.set(ControlMode.PercentOutput, 0);
			drive.leftFollowerA.set(ControlMode.PercentOutput, 0);
			drive.leftFollowerB.set(ControlMode.PercentOutput, 0);
			drive.leftFollowerC.set(ControlMode.PercentOutput, 0);
			
			drive.rightMaster.setInverted(false);
			drive.rightFollowerA.setInverted(false);
			drive.rightFollowerB.setInverted(false);
			drive.rightFollowerC.setInverted(false);

			drive.rightMaster.set(ControlMode.PercentOutput, 0);
			drive.rightFollowerA.set(ControlMode.PercentOutput, 0);
			drive.rightFollowerB.set(ControlMode.PercentOutput, 0);
			drive.rightFollowerC.set(ControlMode.PercentOutput, 0);
		}, 2.0));
		addSequential(new MotorDirectionTest(drive.leftMaster, "kDrive_InvertLeftMaster", false));
		addSequential(new MotorDirectionTest(drive.leftFollowerA, drive.leftMaster, "kDrive_InvertLeftA", false));
		addSequential(new MotorDirectionTest(drive.leftFollowerB, drive.leftMaster, "kDrive_InvertLeftB", false));
		addSequential(new MotorDirectionTest(drive.leftFollowerC, drive.leftMaster, "kDrive_InvertLeftC", false));

		addSequential(new MotorDirectionTest(drive.rightMaster, "kDrive_InvertRightMaster", true));
		addSequential(new MotorDirectionTest(drive.rightFollowerA, drive.rightMaster, "kDrive_InvertRightA", true));
		addSequential(new MotorDirectionTest(drive.rightFollowerB, drive.rightMaster, "kDrive_InvertRightB", true));
		addSequential(new MotorDirectionTest(drive.rightFollowerC, drive.rightMaster, "kDrive_InvertRightC", true));
		addSequential(WPILambdas.runUntil(() -> {
			drive.leftMaster.setInverted(Constants.kDrive_InvertLeftMaster);
			drive.leftFollowerA.setInverted(Constants.kDrive_InvertLeftA);
			drive.leftFollowerB.setInverted(Constants.kDrive_InvertLeftB);
			drive.leftFollowerC.setInverted(Constants.kDrive_InvertLeftC);

			drive.leftFollowerA.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
			drive.leftFollowerB.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
			drive.leftFollowerC.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);

			drive.rightMaster.setInverted(Constants.kDrive_InvertRightMaster);
			drive.rightFollowerA.setInverted(Constants.kDrive_InvertRightA);
			drive.rightFollowerB.setInverted(Constants.kDrive_InvertRightB);
			drive.rightFollowerC.setInverted(Constants.kDrive_InvertRightC);

			drive.rightFollowerA.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
			drive.rightFollowerB.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
			drive.rightFollowerC.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		}, 2.0));
	}
	
	@Override
	protected void interrupted() {
		super.interrupted();
		
		drive.leftMaster.setInverted(Constants.kDrive_InvertLeftMaster);
		drive.leftFollowerA.setInverted(Constants.kDrive_InvertLeftA);
		drive.leftFollowerB.setInverted(Constants.kDrive_InvertLeftB);
		drive.leftFollowerC.setInverted(Constants.kDrive_InvertLeftC);

		drive.leftFollowerA.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
		drive.leftFollowerB.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
		drive.leftFollowerC.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);

		drive.rightMaster.setInverted(Constants.kDrive_InvertRightMaster);
		drive.rightFollowerA.setInverted(Constants.kDrive_InvertRightA);
		drive.rightFollowerB.setInverted(Constants.kDrive_InvertRightB);
		drive.rightFollowerC.setInverted(Constants.kDrive_InvertRightC);

		drive.rightFollowerA.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		drive.rightFollowerB.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		drive.rightFollowerC.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
	}
}
