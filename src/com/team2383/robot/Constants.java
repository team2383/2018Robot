package com.team2383.robot;

import java.util.LinkedList;

public class Constants {

	/*
	 * Feeder and Shooter
	 */
	public static final int kLeftFeederTalonID = 9;
	public static final int kLeftShooterTalonID = 10;
	
	public static final int kRightFeederTalonID = 11;
	public static final int kRightShooterTalonID = 12;
	
	
	/*
	 * Lift Constants
	 */
	public static final int kLeftLiftTalonID = 13;
	public static final int kRightLiftTalonID = 14;                                                                                      
	
	/*
	 * Climber Constants
	 */

	

	/*
	 * Drive Constants
	 */
	public static final int kDriveEncoderTicks = 4096;
	
	public static final int kLeftMasterTalonID = 1;
	public static final int kLeftFollowerATalonID = 2;
	public static final int kLeftFollowerBTalonID = 3;
	public static final int kLeftFollowerCTalonID = 4;

	public static final int kRightMasterTalonID = 5;
	public static final int kRightFollowerATalonID = 6;
	public static final int kRightFollowerBTalonID = 7;
	public static final int kRightFollowerCTalonID = 8;
	
	
	public static double kDriveWheelDiameterInch = 4.0;
	public static double kDriveWheelCircumferenceInch = kDriveWheelDiameterInch * Math.PI;
	public static double kDriveEncoderRatio = 1.0/1.0;
	public static double kDriveInchesPerDegree = kDriveWheelCircumferenceInch / 360.0;
	public static double kDriveFeetPerDegree = kDriveInchesPerDegree / 12.0;

	public static double kDriveTurnTolerance = 1.0;
	public static double kDriveTurnP = 0.015;
	public static double kDriveTurnI = 0.003; //0.01
	public static double kDriveTurnD = 0.03;  //0.4
	public static double kDriveTurnIZone = 10;
	public static double kDriveTurnVelocity = 1.0;

	public static double kDriveHeadingMaintainTolerance = 0.0;
	public static double kDriveHeadingMaintainP = 0.04;
	public static double kDriveHeadingMaintainI = 0.000;
	public static double kDriveHeadingMaintainD = 0.03;
	public static double kDriveHeadingMaintainF = 0;

	public static double kDrivePositionTolerance = 0.5; //0.75
	public static double kDrivePositionP = 0.09;   //0.345
	public static double kDrivePositionI = 0.01; //0.02
	public static double kDrivePositionD = 0.4;  //0
	public static double kDrivePositionIZone = kDrivePositionTolerance * 4.0;
	public static double kDrivePositionF = 0;

	public static double kDriveVelocityP = 0.0;
	public static double kDriveVelocityI = 0.0;
	public static double kDriveVelocityD = 0.0;
	public static double kDriveVelocityF = 0;
	public static int kDriveVelocityIZone = 50;
	
	public static double kPidSetpointWait = 0.15;
	
	public static double kFangPositionP = 0.1;   
	public static double kFangPositionI = 0.00;
	public static double kFangPositionD = 0.0; 
	public static double kFangPositionF = 0.0;
	public static int kFangPositionIZone = 0;

	public static double inputExpo = 0.32;
	public static double inputDeadband = 0.05;
}