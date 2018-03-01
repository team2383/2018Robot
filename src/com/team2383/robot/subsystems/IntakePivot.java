package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.ninjaLib.SetState;
import com.team2383.robot.subsystems.IntakePivot.State;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class IntakePivot extends com.team2383.ninjaLib.SetState.StatefulSubsystem<IntakePivot.State>{

	private DoubleSolenoid intakePivot = new DoubleSolenoid(
					prefs.getInt("kIntake_PivotUp", 6),
					prefs.getInt("kIntake_PivotDown", 7));
	private State state = State.UP;
	
		
		
	public IntakePivot() {
		setState(State.UP);
	}

	public enum State {
		DOWN, UP
	}

	@Override
	public void setState(State state) {
		switch (state) {
			case DOWN:
				intakePivot.set(Value.kForward);
				break;
			
			default:
			case UP:
				intakePivot.set(Value.kReverse);
				break;
			}
		}

	@Override
	protected void initDefaultCommand() {
	}
}
