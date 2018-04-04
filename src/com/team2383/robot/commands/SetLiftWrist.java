package com.team2383.robot.commands;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.Constants;
import com.team2383.robot.subsystems.LiftWrist.Preset;

import edu.wpi.first.wpilibj.command.Command;

public class SetLiftWrist extends Command {
	private double desiredLiftPos;
	private double desiredWristPos;
	private double timeAtSetpoint;
	private double lastCheck;
	boolean waitForGoal;

	public SetLiftWrist(Preset preset) {
		super(0);
		requires(liftWrist);
		this.setInterruptible(true);
		this.desiredLiftPos = preset.liftP.liftPosition;
		this.desiredWristPos = preset.wristP.wristPosition;
		this.waitForGoal = true;
	}
	
	public SetLiftWrist(Preset preset, boolean waitForGoal) {
		super(0);
		requires(liftWrist);
		this.setInterruptible(true);
		this.desiredLiftPos = preset.liftP.liftPosition;
		this.desiredWristPos = preset.wristP.wristPosition;
		this.waitForGoal = waitForGoal;
	}
	
	public SetLiftWrist(double liftPos, double wristPos) {
		super(0);
		requires(liftWrist);
		this.setInterruptible(true);
		this.desiredLiftPos = liftPos;
		this.desiredWristPos = wristPos;
		this.waitForGoal = false;
	}
	
	public SetLiftWrist(double liftPos, double wristPos, boolean waitForGoal) {
		super(0);
		requires(liftWrist);
		this.setInterruptible(true);
		this.desiredLiftPos = liftPos;
		this.desiredWristPos = wristPos;
		this.waitForGoal = waitForGoal;
	}

	// Called just before this Command runs the first time
    protected void initialize() {
    	timeAtSetpoint = 0;
    	lastCheck = 0;
    	System.out.println("Now Wants lift:" +desiredLiftPos+"Wants Wrist:"+desiredWristPos);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	liftWrist.wantsLift(desiredLiftPos);
    	liftWrist.wantsWrist(desiredWristPos);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if(!waitForGoal) {
    		//not waiting for goal
    		return true;
    	}
    	
       	if (liftWrist.atTarget()) {
			timeAtSetpoint += this.timeSinceInitialized() - lastCheck;
		} else {
			timeAtSetpoint = 0;
		}
		
		lastCheck = this.timeSinceInitialized();
		return liftWrist.atTarget() && timeAtSetpoint >= Constants.kLiftWrist_SetpointWait;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}