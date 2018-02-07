package com.team2383.robot;

import com.kauailabs.navx.frc.AHRS;
import com.team2383.robot.subsystems.Drive;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.SPI;

public class HAL {

	// preferences
	
	public static Prefs prefs = new Prefs();
	
	// subsystems
	public static AHRS navX = new AHRS(SPI.Port.kMXP);
	public static Drive drive = new Drive();
	public static Intake intake = new Intake();
	public static Lift lift = new Lift();
}
