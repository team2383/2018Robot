package com.team2383.robot;

import com.kauailabs.navx.frc.AHRS;
import com.team2383.robot.subsystems.Drivetrain;
import com.team2383.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.SPI;

public class HAL {
	
	// subsystems
	public static Drivetrain drivetrain = new Drivetrain();
	public static Intake intake = new Intake();
	public static AHRS navX = new AHRS(SPI.Port.kMXP);
}
