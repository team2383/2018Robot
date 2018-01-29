package com.team2383.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.Constants;


public class Lift extends SetState.StatefulSubsystem<Lift.State> {

		private TalonSRX lift = new TalonSRX(Constants.kLiftTalonID);
		private State state = State.STOPPED;
		
		public Lift(){
			lift.setNeutralMode(NeutralMode.Brake);
		}
		
		public enum State {
			UP, DOWN, STOPPED
		}
		
		public void up(){
			lift.set(ControlMode.PercentOutput, -0.4);
		}
		public void down(){
			lift.set(ControlMode.PercentOutput, 0.4);
		}
		public void stop() {
			lift.set(ControlMode.PercentOutput, 0);
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
