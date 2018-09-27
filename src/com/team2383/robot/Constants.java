package com.team2383.robot;

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
	 * Intake
	 */
	public static boolean kIntake_InvertLeft = false;
	public static boolean kIntake_InvertRight = false;
	
	public static int kIntake_LeftIntake_ID = 9;
	public static int kIntake_RightIntake_ID = 11;
	
	public static double kIntake_UnfeedPlaceSpeed = 0.3;
	public static double kIntake_UnfeedSlowSpeed = 0.4;
	public static double kIntake_UnfeedMidSpeed = 0.5;
	public static double kIntake_UnfeedFastSpeed = 0.62;


	public static double kIntake_UnfeedDrivebyOpen = 0.4;
	public static double kIntake_UnfeedDrivebyClosed = 0.68;
	
	/*
	 * LiftWrist Constants
	 */

	public static double kLiftWrist_SetpointWait = 0.1;
	
	public static double kLiftWrist_WristDunkClearanceAngle = 210;
	public static double kLiftWrist_WristBackClearanceAngle = 145;

	
	public static double kLiftWrist_LiftBackClearanceHeight = 34;
	
	public static double kLiftWrist_LiftDunkClearanceHeight = 38.7;
	
	/*
	 * Lift Constants
	 */
	public static int kLift_Master_ID = 13;
	public static int kLift_Follower_ID = 14;

	public static double kLift_MaxRate = 20;
	public static double kLift_MaxLiftOpenLoop = 7;
	
	public static double kLift_NudgeAmount = 2;
	
	public static double kLift_P = 0.5;
	public static double kLift_I = 0;
	public static double kLift_D = 0.75;
	
	public static double kLift_F = 0.12;
	public static int kLift_IZone = 0;

	public static double kLift_UpAccelMinError = 25;

	public static int kLift_Cruise_Velocity = 12000;
	public static int kLift_Accel = 16000;

	public static double kLift_Gravity = 0.08;
	
	public static double kLift_AccelErrorThreshold = 12;
	public static double kLift_AccelFeedforward = 0.25;
	
	public static double kLift_Tolerance = 2.0;
	public static boolean kLift_InvertMaster = false;
	public static boolean kLift_InvertFollower = false;
	
	/*
	 * Wrist Constants
	 */

	public static int kWrist_ID = 15;

	public static double kWrist_MaxRate = 120; //degrees/s
	public static double kWrist_MaxWristOpenLoop = 0.5; //%
	
	public static double kWrist_NudgeAmount = 10;
	
	public static double kWrist_P = 0.6;
	public static double kWrist_I = 0.0;
	public static double kWrist_D = 1.5;
	public static double kWrist_F = 0.647; //for 400:1 is 1.214  //for 160:1 is 0.647;
	public static int kWrist_IZone = 0;
	
	public static double kWrist_ForwardSpringMinTo = -0.13;
	public static double kWrist_ForwardSpringMaxTo = -0.01;
	
	public static double kWrist_ForwardGravityMinTo = 0.03;
	public static double kWrist_ForwardGravityMaxTo = 0.11;
	
	public static double kWrist_ReverseGravityMinTo = -0.1;
	public static double kWrist_ReverseGravityMaxTo = -0.28;

	public static int kWrist_Cruise_Velocity = 1800; //deg/s
	public static int kWrist_Accel = 3000; //deg/s

	public static double kWrist_Tolerance = 7.0; //degrees
	public static boolean kWrist_Invert = false;
	
	/*
	 * Climber Constants
	 */

	//todo
	

	/*
	 * Drive Constants
	 */
	public static int kDrive_EncoderTicks = 4096;
	
	public static int kDrive_LeftMaster_ID = 1;
	public static int kDrive_LeftFollowerA_ID = 2;
	public static int kDrive_LeftFollowerB_ID = 3;
	public static int kDrive_LeftFollowerC_ID = 4;


	public static boolean kDrive_InvertLeftMaster = false;
	public static boolean kDrive_InvertLeftA = false;
	public static boolean kDrive_InvertLeftB = false;
	public static boolean kDrive_InvertLeftC = false;

	public static int kDrive_RightMaster_ID = 5;
	public static int kDrive_RightFollowerA_ID = 6;
	public static int kDrive_RightFollowerB_ID = 7;
	public static int kDrive_RightFollowerC_ID = 8;
	
	public static boolean kDrive_InvertRightMaster = false;
	public static boolean kDrive_InvertRightA = false;
	public static boolean kDrive_InvertRightB = false;
	public static boolean kDrive_InvertRightC = false;
	
	public static int kDrive_ContinuousCurrentLimit = 60;
	public static int kDrive_PeakCurrentLimit = 80;
	public static int kDrive_PeakCurrentTime_ms = 100;

	public static double kDrive_WheelDiameterInch = 3.875;
	public static double getWheelCircumference() { return (kDrive_WheelDiameterInch*Math.PI)/12.0; };
	public static double kDrive_EncoderRatio = 1.0/1.0;
	
		/* Drive PID Constants */
		/*
		 * % = motor percent in range [-1.0, 1.0]
		 * s = seconds
		 */
															// units
	public static double kDrive_Motion_P = 1.4;				// %/ft
	public static double kDrive_Motion_D = 0.0;				// %/(ft/s)

	public static double kDrive_Motion_talonP = 0.7;			// %/ft
	public static double kDrive_Motion_talonI = 0.002;			//natives
	public static double kDrive_Motion_talonD = 15;			// %/(ft/s)
	
	//talon V and motio V are shared
	public static double kDrive_Motion_Velocity = 6.0;		// for turn
	public static double kDrive_Motion_Acceleration = 13.0; // for turn

	public static double kDrive_Motion_V = 0.058;			// %/(ft/s) max turn speed
	public static double kDrive_Motion_A = 0.0;				// %/(ft/s/s) max acceleration

	public static double kDrive_Motion_Tolerance = 0.05;// ft
	public static double kDrive_Motion_turnP = 0.0175;
	public static double kDrive_Motion_trackwidth = 2.72;
	
	public static double kDrive_Turn_Tolerance = 1.0;		// degrees
	
	public static double kDrive_peakOutput = 0.8;
	
	/*
	 * General Constants
	 */

	public static double kPidSetpointWait = 0.15;
	
	public static double inputExpo = 0.32;
	public static double inputDeadband = 0.05;

	public static double motorTestMinRotations = 0.5;

	public static int driverControlScheme = OI.Driver_ControlScheme.TARANIS.ordinal();
	public static int operatorControlScheme = OI.Operator_ControlScheme.BUTTONBOARD_RIGHTJOYSTICK.ordinal();
	
	@Override
	public String getFileLocation() {
		return "~/constants.json";
	}
}