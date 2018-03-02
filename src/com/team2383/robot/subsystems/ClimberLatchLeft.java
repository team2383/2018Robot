package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class ClimberLatchLeft extends com.team2383.ninjaLib.SetState.StatefulSubsystem<ClimberLatchLeft.State>{

		
		private Solenoid climberLatchLeft = new Solenoid(
				prefs.getInt("kClimberLatch_Left", 2));

		private State state = State.OPEN;
			
		public ClimberLatchLeft() {
			setState(State.CLOSED);
		}

		public enum State {
			OPEN, CLOSED
		}

		@Override
		public void setState(State state) {
			switch (state) {
				case OPEN:
					climberLatchLeft.set(true);
					break;

				default:
				case CLOSED:
					climberLatchLeft.set(false);
					break;
				}
			}

		@Override
		protected void initDefaultCommand() {
			// TODO Auto-generated method stub
		}


}