package com.team2383.robot;

import com.kauailabs.navx.frc.AHRS;
import com.team2383.robot.subsystems.Drive;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.Lift;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.SPI;

public class HAL {

	public static Constants constants = new Constants();
	// preferences
	public static boolean isPracticeBot = Constants.isPracticeBot;
	
	// subsystems
	public static AHRS navX = new AHRS(SPI.Port.kMXP);

	public static Drive drive = new Drive(isPracticeBot);
	public static Intake intake = new Intake(isPracticeBot);
	public static IntakeArms intakeArms = new IntakeArms(isPracticeBot);

	public static LiftWrist liftWrist = new LiftWrist(isPracticeBot);
}
