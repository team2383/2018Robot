package com.team2383.robot.commands;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class NotifierTest extends Command {
	private Notifier notifier;
	private double lastTime;

    public NotifierTest() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	notifier = new Notifier(this::testFun);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	notifier.startPeriodic(0.01);
    	lastTime = Timer.getFPGATimestamp();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return this.isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    	notifier.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
    
    private void testFun() {
    	double time = Timer.getFPGATimestamp();
    	System.out.println("looper dt: " + (time - lastTime));
    	lastTime = time;
    }
}
