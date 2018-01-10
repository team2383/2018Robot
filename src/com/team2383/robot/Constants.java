package com.team2383.robot;

import java.util.LinkedList;

public class Constants {
	
	/*
	 * Fangs Constants
	 */
	public static final int kFangsTalonID = 9;
	public static final double fangsReverseLimit = 0;
	public static final double fangsForwardLimit = 0;
	
	/*
	 * Feeder Constants
	 */
	
	public static final int kFeederTalonID = 10;
	
	
	/*
	 * Climber Constants
	 */
	public static final int kClimberTalonID = 11;

	

	/*
	 * Drive Constants
	 */
	public static final int kLeftMasterTalonID = 1;
	public static final int kLeftFollowerATalonID = 2;
	public static final int kLeftFollowerBTalonID = 3;
	public static final int kLeftFollowerCTalonID = 4;

	public static final int kRightMasterTalonID = 5;
	public static final int kRightFollowerATalonID = 6;
	public static final int kRightFollowerBTalonID = 7;
	public static final int kRightFollowerCTalonID = 8;
	
	
	public static double kDriveWheelDiameter = 3.95;
	public static double kDriveWheelCircumference = kDriveWheelDiameter * Math.PI;
	public static double kDriveEncoderScalar = 20.0/42.0;
	public static double kDriveInchesPerDegree = kDriveWheelCircumference / 360.0;
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