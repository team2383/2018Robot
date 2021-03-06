package com.team2383.robot.subsystems;

import java.util.function.DoubleUnaryOperator;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.Values;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Wrist extends Subsystem {
		/*
		 * Gearbox
		 * BAG
		 * 9:1
		 * 4:1
		 * ENCODER
		 * 18:72
		 * 
		 * 1encoder rot/4096ticks * 18 wrist rots / 72 encoder rots * 3600 ticks / 4096 ticks
		 * 
		 * max speed deg/s at wrist = 494.25 deg/s
		 * max speed deg/s at encoder = 1977 deg/s
		 * max speed deg/s at motor = 74,000 deg/s
		 */

		private static final double MAX_WRIST_TRAVEL_DEGREES = 255;
		private static final double MAX_WRIST_TRAVEL_TICKS = ticks(MAX_WRIST_TRAVEL_DEGREES);


		private static final DoubleUnaryOperator rangeLimiter = Values.limiter(0.0, MAX_WRIST_TRAVEL_DEGREES);
		private static final DoubleUnaryOperator springRange = Values.mapRange(0, Preset.FORWARD_SPRING_MAX.wristPosition).toRange(Constants.kWrist_ForwardSpringMinTo, Constants.kWrist_ForwardSpringMaxTo);
		private static final DoubleUnaryOperator forwardRange = Values.mapRange(Preset.FORWARD_SPRING_MAX.wristPosition, Preset.FORWARD_GRAVITY_MAX.wristPosition).toRange(Constants.kWrist_ForwardGravityMinTo, Constants.kWrist_ForwardGravityMaxTo);
		private static final DoubleUnaryOperator reverseRange = Values.mapRange(Preset.REVERSE_GRAVITY_MIN.wristPosition, MAX_WRIST_TRAVEL_DEGREES).toRange(Constants.kWrist_ReverseGravityMinTo, Constants.kWrist_ReverseGravityMaxTo);
		
		private static final DoubleUnaryOperator gravity = (d) -> {
			double degrees = rangeLimiter.applyAsDouble(d);
			
			if (degrees >= 0 && degrees <= Preset.FORWARD_SPRING_MAX.wristPosition) {
				return springRange.applyAsDouble(degrees);
			}
			
			if (degrees >= Preset.FORWARD_SPRING_MAX.wristPosition && degrees <= Preset.FORWARD_GRAVITY_MAX.wristPosition) {
				return forwardRange.applyAsDouble(degrees);
			}
			
			if (degrees >= Preset.REVERSE_GRAVITY_MIN.wristPosition && degrees <= MAX_WRIST_TRAVEL_DEGREES){
				return reverseRange.applyAsDouble(degrees);
			}
			
			return 0;
		};
		
		private TalonSRX wrist;
		
		public static enum Preset {
			INTAKE(0),
			
			PORTAL(38),
			
			FORWARD_HORIZONTAL(13),
			FORWARD_VERTICAL(75),

			UP(90),
	
			TRANSIT(120),
			
			AUTOSHOT(170),

			FORWARD_SPRING_MAX(40),
			FORWARD_GRAVITY_MAX(130),
			REVERSE_GRAVITY_MIN(135),

			BACKWARDS_UP(156),
			BACKWARDS_LEVEL(170),
			BACKWARDS_DOWN(200),
			
			BACKWARDS_DUNK_30(215),
			BACKWARDS_DUNK_15(235),
			BACKWARDS_DUNK_0(250),
			FULL_BACK(MAX_WRIST_TRAVEL_DEGREES);

			public double wristPosition;
			
			private Preset(double wristPosition) {
				this.wristPosition = wristPosition;
			}
		}

		Wrist(boolean isPracticeBot) {
			wrist = new TalonSRX(Constants.kWrist_ID);
			
			configMotorControllers(10);
		}
		
		/**
		 * update configuration parameters on talonSRX from the constants file
		 * @param timeout
		 */
		void configMotorControllers(int timeout) {
			wrist.config_kP(0, Constants.kWrist_P, timeout);
			wrist.config_kI(0, Constants.kWrist_I, timeout);
			wrist.config_kD(0, Constants.kWrist_D, timeout);
			wrist.config_kF(0, Constants.kWrist_F, timeout);
			wrist.config_IntegralZone(0, Constants.kWrist_IZone, timeout);

			wrist.configForwardSoftLimitEnable(true, timeout);
			setSoftLimit(Wrist.Preset.BACKWARDS_LEVEL.wristPosition);
			
			wrist.configSelectedFeedbackCoefficient(1.0, 0, timeout);
			wrist.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, timeout);
			wrist.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, timeout);
			wrist.configAllowableClosedloopError(0, ticks(1), timeout);

			wrist.configMotionAcceleration(Constants.kWrist_Accel, timeout);
			wrist.configMotionCruiseVelocity(Constants.kWrist_Cruise_Velocity, timeout);

			wrist.setNeutralMode(NeutralMode.Brake);
			
			wrist.setSensorPhase(false);
			wrist.setInverted(Constants.kWrist_Invert);
		}
		
		/**
		 * Wrist
		 * ticks to degrees
		 * @param ticks
		 * @return degrees
		 */
		static double degrees(int ticks) {
			return ticks * (18.0/72.0) * (360.0/4096.0);
		}
		
		/**
		 * Wrist
		 * degrees to ticks
		 * @param degrees
		 * @return ticks
		 */
		static int ticks(double degrees) {
			return (int) (degrees * (72.0/18) * (4096.0/360));
		}
		
		/**
		 * holds wrist at current position
		 */
		void holdPosition() {
			wrist.set(ControlMode.MotionMagic, wrist.getSelectedSensorPosition(0));
		}
		
		/**
		 * raise or lower the wrist by @param change degrees.
		 * @param change amount to change the position by
		 */
		void changePosition(double change) {
			setPosition(degrees(wrist.getClosedLoopTarget(0))  + change);
		}
		
		double getClosedLoopTargetPosition() {
			return degrees(wrist.getClosedLoopTarget(0));
		}
		
		double getCurrentPosition() {
			return degrees(wrist.getSelectedSensorPosition(0));
		}
		
		/**
		 * Set the position of the elevator to a preset
		 * @param preset the desired preset
		 */
		void setPreset(Preset preset) {
			setPosition(preset.wristPosition);
		}
		
		/**
		 * Set the position of the elevator
		 * @param position the desired position in the elevator in inches
		 */
		void setPosition(double position) {		
			double gravityComp = gravity.applyAsDouble(position);
			
			SmartDashboard.putNumber("wrist gravity comp", gravityComp);
			
			wrist.set(ControlMode.MotionMagic, ticks(position), DemandType.ArbitraryFeedForward, gravityComp);
		}
		
		boolean atTarget() {
			return Math.abs(degrees(wrist.getClosedLoopError(0))) < Constants.kWrist_Tolerance;
		}
		
		void setOutput(double output) {
			wrist.set(ControlMode.PercentOutput, output);
		}
		
		void stop() {
			wrist.set(ControlMode.PercentOutput, 0);
		}
		
		/**
		 * Set soft limit of wrist
		 * @param degrees
		 */
		void setSoftLimit(double degrees) {
			wrist.configForwardSoftLimitThreshold(ticks(degrees), 5);
		}
		
		public void periodic() {	
			if (wrist.getControlMode() == ControlMode.MotionMagic) {
				SmartDashboard.putNumber("wrist desired Position: ", degrees(wrist.getClosedLoopTarget(0)));
				SmartDashboard.putBoolean("wrist at target?: ", atTarget());
				SmartDashboard.putNumber("wrist error: ", degrees(wrist.getClosedLoopError(0)));
			}
			SmartDashboard.putNumber("wrist output: ", wrist.getMotorOutputPercent());
			SmartDashboard.putNumber("wrist actual Position: ", getCurrentPosition());
		}
		
		public void setZero() {
			wrist.setSelectedSensorPosition(0, 0, 10);
		}		
		
		@Override
		protected void initDefaultCommand() {
		}		
}