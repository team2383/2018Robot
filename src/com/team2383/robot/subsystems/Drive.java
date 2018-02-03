package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.navX;

import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.Values;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.commands.TeleopDrive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;

import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class Drive extends Subsystem {
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
	
	private double lastTime;
	private double lastLeftVelocity;
	private double lastRightVelocity;
	private double lastVelocity;
	private double current_leftAcceleration;
	private double current_rightAcceleration;
	private double current_acceleration;
	
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
	public class Motion implements Sendable {
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
		
		public Motion() {
			leftPosition_Native = leftMaster.getSelectedSensorPosition(0) * Constants.kDrive_EncoderRatio;
			leftPosition_Rotations = MotionUtils.ticksToRotations(leftPosition_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			leftPosition = MotionUtils.rotationsToDistance(leftPosition_Rotations, Constants.kDrive_WheelCircumferenceInch/12.0);
			
			rightPosition_Native = rightMaster.getSelectedSensorPosition(0) * Constants.kDrive_EncoderRatio;
			rightPosition_Rotations = MotionUtils.ticksToRotations(rightPosition_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			rightPosition = MotionUtils.rotationsToDistance(rightPosition_Rotations, Constants.kDrive_WheelCircumferenceInch/12.0);
			
			leftVelocity_Native = leftMaster.getSelectedSensorVelocity(0) * Constants.kDrive_EncoderRatio;
			leftVelocity_RPM = MotionUtils.ticksToRPM(leftVelocity_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			leftVelocity_RPS = MotionUtils.ticksToRPS(leftVelocity_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			leftVelocity = MotionUtils.rotationsToDistance(leftVelocity_RPS, Constants.kDrive_WheelCircumferenceInch/12.0);
			
			rightVelocity_Native = rightMaster.getSelectedSensorVelocity(0) * Constants.kDrive_EncoderRatio;
			rightVelocity_RPM = MotionUtils.ticksToRPM(rightVelocity_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			rightVelocity_RPS = MotionUtils.ticksToRPS(rightVelocity_Native, Constants.kDrive_EncoderTicks, Constants.kDrive_EncoderRatio);
			rightVelocity = MotionUtils.rotationsToDistance(rightVelocity_RPS, Constants.kDrive_WheelCircumferenceInch/12.0);
			
			position_Native = (leftPosition_Native + rightPosition_Native) / 2.0;
			position_Rotations = (leftPosition_Rotations + rightPosition_Rotations) / 2.0;
			position = (leftPosition + rightPosition) / 2.0;
			
			velocity_Native = (leftVelocity_Native + rightVelocity_Native) / 2.0;
			velocity_RPM = (leftVelocity_RPM + rightVelocity_RPM) / 2.0;
			velocity_RPS = (leftVelocity_RPS + rightVelocity_RPS) / 2.0;
			velocity = (leftVelocity + rightVelocity) / 2.0;

			heading = navX.getYaw();	
		}

		@Override
		public String getName() {
			return "Drive Motion";
		}

		@Override
		public void setName(String name) {
			return;
		}

		@Override
		public String getSubsystem() {
			return "Drive";
		}

		@Override
		public void setSubsystem(String subsystem) {
			return;
		}

		@Override
		public void initSendable(SendableBuilder builder) {
			builder.setSmartDashboardType("DriveMotion");
			builder.addDoubleProperty("Left Position (ft)", () -> leftPosition, (unused) -> {});
			builder.addDoubleProperty("Left Velocity (ft/s)", () -> leftVelocity, (unused) -> {});
			builder.addDoubleProperty("Right Position (ft)", () -> rightPosition, (unused) -> {});
			builder.addDoubleProperty("Right Velocity (ft/s)", () -> rightVelocity, (unused) -> {});
			builder.addDoubleProperty("Avg Position (ft)", () -> velocity, (unused) -> {});
			builder.addDoubleProperty("Avg Velocity (ft/s)", () -> position, (unused) -> {});
		}
	}

	public Drive() {
		super("Drivetrain");
		
		/*
		 * Left drive
		 */

		//init left talons
		leftMaster = new WPI_TalonSRX(Constants.kDrive_LeftMasterTalonID);
		leftFollowerA = new TalonSRX(Constants.kDrive_LeftFollowerATalonID);
		leftFollowerB = new TalonSRX(Constants.kDrive_LeftFollowerBTalonID);
		leftFollowerC = new TalonSRX(Constants.kDrive_LeftFollowerCTalonID);

		//setup followers
		int leftMasterID = leftMaster.getDeviceID();
		leftFollowerA.set(ControlMode.Follower, leftMasterID);
		leftFollowerB.set(ControlMode.Follower, leftMasterID);
		leftFollowerC.set(ControlMode.Follower, leftMasterID);
		
		//Left settings
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		leftMaster.setSensorPhase(true);
		leftMaster.setInverted(false);
		leftFollowerA.setInverted(false);
		leftFollowerB.setInverted(false);
		leftFollowerC.setInverted(false);
		
		leftMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		leftMaster.config_kP(0, 0, 0);
		leftMaster.config_kI(0, 0, 0);
		leftMaster.config_kD(0, 0, 0);
		leftMaster.config_kF(0, 0, 0);
		leftMaster.config_IntegralZone(0, 0, 0);

		/*
		 * Right drive
		 */
		
		//init right talons
		rightMaster = new WPI_TalonSRX(Constants.kDrive_RightMasterTalonID);
		rightFollowerA = new TalonSRX(Constants.kDrive_RightFollowerATalonID);
		rightFollowerB = new TalonSRX(Constants.kDrive_RightFollowerBTalonID);
		rightFollowerC = new TalonSRX(Constants.kDrive_RightFollowerCTalonID);
		
		//setup followers
		int rightMasterID = rightMaster.getDeviceID();
		rightFollowerA.set(ControlMode.Follower, rightMasterID);
		rightFollowerB.set(ControlMode.Follower, rightMasterID);
		rightFollowerC.set(ControlMode.Follower, rightMasterID);
		
		//Right settings
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightMaster.setSensorPhase(false);
		rightMaster.setInverted(false);
		rightFollowerA.setInverted(false);
		rightFollowerB.setInverted(false);
		rightFollowerC.setInverted(false);
		
		rightMaster.setNeutralMode(NeutralMode.Brake);
		
		//PID
		rightMaster.config_kP(0, 0, 0);
		rightMaster.config_kI(0, 0, 0);
		rightMaster.config_kD(0, 0, 0);
		rightMaster.config_kF(0, 0, 0);
		rightMaster.config_IntegralZone(0, 0, 0);
		
		/*
		 * init differential drive
		 */
		drive = new DifferentialDrive(leftMaster, rightMaster);
		
		lastTime = 0.0;
		lastVelocity = 0.0;
		current_acceleration = 0.0;
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
		SmartDashboard.putData(getMotion());
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
	
	@Override
	protected void initDefaultCommand() {
		this.setDefaultCommand(new TeleopDrive(OI.throttle, OI.turn));
	}
}
