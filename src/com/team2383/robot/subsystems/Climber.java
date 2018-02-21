package com.team2383.robot.subsystems;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climber extends com.team2383.ninjaLib.SetState.StatefulSubsystem<Climber.State>{

		private DoubleSolenoid climberLeft = new DoubleSolenoid(Constants.kClimberLeftIn, Constants.kClimberLeftOut);
		private DoubleSolenoid climberRight = new DoubleSolenoid(Constants.kClimberRightIn, Constants.kClimberRightOut);
		private State state = State.RETRACTED;
		
			
			
		public Climber() {
			setState(State.RETRACTED);
		}

		public enum State {
			EXTENDED, RETRACTED
		}

		@Override
		public void setState(State state) {
			switch (state) {
				case EXTENDED:
					climberLeft.set(Value.kReverse);
					climberRight.set(Value.kReverse);
					break;

				default:
				case RETRACTED:
					climberLeft.set(Value.kForward);
					climberRight.set(Value.kForward);
					break;
				}
			}

		@Override
		protected void initDefaultCommand() {
			setState(State.RETRACTED);
		}


}
