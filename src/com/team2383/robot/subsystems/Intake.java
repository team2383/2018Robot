package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.prefs;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team2383.ninjaLib.SetState;


public class Intake extends SetState.StatefulSubsystem<Intake.State> {

	private BaseMotorController leftFeeder;
	private BaseMotorController leftShooter;
	
	private BaseMotorController rightFeeder;
	private BaseMotorController rightShooter;
	
	public Intake(boolean isPracticeBot){
		
		if (isPracticeBot) {
			leftFeeder = new TalonSRX(prefs.getInt("kIntake_LeftFeederTalonID", 9));
			leftShooter = new TalonSRX(prefs.getInt("kIntake_LeftShooterTalonID", 10));
			
			rightFeeder = new TalonSRX(prefs.getInt("kIntake_RightFeederTalonID", 11));
			rightShooter = new TalonSRX(prefs.getInt("kIntake_RightShooterTalonID", 12));
		} else {
			leftFeeder = new VictorSPX(prefs.getInt("kIntake_LeftFeederTalonID", 9));
			leftShooter = new VictorSPX(prefs.getInt("kIntake_LeftShooterTalonID", 10));
			
			rightFeeder = new VictorSPX(prefs.getInt("kIntake_RightFeederTalonID", 11));
			rightShooter = new VictorSPX(prefs.getInt("kIntake_RightShooterTalonID", 12));
		}
		
		/*
		 * feeder init
		 */
		
		leftFeeder.setNeutralMode(NeutralMode.Brake);
		rightFeeder.setNeutralMode(NeutralMode.Brake);
		
		leftFeeder.configPeakOutputForward(0.7, 0);
		leftFeeder.configPeakOutputReverse(-0.7, 0);
		
		rightFeeder.configPeakOutputForward(0.7, 0);
		rightFeeder.configPeakOutputReverse(-0.7, 0);
		
		
		/*
		 * shooter init
		 */
		
		leftShooter.setNeutralMode(NeutralMode.Brake);
		rightShooter.setNeutralMode(NeutralMode.Brake);
		
		leftShooter.configPeakOutputForward(0.9,0);
		leftShooter.configPeakOutputReverse(-0.9,0);
		
		rightShooter.configPeakOutputForward(0.9,0);
		rightShooter.configPeakOutputReverse(-0.9,0);
	}
	
	public enum State {
		FEED, UNFEED, STOPPED
	}
	
	public void feed(){
		leftFeeder.set(ControlMode.PercentOutput, 1.0);
		rightFeeder.set(ControlMode.PercentOutput, 1.0);
		
		leftShooter.set(ControlMode.PercentOutput, 1.0);
		rightShooter.set(ControlMode.PercentOutput, 1.0);
	}
	
	public void unfeed(){
		leftFeeder.set(ControlMode.PercentOutput, -1.0);
		rightFeeder.set(ControlMode.PercentOutput, -1.0);
		
		leftShooter.set(ControlMode.PercentOutput, -1.0);
		rightShooter.set(ControlMode.PercentOutput, -1.0);
	}

	public void stop() {
		leftFeeder.set(ControlMode.PercentOutput, 0);
		rightFeeder.set(ControlMode.PercentOutput, 0);
		
		leftShooter.set(ControlMode.PercentOutput, 0);
		rightShooter.set(ControlMode.PercentOutput, 0);
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

