package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.intake;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.StatefulSubsystem;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;


public class Intake extends StatefulSubsystem<Intake.State> {

	private BaseMotorController leftIntake;
	private BaseMotorController rightIntake;
	
	public Intake(boolean isPracticeBot){
		if (isPracticeBot) {
			leftIntake = new TalonSRX(Constants.kIntake_LeftIntake_ID);
			rightIntake = new TalonSRX(Constants.kIntake_RightIntake_ID);
		} else {
			leftIntake = new VictorSPX(Constants.kIntake_LeftIntake_ID);
			rightIntake = new VictorSPX(Constants.kIntake_RightIntake_ID);
		}
		
		/*
		 * Intake init
		 */
		
		leftIntake.setNeutralMode(NeutralMode.Brake);
		rightIntake.setNeutralMode(NeutralMode.Brake);
		
		leftIntake.setInverted(Constants.kIntake_InvertLeft);
		rightIntake.setInverted(Constants.kIntake_InvertRight);
		
		leftIntake.configPeakOutputForward(1.0, 0);
		leftIntake.configPeakOutputReverse(-1.0, 0);
		
		rightIntake.configPeakOutputForward(1.0, 0);
		rightIntake.configPeakOutputReverse(-1.0, 0);
		
		instanceSupplier = () -> intake;
	}
	
	public enum State {
		RAW, FEED, UNFEED_SLOW, UNFEED_FAST, UNFEED_SWITCHAUTO, STOP
	}

	@Override
	public void setState(State state) {
		switch (state) {
			case RAW:
				leftIntake.set(ControlMode.PercentOutput, -OI.liftSpeed.getAsDouble());
				rightIntake.set(ControlMode.PercentOutput, -OI.liftSpeed.getAsDouble());
				break;

			case UNFEED_SWITCHAUTO:
			case FEED:
				leftIntake.set(ControlMode.PercentOutput, 1.0);
				rightIntake.set(ControlMode.PercentOutput, 1.0);
				break;
				
			case UNFEED_SLOW:
				leftIntake.set(ControlMode.PercentOutput, -Constants.kIntake_UnfeedSlowSpeed);
				rightIntake.set(ControlMode.PercentOutput, -Constants.kIntake_UnfeedSlowSpeed);
				break;
				
			case UNFEED_FAST:
				leftIntake.set(ControlMode.PercentOutput, -Constants.kIntake_UnfeedFastSpeed);
				rightIntake.set(ControlMode.PercentOutput, -Constants.kIntake_UnfeedFastSpeed);
				break;
				
			default:
			case STOP:
				leftIntake.set(ControlMode.PercentOutput, 0);
				rightIntake.set(ControlMode.PercentOutput, 0);
				break;
		}
	}

	@Override
	protected void initDefaultCommand() {
	}
}

