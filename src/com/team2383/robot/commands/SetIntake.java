package com.team2383.robot.commands;

import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;
import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Intake.State;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.robot.subsystems.LiftWrist.Preset;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetIntake extends Command {
	private boolean isDriveby;
	private Intake.State startState;
	private Intake.State endState;
	private boolean ends;
	
    public SetIntake(Intake.State startState, Intake.State endState) {
		this(startState, endState, false);
	}

    public SetIntake(Intake.State startState, Intake.State endState, boolean ends) {
    	requires(intake);
    	this.startState = startState;
    	this.endState = endState;
    	this.ends = ends;
    }
    
    public SetIntake(Intake.State startState, Intake.State endState, double timeout) {
    	super(timeout);
    	requires(intake);
    	this.startState = startState;
    	this.endState = endState;
    	this.ends = true;
    }

	// Called just before this Command runs the first time
    protected void initialize() {
    	isDriveby = liftWrist.doesWantPreset(Preset.SWITCH_AUTO);
    }

    // Called repeatedly when this Command is schedSuled to run
    protected void execute() {
    	if(isDriveby) {
    		switch(intakeArms.getState()) {
	    		case LEFT:
	    			intake.setState(Intake.State.UNFEED_DRIVEBY_LEFT);
	    			break;
	    			
	    		case RIGHT:
	    			intake.setState(Intake.State.UNFEED_DRIVEBY_RIGHT);
	    			break;
	    			
	    		default:
	    			intake.setState(startState);
	    			break;
	    			
    		}
    	} else {
			intake.setState(startState);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return ends && this.isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    	intake.setState(endState);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
