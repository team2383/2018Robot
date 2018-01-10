package com.team2383.robot.subsystems;

import com.team2383.ninjaLib.Values;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.commands.TeleopDrive;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivetrain extends Subsystem {
	private final WPI_TalonSRX leftMaster;
	private final TalonSRX leftFollowerA;
	private final TalonSRX leftFollowerB;
	private final TalonSRX leftFollowerC;

	private final WPI_TalonSRX rightMaster;
	private final TalonSRX rightFollowerA;
	private final TalonSRX rightFollowerB;
	private final TalonSRX rightFollowerC;
	
	private final DifferentialDrive drive;
	
	//cheesyDrive vars
	double oldWheel, quickStopAccumulator;
	private final double throttleDeadband = 0.02;
	private final double wheelDeadband = 0.02;
	private final double turnSensitivity = 0.85;

	public Drivetrain() {
		super("Drivetrain");
		
		/*
		 * Left drive
		 */

		//init left talons
		leftMaster = new WPI_TalonSRX(Constants.kLeftMasterTalonID);
		leftFollowerA = new TalonSRX(Constants.kLeftFollowerATalonID);
		leftFollowerB = new TalonSRX(Constants.kLeftFollowerBTalonID);
		leftFollowerC = new TalonSRX(Constants.kLeftFollowerCTalonID);

		//setup followers
		int leftMasterID = leftMaster.getDeviceID();
		leftFollowerA.set(ControlMode.Follower, leftMasterID);
		leftFollowerB.set(ControlMode.Follower, leftMasterID);
		leftFollowerC.set(ControlMode.Follower, leftMasterID);
		
		//Left settings
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		leftMaster.setSensorPhase(true);
		leftMaster.setInverted(true);
		leftMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		leftMaster.config_kP(0, Constants.kDriveVelocityP, 0);
		leftMaster.config_kI(0, Constants.kDriveVelocityI, 0);
		leftMaster.config_kD(0, Constants.kDriveVelocityD, 0);
		leftMaster.config_kF(0, Constants.kDriveVelocityF, 0);
		leftMaster.config_IntegralZone(0, Constants.kDriveVelocityIZone, 0);

		/*
		 * Right drive
		 */
		
		//init right talons
		rightMaster = new WPI_TalonSRX(Constants.kRightMasterTalonID);
		rightFollowerA = new TalonSRX(Constants.kRightFollowerATalonID);
		rightFollowerB = new TalonSRX(Constants.kRightFollowerBTalonID);
		rightFollowerC = new TalonSRX(Constants.kRightFollowerCTalonID);
		
		//setup followers
		int rightMasterID = rightMaster.getDeviceID();
		rightFollowerA.set(ControlMode.Follower, rightMasterID);
		rightFollowerB.set(ControlMode.Follower, rightMasterID);
		rightFollowerC.set(ControlMode.Follower, rightMasterID);
		
		//Right settings
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightMaster.setSensorPhase(true);
		rightMaster.setInverted(true);
		rightMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		rightMaster.config_kP(0, Constants.kDriveVelocityP, 0);
		rightMaster.config_kI(0, Constants.kDriveVelocityI, 0);
		rightMaster.config_kD(0, Constants.kDriveVelocityD, 0);
		rightMaster.config_kF(0, Constants.kDriveVelocityF, 0);
		rightMaster.config_IntegralZone(0, Constants.kDriveVelocityIZone, 0);
		
		/*
		 * init differential drive
		 */
		drive = new DifferentialDrive(leftMaster, rightMaster);
	}

	public void tank(double leftValue, double rightValue) {
		drive.tankDrive(leftValue, rightValue);
	}

	public void arcade(double driveSpeed, double turnSpeed) {
		drive.arcadeDrive(driveSpeed, turnSpeed);
	}
	
	public void curvature(double driveSpeed, double turnSpeed, boolean quickTurn) {
		drive.curvatureDrive(driveSpeed, turnSpeed, quickTurn);
	}
	
	public void cheesyDrive(double throttle, double wheel) {
		double wheelNonLinearity;

		boolean isQuickTurn = Math.abs(throttle) < 0.1;
		wheel = handleDeadband(wheel, wheelDeadband);
		throttle = handleDeadband(throttle, throttleDeadband);

		double negInertia = wheel - oldWheel;
		oldWheel = wheel;
		
		wheelNonLinearity = 0.6;
		// Apply a sin function that's scaled to make it feel better.
		wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
		wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);

		double leftSpeed, rightSpeed, overPower;
		double sensitivity;

		double angularPower;
		double linearPower;

		// Negative inertia!
		double negInertiaAccumulator = 0.0;
		double negInertiaScalar;
		if (Math.abs(wheel) > 0.65) {
			negInertiaScalar = 6.5;
		} else {
			negInertiaScalar = 4.5;
		}
		sensitivity = turnSensitivity;
		double negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccumulator += negInertiaPower;

		wheel = wheel + negInertiaAccumulator;
		if (negInertiaAccumulator > 1) {
			negInertiaAccumulator -= 1;
		} else if (negInertiaAccumulator < -1) {
			negInertiaAccumulator += 1;
		} else {
			negInertiaAccumulator = 0;
		}
		linearPower = throttle;

		// Quickturn!
		if (isQuickTurn) {
			if (Math.abs(linearPower) < 0.2) {
				double alpha = 0.1;
				quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha * Values.limit(-1.0, wheel, 1.0) * 5;
			}
			overPower = 1.0;
			sensitivity = 1.0;
			angularPower = wheel;
		} else {
			overPower = 0.0;
			angularPower = Math.abs(throttle) * wheel * sensitivity - quickStopAccumulator;
			if (quickStopAccumulator > 1) {
				quickStopAccumulator -= 1;
			} else if (quickStopAccumulator < -1) {
				quickStopAccumulator += 1;
			} else {
				quickStopAccumulator = 0.0;
			}
		}

		rightSpeed = leftSpeed = linearPower;
		leftSpeed += angularPower;
		rightSpeed -= angularPower;

		if (leftSpeed > 1.0) {
			rightSpeed -= overPower * (leftSpeed - 1.0);
			leftSpeed = 1.0;
		} else if (rightSpeed > 1.0) {
			leftSpeed -= overPower * (rightSpeed - 1.0);
			rightSpeed = 1.0;
		} else if (leftSpeed < -1.0) {
			rightSpeed += overPower * (-1.0 - leftSpeed);
			leftSpeed = -1.0;
		} else if (rightSpeed < -1.0) {
			leftSpeed += overPower * (-1.0 - rightSpeed);
			rightSpeed = -1.0;
		}
		tank(leftSpeed, rightSpeed);
	}

	private double handleDeadband(double val, double deadband) {
		return Math.abs(val) > Math.abs(deadband) ? val : 0.0;
	}


	public void resetEncoders() {
		leftMaster.setSelectedSensorPosition(0, 0, 0);
		rightMaster.setSelectedSensorPosition(0, 0, 0);
	}



	public double getRotations() {
		double rotations;
		try {
			rotations = (leftMaster.getSelectedSensorPosition(0) + rightMaster.getSelectedSensorPosition(0)) / 2.0;
		} catch (Throwable e) {
			System.out.println("Failed to get encoder rotations of drivetrain");
			rotations = 0;
		}
		return rotations * Constants.kDriveEncoderScalar;
	}

	public double getVelocity() {
		double rotations;
		try {
			rotations = (leftMaster.getSelectedSensorVelocity(0) + rightMaster.getSelectedSensorVelocity(0))/2.0;
		} catch (Throwable e) {
			System.out.println("Failed to get encoder speed of drivetrain");
			rotations = 0;
		}
		return rotations * Constants.kDriveEncoderScalar;
	}
	
	
	public double getError(){
		double error;
		try {
			error = (rightMaster.getClosedLoopError(0) + leftMaster.getClosedLoopError(0)) / 2;
		} catch (Throwable e) {
			System.out.println("Failed to get error of drivetrain");
			error = 0;
		}
		return error;
	}

	public double getInches() {
		return getRotations() * Constants.kDriveWheelCircumference;
	}

	// Feet per Seconds
	public double getSpeed() {
		return getVelocity() * Constants.kDriveWheelCircumference / 12.0 / 60.0;
	}

	@Override
	protected void initDefaultCommand() {
		this.setDefaultCommand(new TeleopDrive(OI.throttle, OI.turn));
	}

	public void setBrake(boolean brake) {
		NeutralMode mode = brake ? NeutralMode.Brake : NeutralMode.Coast;
		leftMaster.setNeutralMode(mode);
		rightMaster.setNeutralMode(mode);
	}

	public void enableBrake() {
		setBrake(true);
	}

	public void disableBrake() {
		setBrake(false);
	}
}
