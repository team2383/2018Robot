package com.team2383.robot.auto;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Test_CalculateTrackWidthAuto extends Command {

    public Test_CalculateTrackWidthAuto() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	navX.reset();
    	drive.resetEncoders();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	drive.arcade(0, 0.5);
    	double totalTurns = Math.abs(navX.getAngle() / 360.0);
    	double distance = Math.abs(drive.getLeftPosition()) + Math.abs(drive.getRightPosition())/2.0;
    	
    	SmartDashboard.putNumber("calculated trackwidth", distance/(totalTurns * Math.PI));
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
