package com.team2383.robot;

import com.kauailabs.navx.frc.AHRS;
import com.team2383.ninjaLib.Prefs;
import com.team2383.robot.subsystems.Drive;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakePivot;
import com.team2383.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.SPI;

public class HAL {

	// preferences
	
	public static Prefs prefs = new Prefs();
	
	public static boolean isPracticeBot = prefs.getBoolean("isPracticeBot", false);
	
	// subsystems
	public static AHRS navX = new AHRS(SPI.Port.kMXP);
	public static Drive drive = new Drive(isPracticeBot);
	public static Intake intake = new Intake(isPracticeBot);
	public static IntakePivot intakePivot = new IntakePivot(isPracticeBot);
	public static Lift lift = new Lift(isPracticeBot);
}
