package com.team2383.robot.auto;

import static com.team2383.robot.HAL.drive;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Test_DriveMotionMagic extends Command {

    public Test_DriveMotionMagic() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	drive.resetEncoders();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	drive.position(5.0, 5.0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return this.timeSinceInitialized() > 1.0 && (drive.leftMaster.getClosedLoopError(0) < 1.0/12.0) && (drive.rightMaster.getClosedLoopError(0) < 1.0/12.0);
    }

    // Called once after isFinished returns true
    protected void end() {
    	drive.tank(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
