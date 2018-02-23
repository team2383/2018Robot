package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.OI;
import com.team2383.robot.StaticConstants;
import com.team2383.robot.commands.TeleopLiftOpenLoop;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Lift extends Subsystem {

	
		//calculated kV is somewhere around 0.27
		//travel is 38.5 inches
		//16tooth sprocket, diameter of 1.406"
	
		private static final double SPROCKET_CIRCUMFERENCE_IN = 1.568 * Math.PI;
		private static final double MAX_TRAVEL_IN = 39;
		private static final double MAX_TRAVEL_ROTATIONS = inchesToRotations(MAX_TRAVEL_IN);
		private static final double MAX_TRAVEL_TICKS = MAX_TRAVEL_ROTATIONS * 4096;

		private TalonSRX masterLift;
		private VictorSPX followerLift;
		private double position;
		
		public static enum Preset {
			BOTTOM(0),
			SWITCH(20),
			TRAVEL(10),
			SCALE_LOW(30),
			SCALE_MID(35),
			SCALE_HIGH(39),
			TOP(MAX_TRAVEL_IN);
			
			public double position;
			
			private Preset(double position) {
				this.position = position;
			}
		}

		public Lift() {
			masterLift = new TalonSRX(StaticConstants.kLift_LeftTalonID);
			followerLift = new VictorSPX(StaticConstants.kLift_RightTalonID);
			
			masterLift.set(ControlMode.Follower, masterLift.getDeviceID());
			masterLift.config_kP(0, 0.2, 10);
			masterLift.config_kI(0, 0, 10);
			masterLift.config_kD(0, 0.2, 10);
			masterLift.config_kF(0, 0.27, 10);
			masterLift.config_IntegralZone(0, 0, 10);
			masterLift.configForwardSoftLimitEnable(true, 10);
			masterLift.configForwardSoftLimitThreshold((int) MAX_TRAVEL_ROTATIONS * 4096, 10);
			masterLift.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, 10);
			masterLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
			
			masterLift.configMotionAcceleration(prefs.getInt("Lift MM Accel", 8000), 10);
			masterLift.configMotionCruiseVelocity(prefs.getInt("Lift MM Cruise Velocity", 6000), 10);
			
			masterLift.setSensorPhase(false);
			masterLift.setInverted(true);
			
			masterLift.setNeutralMode(NeutralMode.Brake);
			followerLift.setNeutralMode(NeutralMode.Brake);
			
			followerLift.setInverted(false);
			followerLift.follow(masterLift);
		}
		
		/**
		 * Convert inches of height to rotations
		 * @param position the position in inches
		 * @return the position we are demanding from the lift talon
		 */
		private static double inchesToRotations(double position) {
			return MotionUtils.distanceToRotations(position, SPROCKET_CIRCUMFERENCE_IN);
		}
		
		/**
		 * Convert rotations to inches of height
		 * @param rotations the position in rotations
		 * @return position in inches
		 */
		private static double rotationsToInches(double rotations) {
			return MotionUtils.rotationsToDistance(rotations, SPROCKET_CIRCUMFERENCE_IN);
		}
		
		/**
		 * raise or lower the lift by @param change inches.
		 * @param change amount to change the position by
		 */
		public void changePosition(double change) {
			setPosition(position += change);
		}
		
		public double getCurrentPosition() {
			return rotationsToInches(masterLift.getSelectedSensorPosition(0)/4096.0);
		}
		
		/**
		 * Set the position of the elevator to a preset
		 * @param preset the desired preset
		 */
		public void setPreset(Preset preset) {
			setPosition(preset.position);
		}
		
		/**
		 * Set the position of the elevator
		 * @param position the desired position in the elevator in inches
		 */
		public void setPosition(double position) {
			this.position = Math.max(Math.min(position, MAX_TRAVEL_IN), 0);
			
			masterLift.configMotionAcceleration(prefs.getInt("Lift MM Accel", 8000), 0);
			masterLift.configMotionCruiseVelocity(prefs.getInt("Lift MM Cruise Velocity", 4200), 0);
			masterLift.set(ControlMode.MotionMagic, inchesToRotations(position)*4096.0);
		}
		
		public boolean atTarget() {
			return rotationsToInches(masterLift.getClosedLoopError(0)/4096.0) < prefs.getDouble("kLift_tolerance", 1.0);
		}
		
		public void setOutput(double output) {
			masterLift.set(ControlMode.PercentOutput, output);
		}
		
		public void stop() {
			masterLift.set(ControlMode.PercentOutput, 0);
		}
		
		public void periodic() {
			SmartDashboard.putNumber("desired Position: ", position);
			SmartDashboard.putNumber("actual Position: ", rotationsToInches(masterLift.getSelectedSensorPosition(0)/4096.0));
;			SmartDashboard.putNumber("Rotations: ", masterLift.getSelectedSensorPosition(0)/4096.0);
		}
		
		@Override
		protected void initDefaultCommand() {
			//
		}		
}
