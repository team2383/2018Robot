package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;

public class ClimberPin extends com.team2383.ninjaLib.SetState.StatefulSubsystem<ClimberPin.State>{

		
		private Solenoid climberPinLeft = new Solenoid(
				prefs.getInt("kClimberPin_Left", 2));

		private Solenoid climberPinRight = new Solenoid(
				prefs.getInt("kClimberPin_Right", 5));
		

		private State state = State.EXTENDED;
			
		public ClimberPin() {
			setState(State.EXTENDED);
		}

		public enum State {
			EXTENDED, RETRACTED
		}

		@Override
		public void setState(State state) {
			switch (state) {
				case EXTENDED:
					climberPinLeft.set(false);
					climberPinRight.set(false);
					break;

				default:
				case RETRACTED:
					climberPinLeft.set(true);
					climberPinRight.set(true);
					break;
				}
			}

		@Override
		protected void initDefaultCommand() {
			// TODO Auto-generated method stub
		}


}