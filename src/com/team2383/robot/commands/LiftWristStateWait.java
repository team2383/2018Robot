package com.team2383.robot.commands;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.robot.subsystems.LiftWrist.State;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class LiftWristStateWait extends Command {
    private State state;

	public LiftWristStateWait(LiftWrist.State state) {
    	super(0.2);
    	requires(liftWrist);
    	this.state = state;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	liftWrist.setState(state);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return liftWrist.getState() == state && liftWrist.atTarget() && this.isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
