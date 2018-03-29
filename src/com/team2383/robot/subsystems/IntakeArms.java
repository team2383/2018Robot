package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.intakeArms;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.StatefulSubsystem;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.subsystems.LiftWrist.State;

import edu.wpi.first.wpilibj.Solenoid;


public class IntakeArms extends StatefulSubsystem<IntakeArms.State> {

	private Solenoid leftArm;
	private Solenoid rightArm;
	
	public IntakeArms(boolean isPracticeBot) {
		if(isPracticeBot) {
			leftArm = new Solenoid(1, 0);
			rightArm = new Solenoid(1, 1);
		} else {
			leftArm = new Solenoid(1, 0);
			rightArm = new Solenoid(1, 1);
		}

		instanceSupplier = () -> intakeArms;
		this.state = State.CLOSED;
	}
	
	public enum State {
		CLOSED, OPEN, LEFT, RIGHT;
	}

	@Override
	public void setState(State state) {
		switch (state) {
			case RIGHT:
				leftArm.set(false);
				rightArm.set(true);
				break;
				
			case LEFT:
				leftArm.set(true);
				rightArm.set(false);
				break;
				
			case OPEN:
				leftArm.set(true);
				rightArm.set(true);
				break;
				
			default:
			case CLOSED:
				leftArm.set(false);
				rightArm.set(false);
				break;
		}
	}

	@Override
	protected void initDefaultCommand() {
	}
}

