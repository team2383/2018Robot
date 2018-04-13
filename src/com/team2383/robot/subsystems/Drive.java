 package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.navX;

import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.Values;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.commands.TeleopDrive;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
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
		leftMaster.enableVoltageCompensation(true);
		leftMaster.configVoltageCompSaturation(12, 10);

		//clear options
		leftMaster.configForwardSoftLimitEnable(false, 10);
		leftMaster.configReverseSoftLimitEnable(false, 10);
		
		leftMaster.setControlFramePeriod(ControlFrame.Control_3_General, 5);
		leftMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 10);

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
		rightMaster.enableVoltageCompensation(true);
		rightMaster.configVoltageCompSaturation(12, 10);
		
		//clear options
		rightMaster.configForwardSoftLimitEnable(false, 10);
		rightMaster.configReverseSoftLimitEnable(false, 10);
		
		rightMaster.setControlFramePeriod(ControlFrame.Control_3_General, 5);
		rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 5, 10);
		
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
		
		//IF F***ED AT COMP Remove !
		rightMaster.setSensorPhase(!Constants.kDrive_InvertRightMaster);
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
		rightMaster.config_kP(0, -(Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		rightMaster.config_kI(0, -Constants.kDrive_Motion_talonI, 10);
		rightMaster.config_kD(0, -(Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		rightMaster.config_kF(0, -(Constants.kDrive_Motion_V * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)),  10);
		rightMaster.config_IntegralZone(0, 50, 10);

		rightMaster.config_kP(1, -(Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		rightMaster.config_kI(1, -Constants.kDrive_Motion_talonI, 10);
		rightMaster.config_kD(1, -(Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		rightMaster.config_kF(1, 0,  10);
		rightMaster.config_IntegralZone(1, 50, 10);

		leftMaster.config_kP(0, (Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		leftMaster.config_kI(0, Constants.kDrive_Motion_talonI, 10);
		leftMaster.config_kD(0, (Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		leftMaster.config_kF(0, (Constants.kDrive_Motion_V * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)),  10);
		leftMaster.config_IntegralZone(0, 50, 10);
		
		leftMaster.config_kP(1, (Constants.kDrive_Motion_talonP * (1023.0/1.0) * (1.0/(kWheelCircumference)) * (1.0/4096.0)), 10);
		leftMaster.config_kI(1, Constants.kDrive_Motion_talonI, 10);
		leftMaster.config_kD(1, (Constants.kDrive_Motion_talonD * (1023.0/1.0) * (1.0/(1.0/kWheelCircumference)) * (1.0/4096.0) * (10.0)), 10);
		leftMaster.config_kF(1, 0,  10);
		leftMaster.config_IntegralZone(1, 50, 10);
		
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

	/**
	 * Updates the smart dashboard values
	 * calculate acceleration and send to smartdashboard
	 */
	@Override
	public void periodic() {
		SmartDashboard.putNumber("Drive Left Position", getLeftPosition());
		SmartDashboard.putNumber("Drive Right Position", getRightPosition());
		SmartDashboard.putNumber("Drive Left Velocity", getLeftVelocity());
		SmartDashboard.putNumber("Drive Right Velocity", getRightVelocity());
		SmartDashboard.putNumber("Drive Left kV", leftMaster.getMotorOutputPercent()/getLeftVelocity());
		SmartDashboard.putNumber("Drive Right kV", rightMaster.getMotorOutputPercent()/getRightVelocity());
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
	
	public double inches(int ticks) {
		return MotionUtils.rotationsToDistance(MotionUtils.ticksToRotations(ticks, 4096, 1), Constants.getWheelCircumference());
	}
	
	public double getLeftPosition() {
		return MotionUtils.rotationsToDistance(MotionUtils.ticksToRotations(leftMaster.getSelectedSensorPosition(0), 4096, 1), Constants.getWheelCircumference());
	}
	
	public double getRightPosition() {
		return MotionUtils.rotationsToDistance(MotionUtils.ticksToRotations(rightMaster.getSelectedSensorPosition(0), 4096, 1), Constants.getWheelCircumference());
	}
	
	public void position(double leftPos, double rightPos) {
		leftMaster.selectProfileSlot(0, 0);
		rightMaster.selectProfileSlot(0, 0);

		leftMaster.set(ControlMode.MotionMagic, MotionUtils.distanceToRotations(leftPos, Constants.getWheelCircumference()) * 4096);
		rightMaster.set(ControlMode.MotionMagic, MotionUtils.distanceToRotations(rightPos, Constants.getWheelCircumference()) * 4096);
	}
	
	public void positionPDauxF(double leftPos, double leftFF, double rightPos, double rightFF) {
		leftMaster.selectProfileSlot(1, 0);
		rightMaster.selectProfileSlot(1, 0);

		leftMaster.set(ControlMode.Position, MotionUtils.distanceToRotations(leftPos, Constants.getWheelCircumference()) * 4096, DemandType.ArbitraryFeedForward, leftFF);
		rightMaster.set(ControlMode.Position, MotionUtils.distanceToRotations(rightPos, Constants.getWheelCircumference()) * 4096, DemandType.ArbitraryFeedForward, -rightFF);
	}

	public double getLeftVelocity() {
		return leftMaster.getSelectedSensorVelocity(0) / 4096.0 * 10.0 * Constants.getWheelCircumference();
	}
	
	public double getRightVelocity() {
		return rightMaster.getSelectedSensorVelocity(0) / 4096.0 * 10.0 * Constants.getWheelCircumference();
	}
	
	public boolean atTarget() {
		return Math.abs(inches(leftMaster.getClosedLoopError(0))) < Constants.kDrive_Motion_Tolerance && Math.abs(inches(leftMaster.getClosedLoopError(0))) < Constants.kDrive_Motion_Tolerance;
	}
}
