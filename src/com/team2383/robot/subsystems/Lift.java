package com.team2383.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.MotionUtils;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.OI;
import com.team2383.robot.StaticConstants;
import com.team2383.robot.commands.TeleopLiftOpenLoop;

import edu.wpi.first.wpilibj.command.Subsystem;


public class Lift extends Subsystem {

	
		//calculated kV is somewhere around 0.27
		//travel is 38.5 inches
		//16tooth sprocket, diameter of 1.406"
	
		private static final double SPROCKET_CIRCUMFERENCE_IN = 1.406 * Math.PI;
		private static final double MAX_TRAVEL_IN = 38.5;
		private static final double MAX_TRAVEL_ROTATIONS = inchesToRotations(MAX_TRAVEL_IN);

		private TalonSRX masterLift;
		private TalonSRX followerLift;
		private double position;

		public Lift() {
			masterLift = new TalonSRX(StaticConstants.kLift_LeftTalonID);
			followerLift = new TalonSRX(StaticConstants.kLift_RightTalonID);
			
			masterLift.set(ControlMode.Follower, masterLift.getDeviceID());
			masterLift.config_kP(0, 0.2, 0);
			masterLift.config_kI(0, 0, 0);
			masterLift.config_kD(0, 0, 0);
			masterLift.config_kF(0, 0.27, 0);
			masterLift.config_IntegralZone(0, 0, 0);
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
		 * raise or lower the lift by @param change inches.
		 * @param change amount to change the position by
		 */
		public void changePosition(double change) {
			setPosition(position += change);
		}
		
		/**
		 * Set the position of the elevator
		 * @param position the desired position in the elevator in inches
		 */
		public void setPosition(double position) {
			this.position = Math.max(Math.min(position, MAX_TRAVEL_IN), 0);
			masterLift.set(ControlMode.MotionMagic, inchesToRotations(position));
		}
		
		public void setOutput(double output) {
			masterLift.set(ControlMode.PercentOutput, output);
		}
		
		public void stop() {
			masterLift.set(ControlMode.PercentOutput, 0);
		}
		
		@Override
		protected void initDefaultCommand() {
			this.setDefaultCommand(new TeleopLiftOpenLoop(OI.liftSpeed));
		}		
}
