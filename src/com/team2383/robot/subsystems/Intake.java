package com.team2383.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2383.ninjaLib.SetState;
import com.team2383.robot.Constants;

/*
 * This subsystem includes the conveyor streaming balls into the shooter
 */


public class Intake extends SetState.StatefulSubsystem<Intake.State> {

	private TalonSRX intake = new TalonSRX(Constants.kFeederTalonID);
	private State state = State.STOPPED;
	
	public Intake(){
		intake.setNeutralMode(NeutralMode.Brake);
	}
	
	public enum State {
		FEED, UNFEED, STOPPED
	}
	
	public void feed(){
		intake.set(ControlMode.PercentOutput, 1);
	}
	public void unfeed(){
		intake.set(ControlMode.PercentOutput, -0.6);
	}
	public void stop() {
		intake.set(ControlMode.PercentOutput, 0);
	}

	@Override
	public void setState(State state) {
		switch (state) {
			case FEED:
				feed();
				break;
				
			case UNFEED:
				unfeed();
				break;
	
			default:
			case STOPPED:
				stop();
				break;
		}
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
	}
}
