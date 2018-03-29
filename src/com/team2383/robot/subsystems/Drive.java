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
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Timer;

public class Drive extends Subsystem {
	public final WPI_TalonSRX leftMaster;
	public final BaseMotorController leftFollowerA;
	public final BaseMotorController leftFollowerB;
	public final BaseMotorController leftFollowerC;

	public final WPI_TalonSRX rightMaster;
	public final BaseMotorController rightFollowerA;
	public final BaseMotorController rightFollowerB;
	public final BaseMotorController rightFollowerC;
	
	private final DifferentialDrive drive;
	
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
			double kWheelCircumference = Constants.getWheelCircumference();
			
			leftPosition_Native = leftMaster.getSelectedSensorPosition(0) * kEncoderRatio;
			leftPosition_Rotations = MotionUtils.ticksToRotations(leftPosition_Native, kEncoderTicks, kEncoderRatio);
			leftPosition = MotionUtils.rotationsToDistance(leftPosition_Rotations, kWheelCircumference);
			
			rightPosition_Native = -rightMaster.getSelectedSensorPosition(0) * kEncoderRatio;
			rightPosition_Rotations = MotionUtils.ticksToRotations(rightPosition_Native, kEncoderTicks, kEncoderRatio);
			rightPosition = MotionUtils.rotationsToDistance(rightPosition_Rotations, kWheelCircumference);
			
			leftVelocity_Native = leftMaster.getSelectedSensorVelocity(0) * kEncoderRatio;
			leftVelocity_RPM = MotionUtils.ticksToRPM(leftVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			leftVelocity_RPS = MotionUtils.ticksToRPS(leftVelocity_Native, kEncoderTicks, 0.1, kEncoderRatio);
			leftVelocity = MotionUtils.rotationsToDistance(leftVelocity_RPS, kWheelCircumference);
			
			rightVelocity_Native = -rightMaster.getSelectedSensorVelocity(0) * kEncoderRatio;
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
		leftMaster = new WPI_TalonSRX(Constants.kDrive_LeftMaster_ID);
		if (isPracticeBot) {
			leftFollowerA = new TalonSRX(Constants.kDrive_LeftFollowerA_ID);
			leftFollowerB = new TalonSRX(Constants.kDrive_LeftFollowerB_ID);
			leftFollowerC = new TalonSRX(Constants.kDrive_LeftFollowerC_ID);
			//setup followers
			int lMasterID = leftMaster.getDeviceID();
			leftFollowerA.set(ControlMode.Follower, leftMaster.getDeviceID());
			leftFollowerB.set(ControlMode.Follower, leftMaster.getDeviceID());
			leftFollowerC.set(ControlMode.Follower, leftMaster.getDeviceID());
		} else {
			leftFollowerA = new VictorSPX(Constants.kDrive_LeftFollowerA_ID);
			leftFollowerB = new VictorSPX(Constants.kDrive_LeftFollowerB_ID);
			leftFollowerC = new VictorSPX(Constants.kDrive_LeftFollowerC_ID);
			//setup followers
			leftFollowerA.follow(leftMaster);
			leftFollowerB.follow(leftMaster);
			leftFollowerC.follow(leftMaster);
		}
		
		//Left settings
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		leftMaster.setNeutralMode(NeutralMode.Brake);

		//clear options
		leftMaster.configForwardSoftLimitEnable(false, 10);
		leftMaster.configReverseSoftLimitEnable(false, 10);

		leftMaster.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 10);
		
		/*
		 * Right drive
		 */
		
		//init right talons
		rightMaster = new WPI_TalonSRX(Constants.kDrive_RightMaster_ID);
		if(isPracticeBot) {
			rightFollowerA = new TalonSRX(Constants.kDrive_RightFollowerA_ID);
			rightFollowerB = new TalonSRX(Constants.kDrive_RightFollowerB_ID);
			rightFollowerC = new TalonSRX(Constants.kDrive_RightFollowerC_ID);
			//setup followers
			int rMasterID = rightMaster.getDeviceID();
			rightFollowerA.set(ControlMode.Follower, rMasterID);
			rightFollowerB.set(ControlMode.Follower, rMasterID);
			rightFollowerC.set(ControlMode.Follower, rMasterID);
		} else {
			rightFollowerA = new VictorSPX(Constants.kDrive_RightFollowerA_ID);
			rightFollowerB = new VictorSPX(Constants.kDrive_RightFollowerB_ID);
			rightFollowerC = new VictorSPX(Constants.kDrive_RightFollowerC_ID);
			//setup followers
			rightFollowerA.follow(rightMaster);
			rightFollowerB.follow(rightMaster);
			rightFollowerC.follow(rightMaster);
		}
		
		//Right settings
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightMaster.setNeutralMode(NeutralMode.Brake);
		
		//clear options
		rightMaster.configForwardSoftLimitEnable(false, 10);
		rightMaster.configReverseSoftLimitEnable(false, 10);
		rightMaster.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms, 10);
		
		configMotorControllers(10);
		
		/*
		 * init differential drive
		 */

		drive = new DifferentialDrive(leftMaster, rightMaster);
		drive.setSafetyEnabled(false);
		drive.setMaxOutput(1.0);
		
		setBrake(true);
	}
	
	/**
	 * update motor controller options, in this case current limits and inverts
	 */
	public void configMotorControllers(int timeout) {
		double kWheelCircumference = Constants.getWheelCircumference();

		/*
		 * COMP BOT SETTINGS
		leftMaster.setSensorPhase(false);
		leftMaster.setInverted(false);
		leftFollowerA.setInverted(true);
		leftFollowerB.setInverted(true);
		leftFollowerC.setInverted(false);
		*/

		leftMaster.setSensorPhase(Constants.kDrive_InvertLeftMaster);
		leftMaster.setInverted(Constants.kDrive_InvertLeftMaster);
		leftFollowerA.setInverted(Constants.kDrive_InvertLeftA);
		leftFollowerB.setInverted(Constants.kDrive_InvertLeftB);
		leftFollowerC.setInverted(Constants.kDrive_InvertLeftC);

		leftMaster.configSetParameter(ParamEnum.eContinuousCurrentLimitAmps, Constants.kDrive_ContinuousCurrentLimit, 0x00, 0x00, timeout);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitAmps, Constants.kDrive_PeakCurrentLimit, 0x00, 0x00, timeout);
		leftMaster.configSetParameter(ParamEnum.ePeakCurrentLimitMs, Constants.kDrive_PeakCurrentTime_ms, 0x00, 0x00, timeout);
		leftMaster.enableCurrentLimit(false);
		
		leftMaster.configPeakOutputForward(Constants.kDrive_peakOutput, timeout);
		leftMaster.configPeakOutputReverse(-Constants.kDrive_peakOutput, timeout);
		leftMaster.configOpenloopRamp(0.0, timeout);
		
		/*
		 * COMP BOT SETTINGS
		rightMaster.setSensorPhase(false);
		rightMaster.setInverted(true);
		rightFollowerA.setInverted(false);
		rightFollowerB.setInverted(true);
		rightFollowerC.setInverted(false);
		*/
		
		rightMaster.setSensorPhase(Constants.kDrive_InvertRightMaster);
		rightMaster.setInverted(Constants.kDrive_InvertRightMaster);
		rightFollowerA.setInverted(Constants.kDrive_InvertRightA);
		rightFollowerB.setInverted(Constants.kDrive_InvertRightB);
		rightFollowerC.setInverted(Constants.kDrive_InvertRightC);
		
		rightMaster.configSetParameter(ParamEnum.eContinuousCurrentLimitAmps, Constants.kDrive_ContinuousCurrentLimit, 0x00, 0x00, timeout);
		rightMaster.configSetParameter(ParamEnum.ePeakCurrentLimitAmps, Constants.kDrive_PeakCurrentLimit, 0x00, 0x00, timeout);
		rightMaster.configSetParameter(ParamEnum.ePeakCurrentLimitMs, Constants.kDrive_PeakCurrentTime_ms, 0x00, 0x00, timeout);
		rightMaster.enableCurrentLimit(false);
		
		rightMaster.configPeakOutputForward(Constants.kDrive_peakOutput, timeout);
		rightMaster.configPeakOutputReverse(-Constants.kDrive_peakOutput, timeout);
		rightMaster.configOpenloopRamp(0.0, timeout);
		
		//PID
		rightMaster.config_kP(0, (Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		rightMaster.config_kI(0, Constants.kDrive_Motion_talonI, 10);
		rightMaster.config_kD(0, (Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		rightMaster.config_kF(0, (Constants.kDrive_Motion_V * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)),  10);
		rightMaster.config_IntegralZone(0, 50, 10);

		leftMaster.config_kP(0, (Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		leftMaster.config_kI(0, Constants.kDrive_Motion_talonI, 10);
		leftMaster.config_kD(0, (Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		leftMaster.config_kF(0, (Constants.kDrive_Motion_V * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)),  10);
		leftMaster.config_IntegralZone(0, 50, 10);
		
		/*
		 * ft/s -> ticks per 100ms
		 * so ft/s * 1 rotation / circumference ft * 4096 / 1 rotation / 10
		 */
		int nativeVelocity = (int) (Constants.kDrive_Motion_Velocity * 1.0/Constants.getWheelCircumference() * 4096.0 / 10.0);

		/*
		 * ft/s/s -> ticks per 100ms per s
		 * so ft/s/s * 1 rotation / circumference ft * 4096 / 1 rotation / 10
		 */
		int nativeAcceleration = (int) (Constants.kDrive_Motion_Acceleration * 1.0/Constants.getWheelCircumference() * 4096.0 / 10.0);
		leftMaster.configMotionCruiseVelocity(nativeVelocity, timeout);
		leftMaster.configMotionAcceleration(nativeAcceleration, timeout);
		rightMaster.configMotionCruiseVelocity(nativeVelocity, timeout);
		rightMaster.configMotionAcceleration(nativeAcceleration, timeout);
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
		leftFollowerA.setNeutralMode(mode);
		leftFollowerB.setNeutralMode(mode);
		leftFollowerC.setNeutralMode(mode);
		rightMaster.setNeutralMode(mode);
		rightFollowerA.setNeutralMode(mode);
		rightFollowerB.setNeutralMode(mode);
		rightFollowerC.setNeutralMode(mode);
	}

	public void resetEncoders() {
		leftMaster.setSelectedSensorPosition(0, 0, 0);
		rightMaster.setSelectedSensorPosition(0, 0, 0);
	}
	
	public void tank(double leftValue, double rightValue) {
		drive.tankDrive(leftValue, rightValue, false);
	}

	public void arcade(double driveSpeed, double turnSpeed) {
		drive.arcadeDrive(driveSpeed, turnSpeed);
	}
	
	public void curvature(double driveSpeed, double turnSpeed, boolean isQuickTurn) {
		drive.curvatureDrive(driveSpeed, turnSpeed, isQuickTurn);
	}
	
	public void curvatureAutoQT(double driveSpeed, double turnSpeed) {
		boolean isQuickTurn = Math.abs(driveSpeed) < 0.1;
		drive.curvatureDrive(driveSpeed, turnSpeed, isQuickTurn);
	}
	
	@Override
	protected void initDefaultCommand() {
		this.setDefaultCommand(new TeleopDrive(OI.throttle, OI.turn));
	}
	
	public void position(double leftPos, double rightPos) {
		leftMaster.set(ControlMode.MotionMagic, MotionUtils.distanceToRotations(leftPos, Constants.getWheelCircumference()) * 4096);
		rightMaster.set(ControlMode.MotionMagic, MotionUtils.distanceToRotations(-rightPos, Constants.getWheelCircumference()) * 4096);
	}
}
