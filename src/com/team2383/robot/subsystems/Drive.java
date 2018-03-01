 package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.navX;

import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.Values;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.commands.TeleopDrive;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Sendable;

public class Drive extends Subsystem {
	public final WPI_TalonSRX leftMaster;
	public final BaseMotorController leftFollowerA;
	public final BaseMotorController leftFollowerB;
	public final BaseMotorController leftFollowerC;

	public final WPI_TalonSRX rightMaster;
	public final BaseMotorController rightFollowerA;
	public final BaseMotorController rightFollowerB;
	public final BaseMotorController rightFollowerC;
	
	//private final PowerDistributionPanel pdp;
	
	private final DifferentialDrive drive;
	
	//cheesyDrive vars
	double oldWheel, quickStopAccumulator;
	private final double throttleDeadband = 0.02;
	private final double wheelDeadband = 0.02;
	private final double turnSensitivity = 0.65;
	
	/**
	 * The Motion class holds info about the current motion state of the drivetrain, like the
	 * position, velocity, and heading.
	 * 
	 * class members are named of the form
	 * 		sideType_Units
	 *	
	 *	Units can be omitted for the commonly used units, eg feet, degrees, etc that we use most often
	 *	ticks and rotations are kept available as some WPILib and Phoenix functions want them to be called 
	 *
	 *	Native means the native units of the input sensor, which for the usual drive encoders we use (SRX Mag encoder)
	 *  is in encoder ticks, of which there are 4096 per rotation
	 *  
	 *  The constructor for the function takes in native units, and fills out the rest of the units using the calculations necessary.
	 */
	public class Motion {
		public final double leftPosition_Native;
		public final double leftPosition_Rotations;
		public final double leftPosition;				// ft
		
		public final double rightPosition_Native;
		public final double rightPosition_Rotations;
		public final double rightPosition;				// ft
		
		public final double leftVelocity_Native;
		public final double leftVelocity_RPM;
		public final double leftVelocity_RPS;
		public final double leftVelocity;				// ft/s
		
		public final double rightVelocity_Native;
		public final double rightVelocity_RPM;
		public final double rightVelocity_RPS;
		public final double rightVelocity;				// ft/s
		
		public final double position_Native;
		public final double position_Rotations;
		public final double position;					// ft
		
		public final double velocity_Native;
		public final double velocity_RPM;
		public final double velocity_RPS;
		public final double velocity;					// ft/s
		
		public final double heading;
		public final double accumHeading;
		
		public Motion() {
			double kEncoderRatio = Constants.kDrive_EncoderRatio;
			double kEncoderTicks = Constants.kDrive_EncoderTicks;
			double kWheelCircumference = Constants.kDrive_WheelDiameterInch/12.0 * Math.PI;
			
			leftPosition_Native = leftMaster.getSelectedSensorPosition(0) * kEncoderRatio;
			leftPosition_Rotations = MotionUtils.ticksToRotations(leftPosition_Native, kEncoderTicks, kEncoderRatio);
			leftPosition = MotionUtils.rotationsToDistance(leftPosition_Rotations, kWheelCircumference);
			
			rightPosition_Native = rightMaster.getSelectedSensorPosition(0) * kEncoderRatio;
			rightPosition_Rotations = MotionUtils.ticksToRotations(rightPosition_Native, kEncoderTicks, kEncoderRatio);
			rightPosition = MotionUtils.rotationsToDistance(rightPosition_Rotations, kWheelCircumference);
			
			leftVelocity_Native = leftMaster.getSelectedSensorVelocity(0) * kEncoderRatio;
			leftVelocity_RPM = MotionUtils.ticksToRPM(leftVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			leftVelocity_RPS = MotionUtils.ticksToRPS(leftVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			leftVelocity = MotionUtils.rotationsToDistance(leftVelocity_RPS, kWheelCircumference);
			
			rightVelocity_Native = rightMaster.getSelectedSensorVelocity(0) * kEncoderRatio;
			rightVelocity_RPM = MotionUtils.ticksToRPM(rightVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			rightVelocity_RPS = MotionUtils.ticksToRPS(rightVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			rightVelocity = MotionUtils.rotationsToDistance(rightVelocity_RPS, kWheelCircumference);
			
			position_Native = (leftPosition_Native + rightPosition_Native) / 2.0;
			position_Rotations = (leftPosition_Rotations + rightPosition_Rotations) / 2.0;
			position = (leftPosition + rightPosition) / 2.0;
			
			velocity_Native = (leftVelocity_Native + rightVelocity_Native) / 2.0;
			velocity_RPM = (leftVelocity_RPM + rightVelocity_RPM) / 2.0;
			velocity_RPS = (leftVelocity_RPS + rightVelocity_RPS) / 2.0;
			velocity = (leftVelocity + rightVelocity) / 2.0;

			heading = navX.getYaw();
			accumHeading = navX.getAngle();

			SmartDashboard.putNumber("Drive Bounded Heading (degrees)", accumHeading);
			
			SmartDashboard.putNumber("Drive Left Position (ft)", leftPosition);
			SmartDashboard.putNumber("Drive Left Velocity (ft-s)", leftVelocity);
			
			SmartDashboard.putNumber("Drive Right Position (ft)", rightPosition);
			SmartDashboard.putNumber("Drive Right Velocity (ft-s)", rightVelocity);
			
			SmartDashboard.putNumber("Drive Avg Position (rotations)", position_Rotations);
			SmartDashboard.putNumber("Drive Avg Position (ft)", position);
			SmartDashboard.putNumber("Drive Avg Velocity (ft-s)", velocity);
		}
	}

	public Drive(boolean isPracticeBot) {
		super("Drivetrain");
		
		/*
		 * Left drive
		 */

		//init left talons
		leftMaster = new WPI_TalonSRX(Constants.kDrive_LeftMasterTalonID);
		if (isPracticeBot) {
			leftFollowerA = new TalonSRX(Constants.kDrive_LeftFollowerATalonID);
			leftFollowerB = new TalonSRX(Constants.kDrive_LeftFollowerBTalonID);
			leftFollowerC = new TalonSRX(Constants.kDrive_LeftFollowerCTalonID);
		} else {
			leftFollowerA = new VictorSPX(Constants.kDrive_LeftFollowerATalonID);
			leftFollowerB = new VictorSPX(Constants.kDrive_LeftFollowerBTalonID);
			leftFollowerC = new VictorSPX(Constants.kDrive_LeftFollowerCTalonID);
		}

		//setup followers
		leftFollowerA.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
		leftFollowerB.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
		leftFollowerC.set(ControlMode.Follower, Constants.kDrive_LeftMasterTalonID);
		
		//Left settings
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		leftMaster.setSensorPhase(false);
		leftMaster.setInverted(Constants.kDrive_InvertLeftMaster);
		leftFollowerA.setInverted(Constants.kDrive_InvertLeftA);
		leftFollowerB.setInverted(Constants.kDrive_InvertLeftB);
		leftFollowerC.setInverted(Constants.kDrive_InvertLeftC);
		
		leftMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		leftMaster.config_kP(0, 0, 10);
		leftMaster.config_kI(0, 0, 10);
		leftMaster.config_kD(0, 0, 10);
		leftMaster.config_kF(0, 0, 10);
		leftMaster.config_IntegralZone(0, 0, 10);

		//clear options
		leftMaster.configForwardSoftLimitEnable(false, 10);
		leftMaster.configReverseSoftLimitEnable(false, 10);

		leftMaster.configSetParameter(ParamEnum.eContinuousCurrentLimitAmps, Constants.kDrive_continuousCurrentLimit, 0x00, 0x00, 10);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitAmps, Constants.kDrive_peakCurrentLimit, 0x00, 0x00, 10);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitMs, Constants.kDrive_peakCurrentTime_ms, 0x00, 0x00, 10);
		leftMaster.enableCurrentLimit(true);
		
		leftMaster.configPeakOutputForward(0.8, 10);
		leftMaster.configPeakOutputReverse(-0.8, 10);

		leftMaster.configOpenloopRamp(0, 10);

		leftMaster.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 10);
		
		/*
		 * Right drive
		 */
		
		//init right talons
		rightMaster = new WPI_TalonSRX(Constants.kDrive_RightMasterTalonID);
		if(isPracticeBot) {
			rightFollowerA = new TalonSRX(Constants.kDrive_RightFollowerATalonID);
			rightFollowerB = new TalonSRX(Constants.kDrive_RightFollowerBTalonID);
			rightFollowerC = new TalonSRX(Constants.kDrive_RightFollowerCTalonID);
		} else {
			rightFollowerA = new VictorSPX(Constants.kDrive_RightFollowerATalonID);
			rightFollowerB = new VictorSPX(Constants.kDrive_RightFollowerBTalonID);
			rightFollowerC = new VictorSPX(Constants.kDrive_RightFollowerCTalonID);
		}
		
		//setup followers
		rightFollowerA.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		rightFollowerB.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		rightFollowerC.set(ControlMode.Follower, Constants.kDrive_RightMasterTalonID);
		
		//Right settings
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightMaster.setSensorPhase(false);
		rightMaster.setInverted(Constants.kDrive_InvertRightMaster);
		rightFollowerA.setInverted(Constants.kDrive_InvertRightA);
		rightFollowerB.setInverted(Constants.kDrive_InvertRightB);
		rightFollowerC.setInverted(Constants.kDrive_InvertRightC);

		rightMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		rightMaster.config_kP(0, 0, 10);
		rightMaster.config_kI(0, 0, 10);
		rightMaster.config_kD(0, 0, 10);
		rightMaster.config_kF(0, 0, 10);
		rightMaster.config_IntegralZone(0, 0, 10);
		
		//clear options
		rightMaster.configForwardSoftLimitEnable(false, 10);
		rightMaster.configReverseSoftLimitEnable(false, 10);
		
		leftMaster.configSetParameter(ParamEnum.eContinuousCurrentLimitAmps, Constants.kDrive_continuousCurrentLimit, 0x00, 0x00, 10);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitAmps, Constants.kDrive_peakCurrentLimit, 0x00, 0x00, 10);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitMs, Constants.kDrive_peakCurrentTime_ms, 0x00, 0x00, 10);
		rightMaster.enableCurrentLimit(false);
		
		rightMaster.configPeakOutputForward(0.8, 10);
		rightMaster.configPeakOutputReverse(-0.8, 10);

		rightMaster.configOpenloopRamp(0, 10);
		
		rightMaster.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 10);
		
		/*
		 * init differential drive
		 */

		drive = new DifferentialDrive(leftMaster, rightMaster);
		drive.setSafetyEnabled(false);
		
		setBrake(true);
	}
	
	public Motion getMotion() {
		return new Motion();
	}

	/**
	 * Updates the smart dashboard values
	 * calculate acceleration and send to smartdashboard
	 */
	@Override
	public void periodic() {
		new Motion();
	}
	
	public void setBrake(boolean brake) {
		NeutralMode mode = brake ? NeutralMode.Brake : NeutralMode.Coast;
		leftMaster.setNeutralMode(mode);
		rightMaster.setNeutralMode(mode);
	}

	public void resetEncoders() {
		leftMaster.setSelectedSensorPosition(0, 0, 0);
		rightMaster.setSelectedSensorPosition(0, 0, 0);
	}
	
	public void tank(double leftValue, double rightValue) {
		drive.tankDrive(leftValue, rightValue);
	}

	public void arcade(double driveSpeed, double turnSpeed) {
		drive.arcadeDrive(driveSpeed, turnSpeed);
	}
	
	public void curvature(double driveSpeed, double turnSpeed) {
		boolean isQuickTurn = Math.abs(driveSpeed) < 0.1;
		drive.curvatureDrive(driveSpeed, turnSpeed, isQuickTurn);
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
			negInertiaScalar = 2.5;
		} else {
			negInertiaScalar = 1.5;
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
	
	@Override
	protected void initDefaultCommand() {
		this.setDefaultCommand(new TeleopDrive(OI.throttle, OI.turn));
	}
}
