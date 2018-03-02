package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ClimberRight extends com.team2383.ninjaLib.SetState.StatefulSubsystem<ClimberRight.State> {

	private DoubleSolenoid climberRight = new DoubleSolenoid(
			prefs.getInt("kClimber_Right_In", 3),
			prefs.getInt("kClimber_Right_Out", 4));

	private State state = State.RETRACTED;
 
	public ClimberRight() {
		setState(State.RETRACTED);
	}

	public enum State {
		EXTENDED, RETRACTED
	}

	@Override
	public void setState(State state) {
		switch (state) {
		case EXTENDED:
			climberRight.set(Value.kReverse);
			break;

		default:
		case RETRACTED:
			climberRight.set(Value.kForward);
			break;
		}
	}

	@Override
	protected void initDefaultCommand() {
	}

}