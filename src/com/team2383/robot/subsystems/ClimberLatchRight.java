package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class ClimberLatchRight extends com.team2383.ninjaLib.SetState.StatefulSubsystem<ClimberLatchRight.State>{

		private Solenoid climberLatchRight = new Solenoid(
				prefs.getInt("kClimberLatch_Right", 5));
		

		private State state = State.OPEN;
			
		public ClimberLatchRight() {
			setState(State.CLOSED);
		}

		public enum State {
			OPEN, CLOSED
		}

		@Override
		public void setState(State state) {
			switch (state) {
				case OPEN:
					climberLatchRight.set(true);
					break;

				default:
				case CLOSED:
					climberLatchRight.set(false);
					break;
				}
			}

		@Override
		protected void initDefaultCommand() {
			// TODO Auto-generated method stub
		}


}