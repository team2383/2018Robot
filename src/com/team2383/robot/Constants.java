package com.team2383.robot;

import java.util.LinkedList;

public class Constants {

	/*
	 * Feeder and Shooter
	 */
	public static final int kIntake_LeftFeederTalonID = 9;
	public static final int kIntake_LeftShooterTalonID = 10;
	
	public static final int kIntake_RightFeederTalonID = 11;
	public static final int kIntake_RightShooterTalonID = 12;
	
	
	/*
	 * Lift Constants
	 */
	public static final int kLift_LeftTalonID = 13;
	public static final int kLift_RightTalonID = 14;                                                                                      
	
	/*
	 * Climber Constants
	 */

	//todo
	

	/*
	 * Drive Constants
	 */
	public static final int kDrive_EncoderTicks = 4096;
	
	public static final int kDrive_LeftMasterTalonID = 1;
	public static final int kDrive_LeftFollowerATalonID = 2;
	public static final int kDrive_LeftFollowerBTalonID = 3;
	public static final int kDrive_LeftFollowerCTalonID = 4;

	public static final int kDrive_RightMasterTalonID = 5;
	public static final int kDrive_RightFollowerATalonID = 6;
	public static final int kDrive_RightFollowerBTalonID = 7;
	public static final int kDrive_RightFollowerCTalonID = 8;
	
	
	public static double kDrive_WheelDiameterInch = 4.0;
	public static double kDrive_WheelCircumferenceInch = kDrive_WheelDiameterInch * Math.PI;
	public static double kDrive_EncoderRatio = 1.0/1.0;
	public static double kDrive_InchesPerDegree = kDrive_WheelCircumferenceInch / 360.0;
	public static double kDrive_FeetPerDegree = kDrive_InchesPerDegree / 12.0;
	
		/* Drive PID Constants */
		/*
		 * % = motor percent in range [-1.0, 1.0]
		 * s = seconds
		 */
															// units
	public static double kDrive_Motion_P = 1.5;				// %/ft
	public static double kDrive_Motion_D = 0.0;				// %/(ft/s)
	public static double kDrive_Motion_V = 1/10.0;			// %/(ft/s) max turn speed
	public static double kDrive_Motion_A = 0.0;				// %/(ft/s/s) max acceleration
	public static double kDrive_Motion_Tolerance = 1.0/12.0;// ft
	
	public static double kDrive_Turn_Tolerance = 1.0;		// degrees
	public static double kDrive_Turn_P = 0.015;				// %/degree
	public static double kDrive_Turn_I = 0.003;				// %/(degree*s)
	public static double kDrive_Turn_D = 0.03;				// %/(degree/s)
	public static double kDrive_Turn_IZone = 10;			// degrees
	public static double kDrive_Turn_Velocity = 1.0;		// %

	public static double kDrive_Heading_P = 0.04;			// %/degree
	public static double kDrive_Heading_I = 0.000;			// %/(degree*s)
	public static double kDrive_Heading_D = 0.03;			// %/(degree/s)
	public static double kDrive_Heading_F = 0;				// %/(degree/s) max turn speed
	
	public static double kPidSetpointWait = 0.15;
	
	public static double inputExpo = 0.32;
	public static double inputDeadband = 0.05;
}