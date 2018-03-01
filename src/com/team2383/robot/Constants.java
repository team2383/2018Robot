package com.team2383.robot;

import java.util.LinkedList;

import com.team2383.ninjaLib.ConstantsBase;

/**
 * any constants set in here are defaults, and will be overridden by the json file on the bot
 * @author granjef3
 *
 */
public class Constants extends ConstantsBase {

	/*
	 * global constants
	 */
	public static boolean isPracticeBot = false;

	/*
	 * Feeder and Shooter
	 */
	public static int kIntake_LeftFeederTalonID = 9;
	public static int kIntake_LeftShooterTalonID = 10;
	
	public static int kIntake_RightFeederTalonID = 11;
	public static int kIntake_RightShooterTalonID = 12;
	
	public static int kIntake_PivotUp = 6;
	public static int kIntake_PivotDown = 7;
	
	/*
	 * Lift Constants
	 */
	public static int kLift_LeftTalonID = 13;
	public static int kLift_RightTalonID = 14;

	public static double kLift_maxRate = 6;
	public static double kLift_maxLiftOpenLoop = 7;

	public static int kLift_MM_Accel = 4000;
	public static int kLift_MM_Cruise_Velocity = 6000;

	public static double kLift_tolerance = 1.0;
	public static boolean kLift_InvertMaster = false;
	public static boolean kLift_InvertFollower = false;
	
	/*
	 * Climber Constants
	 */

	//todo
	

	/*
	 * Drive Constants
	 */
	public static int kDrive_EncoderTicks = 4096;
	
	public static int kDrive_LeftMasterTalonID = 1;
	public static int kDrive_LeftFollowerATalonID = 2;
	public static int kDrive_LeftFollowerBTalonID = 3;
	public static int kDrive_LeftFollowerCTalonID = 4;


	public static boolean kDrive_InvertLeftMaster = false;
	public static boolean kDrive_InvertLeftA = false;
	public static boolean kDrive_InvertLeftB = false;
	public static boolean kDrive_InvertLeftC = false;

	public static int kDrive_RightMasterTalonID = 5;
	public static int kDrive_RightFollowerATalonID = 6;
	public static int kDrive_RightFollowerBTalonID = 7;
	public static int kDrive_RightFollowerCTalonID = 8;
	
	public static boolean kDrive_InvertRightMaster = false;
	public static boolean kDrive_InvertRightA = false;
	public static boolean kDrive_InvertRightB = false;
	public static boolean kDrive_InvertRightC = false;
	
	public static int kDrive_continuousCurrentLimit = 60;
	public static int kDrive_peakCurrentLimit = 80;
	public static int kDrive_peakCurrentTime_ms = 100;

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
	public static double kDrive_Motion_V = 1/17.0;			// %/(ft/s) max turn speed
	public static double kDrive_Motion_A = 1/10;				// %/(ft/s/s) max acceleration
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

	public static double kDrive_trackwidth = 1.41;
	
	
	/*
	 * General Constants
	 */

	public static double kPidSetpointWait = 0.15;
	
	public static double inputExpo = 0.32;
	public static double inputDeadband = 0.05;

	public static double motorTestMinRotations = 0.5;
	
	@Override
	public String getFileLocation() {
		return "~/constants.json";
	}
}