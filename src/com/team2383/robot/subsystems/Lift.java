package com.team2383.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.Constants;


public class Lift extends SetState.StatefulSubsystem<Lift.State> {

		private TalonSRX leftLift = new TalonSRX(Constants.kLeftLiftTalonID);
		private VictorSPX rightLift = new VictorSPX(Constants.kRightLiftTalonID);
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
			leftLift.set(ControlMode.PercentOutput, -0.5);
			rightLift.set(ControlMode.PercentOutput, 0.5);
		}
		public void down(){
			leftLift.set(ControlMode.PercentOutput, 0.5);
			rightLift.set(ControlMode.PercentOutput, -0.5);
		}
		public void stop() {
			leftLift.set(ControlMode.PercentOutput, 0);
			rightLift.set(ControlMode.PercentOutput, 0);
		}
		
		public void moveAtSpeed(double speed){
			leftLift.set(ControlMode.PercentOutput, speed);
			rightLift.set(ControlMode.PercentOutput, -speed);
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
