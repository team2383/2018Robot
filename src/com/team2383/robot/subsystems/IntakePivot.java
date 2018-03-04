package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.team2383.ninjaLib.SetState;
import com.team2383.robot.subsystems.IntakePivot.State;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakePivot extends com.team2383.ninjaLib.SetState.StatefulSubsystem<IntakePivot.State>{

	private Solenoid intakePivot = new Solenoid(1, 0);
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
				intakePivot.set(true);
				SmartDashboard.putBoolean("Pivot ready to intake?", true);
				break;
			
			default:
			case UP:
				intakePivot.set(false);
				SmartDashboard.putBoolean("Pivot ready to intake?", false);
				break;
			}
		}

	@Override
	protected void initDefaultCommand() {
	}
}
