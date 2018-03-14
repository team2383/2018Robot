package com.team2383.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.MotionUtils;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Utility;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Lift extends Subsystem {
		//calculated kV is somewhere around 0.27
		//travel is 38.5 inches
		//16tooth sprocket, diameter of 1.406"
	
		/**
		 * 4.926 = sprocket circumference
		 * 38.7 = max travel
		 * 
		 * 7.856 = max rotations
		 * 32179 = integer ticks max
		 */
	

		private static final double MAX_LIFT_TRAVEL_IN = 38.7;
		private static double SPROCKET_CIRCUMFERENCE_IN;
		private static double MAX_LIFT_TRAVEL_TICKS;

		private TalonSRX masterLift;
		private BaseMotorController followerLift;
		
		static enum Preset {
			BOTTOM(0),
			TRAVEL(2),

			SWITCH(15),

			SCALE_MID(MAX_LIFT_TRAVEL_IN-4),
			SCALE_HIGH(MAX_LIFT_TRAVEL_IN),

			TOP(MAX_LIFT_TRAVEL_IN);
			
			public double liftPosition;
			
			private Preset(double liftPosition) {
				this.liftPosition = liftPosition;
			}
		}

		Lift(boolean isPracticeBot) {
			masterLift = new TalonSRX(Constants.kLift_Master_ID);
			
			if (isPracticeBot) {
				SPROCKET_CIRCUMFERENCE_IN = 1.406 * Math.PI;
				MAX_LIFT_TRAVEL_TICKS = liftTicks(MAX_LIFT_TRAVEL_IN);
				followerLift = new TalonSRX(Constants.kLift_Follower_ID);
			} else {
				followerLift = new VictorSPX(Constants.kLift_Follower_ID);
				SPROCKET_CIRCUMFERENCE_IN = 1.568 * Math.PI;
				MAX_LIFT_TRAVEL_TICKS = liftTicks(MAX_LIFT_TRAVEL_IN);
			}
			
			configMotorControllers(10);
		}
		
		/**
		 * update configuration parameters on talonSRX from the constants file
		 * @param timeout
		 */
		void configMotorControllers(int timeout) {
			
			masterLift.config_kP(0, Constants.kLift_P, timeout);
			masterLift.config_kI(0, Constants.kLift_I, timeout);
			masterLift.config_kD(0, Constants.kLift_D, timeout);
			masterLift.config_kF(0, Constants.kLift_F, timeout);
			masterLift.config_IntegralZone(0, Constants.kLift_IZone, timeout);
			
			
			masterLift.configSelectedFeedbackCoefficient(1, 0, timeout);
			masterLift.configForwardSoftLimitThreshold((int) MAX_LIFT_TRAVEL_TICKS, timeout);
			masterLift.configForwardSoftLimitEnable(true, timeout);
			masterLift.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, timeout);
			
			masterLift.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, timeout);
			masterLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, timeout);

			masterLift.configMotionAcceleration(Constants.kLift_Accel, timeout);
			masterLift.configMotionCruiseVelocity(Constants.kLift_Cruise_Velocity, timeout);

			masterLift.setNeutralMode(NeutralMode.Brake);
			followerLift.setNeutralMode(NeutralMode.Brake);
			
			masterLift.setSensorPhase(false);
			masterLift.setInverted(Constants.kLift_InvertMaster);
			followerLift.setInverted(Constants.kLift_InvertFollower);
			followerLift.follow(masterLift);
		}
		
		/**
		 * Lift
		 * ticks to inches
		 * @param ticks
		 * @return inches
		 */
		static double liftInches(int ticks) {
			return MotionUtils.rotationsToDistance((ticks / 4096.0), SPROCKET_CIRCUMFERENCE_IN);
		}
		
		/**
		 * Lift
		 * inches to ticks
		 * @param inches
		 * @return ticks
		 */
		static int liftTicks(double inches) {
			return (int) (MotionUtils.distanceToRotations(inches, SPROCKET_CIRCUMFERENCE_IN) * 4096.0);
		}
		
		/**
		 * raise or lower the lift by @param change inches.
		 * @param change amount to change the position by
		 */
		void changePosition(double change) {
			setPosition(liftInches(masterLift.getClosedLoopTarget(0)) + change);
		}
		
		void holdPosition() {
			System.out.println("holding at " + masterLift.getSelectedSensorPosition(0) + " to: " + liftInches(masterLift.getSelectedSensorPosition(0)));
			
			masterLift.set(ControlMode.MotionMagic, masterLift.getSelectedSensorPosition(0));
		}
		
		double getClosedLoopTargetPosition() {
			return liftInches(masterLift.getClosedLoopTarget(0));
		}
		
		double getCurrentPosition() {
			return liftInches(masterLift.getSelectedSensorPosition(0));
		}
		
		/**
		 * Set the position of the elevator to a preset
		 * @param preset the desired preset
		 */
		void setPreset(Preset preset) {
			setPosition(preset.liftPosition);
		}
		
		/**
		 * Set the position of the elevator
		 * @param position the desired position in the elevator in inches
		 */
		void setPosition(double position) {
			masterLift.set(ControlMode.MotionMagic, liftTicks(position));
		}
		
		boolean atTarget() {
			return Math.abs(getClosedLoopTargetPosition()-getCurrentPosition()) < Constants.kLift_Tolerance;
		}
		
		void setOutput(double output) {
			masterLift.set(ControlMode.PercentOutput, output);
		}
		
		void stop() {
			masterLift.set(ControlMode.PercentOutput, 0);
		}
		
		public void periodic() {
			if (masterLift.getControlMode() == ControlMode.MotionMagic) {
				SmartDashboard.putNumber("lift desired Position: ", liftInches(masterLift.getClosedLoopTarget(0)));
				SmartDashboard.putNumber("lift error: ", liftInches(Math.abs(masterLift.getClosedLoopError(0))));
				SmartDashboard.putBoolean("lift at target?: ", atTarget());
			}
			
			SmartDashboard.putNumber("lift actual Position: ", getCurrentPosition());
			SmartDashboard.putNumber("lift Rotations: ", liftInches(masterLift.getSelectedSensorPosition(0)));
			SmartDashboard.putNumber("Lift ticks", liftTicks(getCurrentPosition()));
		}
		
		@Override
		protected void initDefaultCommand() {
		}		
}
