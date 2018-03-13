/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team2383.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.team2383.robot.HAL;
import com.team2383.robot.OI;
import com.team2383.robot.auto.BaselineAuto;
import com.team2383.robot.auto.CalculateTrackWidthAuto;
import com.team2383.robot.auto.CenterSwitchAuto;
import com.team2383.robot.auto.LeftScaleAuto;
import com.team2383.robot.auto.LeftSwitchAuto;
import com.team2383.robot.auto.RightScaleAuto;
import com.team2383.robot.auto.RightSwitchAuto;
import com.team2383.robot.auto.TestDriveMotionMagic;
import com.team2383.robot.auto.TestMotionProfile;

import com.team2383.robot.commands.ProfiledTurn;

import java.lang.reflect.Field;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	Command autoCommand;
	SendableChooser<Command> autoChooser = new SendableChooser<>();

	/*
	 * So wpilib is trying to shoot us in the foot as usual
	 * lets make sure exceptions crash the program so broken code doesn't bork us on the field
	 * 
	 * Keep an eye on wpilib updates for when they come up with a better solution for this.
	 * (non-Javadoc)
	 * @see edu.wpi.first.wpilibj.IterativeRobotBase#loopFunc()
	 */
	@Override
	protected void loopFunc()
	{
	    	try
	    	{
	    		// calls user code
			super.loopFunc();
	    	} // catch all the things
	    	catch(Throwable throwable)
	    	{
	    		DriverStation.reportError("Unhandled exception: " + throwable.toString(),
	    		          throwable.getStackTrace());
	    		System.exit(1); // kill the program so it can restart
		}
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		@SuppressWarnings("unused")
		HAL hal = new HAL();
		@SuppressWarnings("unused")
		OI oi = new OI();
		
		/*
		 * Joystick not available warnings are useless, disable them if coding
		 */
		if (DriverStation.getInstance().getMatchType() == MatchType.None) {
			try {
				Field nmt = DriverStation.getInstance().getClass().getDeclaredField("m_nextMessageTime");
				nmt.setAccessible(true);
				nmt.setDouble(DriverStation.getInstance(), Double.MAX_VALUE);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				DriverStation.reportError("Failed to set nextMessageTime", e.getStackTrace());
			}
		}
		
		CameraServer.getInstance().startAutomaticCapture();
		
		autoChooser = new SendableChooser<Command>();

		autoChooser.addObject("Do Nothing", new InstantCommand());
		autoChooser.addObject("Center Switch Auto", new CenterSwitchAuto());
		autoChooser.addObject("Left Scale Auto", new LeftScaleAuto());
		
		/*
		autoChooser.addObject("Baseline Auto", new BaselineAuto());

		*/

		/*
		autoChooser.addObject("Left Switch Auto", new LeftSwitchAuto());
		autoChooser.addObject("Right Switch Auto", new RightSwitchAuto());
		
		autoChooser.addObject("Left Scale Auto", new LeftScaleAuto());
		autoChooser.addObject("Right Scale Auto", new RightScaleAuto());
		*/

		//autoChooser.addObject("PIT AUTO: Fix Motor Direction", pitMotorTestAuto);


		autoChooser.addObject("Test Motion Profiled 90 right turn", new ProfiledTurn(-180));
		autoChooser.addObject("Test Drive Motion Magic", new TestDriveMotionMagic());
		autoChooser.addObject("Test Motion Profiling Auto", new TestMotionProfile());
		autoChooser.addObject("Calc Trackwidth", new CalculateTrackWidthAuto());
		SmartDashboard.putData("Auto Chooser", autoChooser);
		
		this.setPeriod(0.02);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		autoCommand = autoChooser.getSelected();
		autoCommand = (Command) autoChooser.getSelected();
		if (autoCommand != null) {
			autoCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autoCommand != null) {
			autoCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
