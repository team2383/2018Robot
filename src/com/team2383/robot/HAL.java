package com.team2383.robot;

import com.kauailabs.navx.frc.AHRS;
import com.team2383.robot.subsystems.Climber;
import com.team2383.robot.subsystems.ClimberPin;
import com.team2383.robot.subsystems.Drivetrain;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakePivot;
import com.team2383.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.SPI;

public class HAL {
	
	// subsystems
	public static Drivetrain drivetrain = new Drivetrain();
	public static Intake intake = new Intake();
	public static Lift lift = new Lift();
	public static IntakePivot intakePivot = new IntakePivot();
	public static ClimberPin climberPin = new ClimberPin();
	public static Climber climber = new Climber();
	public static AHRS navX = new AHRS(SPI.Port.kMXP);
}
