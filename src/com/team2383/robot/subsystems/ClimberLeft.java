package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.StaticConstants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ClimberLeft extends com.team2383.ninjaLib.SetState.StatefulSubsystem<ClimberLeft.State> {

	private DoubleSolenoid climberLeft = new DoubleSolenoid(
			prefs.getInt("kClimber_Left_In", 0),
			prefs.getInt("kClimber_Left_Out", 1));

	private State state = State.RETRACTED;

	public ClimberLeft() {
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
			break;

		default:
		case RETRACTED:
			climberLeft.set(Value.kForward);
			break;
		}
	}

	@Override
	protected void initDefaultCommand() {
	}
}