package com.team2383.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.Constants;


public class Lift extends SetState.StatefulSubsystem<Lift.State> {

		private TalonSRX leftLift = new TalonSRX(Constants.kLift_LeftTalonID);
		private TalonSRX rightLift = new TalonSRX(Constants.kLift_RightTalonID);
		private State state = State.STOPPED;
		
		public Lift(){
			leftLift.setNeutralMode(NeutralMode.Brake);
			rightLift.setNeutralMode(NeutralMode.Brake);
			
			
			leftLift.configPeakOutputForward(0.7, 0);
			leftLift.configPeakOutputReverse(-0.7, 0);
			
			rightLift.configPeakOutputForward(0.7, 0);
			rightLift.configPeakOutputReverse(-0.7, 0);
		}
		
		public enum State {
			UP, DOWN, STOPPED
		}
		
		public void up(){
			leftLift.set(ControlMode.PercentOutput, -0.4);
			rightLift.set(ControlMode.PercentOutput, 0.4);
		}
		public void down(){
			leftLift.set(ControlMode.PercentOutput, 0.4);
			rightLift.set(ControlMode.PercentOutput, -0.4);
		}
		public void stop() {
			leftLift.set(ControlMode.PercentOutput, 0);
			rightLift.set(ControlMode.PercentOutput, 0);
		}

		@Override
		public void setState(State state) {
			switch (state) {
				case UP:
					up();
					break;
					
				case DOWN:
					down();
					break;
		
				default:
				case STOPPED:
					stop();
					break;
			}
		}

		@Override
		protected void initDefaultCommand() {
			// TODO Auto-generated method stub
		}
}
