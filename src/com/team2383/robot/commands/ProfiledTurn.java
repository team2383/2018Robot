package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;

import com.team2383.robot.Constants;
import com.team2383.robot.subsystems.Drive.Motion;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;

/**
 *
 */
public class ProfiledTurn extends Command {

	double goalAngle;
	double timeAtSetpoint;
	double lastCheck;
	
    public ProfiledTurn(double degrees) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(drive);
    	
    	goalAngle = degrees;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	navX.zeroYaw();
    	drive.resetEncoders();
    	timeAtSetpoint = 0;
    	lastCheck = 0;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double currentAngle = -navX.getAngle();
    	double differenceAngle = Math.toRadians(Pathfinder.boundHalfDegrees(goalAngle-currentAngle));
    	double distance = (Constants.kDrive_Motion_trackwidth * differenceAngle) / 2.0;
    	SmartDashboard.putNumber("Difference Angle", differenceAngle);
    	SmartDashboard.putNumber("ERROR", Math.abs(goalAngle - -navX.getAngle()));
    	SmartDashboard.putNumber("Current Angle", currentAngle);
    	SmartDashboard.putNumber("Distance", distance);
    	
    	Motion m = drive.getMotion();
    	
    	drive.position((-distance) + m.leftPosition, (distance) + m.rightPosition);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (Math.abs(goalAngle - -navX.getAngle()) < Constants.kDrive_Turn_Tolerance) {
			timeAtSetpoint += this.timeSinceInitialized() - lastCheck;
		} else {
			timeAtSetpoint = 0;
		}
		lastCheck = this.timeSinceInitialized();
		return timeAtSetpoint >= 0.1;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("DONE!");
    	drive.tank(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
