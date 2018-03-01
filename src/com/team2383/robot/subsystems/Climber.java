package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climber extends com.team2383.ninjaLib.SetState.StatefulSubsystem<Climber.State>{

		
		private DoubleSolenoid climberLeft = new DoubleSolenoid(
				prefs.getInt("kClimber_Left_In", 0),
				prefs.getInt("kClimber_Left_Out", 1));
			
		private DoubleSolenoid climberRight = new DoubleSolenoid(
				prefs.getInt("kClimber_Right_In", 3),
				prefs.getInt("kClimber_Right_Out", 4));
		
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