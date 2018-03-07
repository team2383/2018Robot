package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.lift;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Wrist extends Subsystem {
		/*
		 * Gearbox
		 * BAG
		 * 10:1
		 * 4:1
		 * ENCODER
		 * 18:72
		 * 
		 * 1encoder rot/4096ticks * 18 wrist rots / 72 encoder rots * 3600 ticks / 4096 ticks
		 * 
		 * max speed deg/s at motor = 1977 deg/s
		 */

		private static final double TALON_COEFF = (18.0/72.0) * (3600.0/4096.0);
		private static final double MAX_WRIST_TRAVEL_DEGREES = 190;
		private static final double MAX_WRIST_TRAVEL_TICKS = ticks(MAX_WRIST_TRAVEL_DEGREES);

		private TalonSRX wrist;
		
		public static enum Preset {
			INTAKE(0),
			SWITCH(10),
			FORWARD_MID(15),
			FORWARD_HIGH(20),
			UP(90),
			BACKWARDS(190);

			public double wristPosition;
			
			private Preset(double wristPosition) {
				this.wristPosition = wristPosition;
			}
		}

		public Wrist(boolean isPracticeBot) {
			wrist = new TalonSRX(Constants.kWrist_ID);
			
			configMotorControllers(10);
		}
		
		/**
		 * update configuration parameters on talonSRX from the constants file
		 * @param timeout
		 */
		public void configMotorControllers(int timeout) {
			wrist.config_kP(0, Constants.kWrist_P, timeout);
			wrist.config_kI(0, Constants.kWrist_I, timeout);
			wrist.config_kD(0, Constants.kWrist_D, timeout);
			wrist.config_kF(0, Constants.kWrist_F, timeout);
			wrist.config_IntegralZone(0, Constants.kWrist_IZone, timeout);

			wrist.configForwardSoftLimitEnable(true, timeout);
			//enforce travel limits of wrist
			if (Math.abs(Lift.Preset.TOP.liftPosition - lift.getCurrentPosition()) < Constants.kWrist_FullTravelTolerance) {
				setSoftLimit(Wrist.Preset.BACKWARDS.wristPosition);
			} else {
				setSoftLimit(Wrist.Preset.UP.wristPosition);
			}
			
			wrist.configSelectedFeedbackCoefficient(TALON_COEFF, 0, timeout);
			wrist.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, timeout);
			wrist.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, timeout);

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
		private static double degrees(int ticks) {
			return ticks / 10.0;
		}
		
		/**
		 * Wrist
		 * degrees to ticks
		 * @param degrees
		 * @return ticks
		 */
		private static int ticks(double degrees) {
			return (int) (degrees * 10.0);
		}
		
		/**
		 * raise or lower the lift by @param change inches.
		 * @param change amount to change the position by
		 */
		public void changePosition(double change) {
			setPosition(wrist.getClosedLoopTarget(0) + change);
		}
		
		public double getCurrentPosition() {
			return degrees(wrist.getSelectedSensorPosition(0));
		}
		
		/**
		 * Set the position of the elevator to a preset
		 * @param preset the desired preset
		 */
		public void setPreset(Preset preset) {
			setPosition(preset.wristPosition);
		}
		
		/**
		 * Set the position of the elevator
		 * @param position the desired position in the elevator in inches
		 */
		public void setPosition(double position) {
			wrist.configForwardSoftLimitEnable(true, 2);
			wrist.set(ControlMode.MotionMagic, ticks(position));
		}
		
		public boolean atTarget() {
			return degrees(wrist.getClosedLoopError(0)) < Constants.kWrist_Tolerance;
		}
		
		public void setOutput(double output) {
			/*
			 * just in case the encoder is broken
			 */
			wrist.configForwardSoftLimitEnable(false, 2);
			wrist.set(ControlMode.PercentOutput, output);
		}
		
		public void stop() {
			wrist.set(ControlMode.PercentOutput, 0);
		}
		
		/**
		 * Set soft limit of wrist
		 * @param degrees
		 */
		private void setSoftLimit(double degrees) {
			wrist.configForwardSoftLimitThreshold(ticks(degrees), 5);
		}
		
		public void periodic() {
			//enforce travel limits of wrist
			if (Math.abs(Lift.Preset.TOP.liftPosition - lift.getCurrentPosition()) < Constants.kWrist_FullTravelTolerance) {
				setSoftLimit(Wrist.Preset.BACKWARDS.wristPosition);
			} else {
				setSoftLimit(Wrist.Preset.UP.wristPosition);
			}
			
			if (wrist.getControlMode() == ControlMode.MotionMagic) {
				SmartDashboard.putNumber("wrist desired Position: ", degrees(wrist.getClosedLoopTarget(0)));
			}

			SmartDashboard.putNumber("wrist actual Position: ", getCurrentPosition());
		}
		
		@Override
		protected void initDefaultCommand() {
		}		
}