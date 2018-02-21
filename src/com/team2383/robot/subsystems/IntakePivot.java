package com.team2383.robot.subsystems;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class IntakePivot extends com.team2383.ninjaLib.SetState.StatefulSubsystem<IntakePivot.State>{

	private DoubleSolenoid intakePivot = new DoubleSolenoid(Constants.kIntakePivotUp, Constants.kIntakePivotDown);
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
			case UP:
				intakePivot.set(Value.kForward);
				break;

			default:
			case DOWN:
				intakePivot.set(Value.kReverse);
				break;
			}
		}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
	}

}
