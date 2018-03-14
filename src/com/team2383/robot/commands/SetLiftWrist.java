package com.team2383.robot.commands;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.Constants;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.robot.subsystems.LiftWrist.State;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetLiftWrist extends Command {
	private double timeAtSetpoint;
	private double lastCheck;
	private State state;

    public SetLiftWrist(LiftWrist.State state) {
    	requires(liftWrist);
    	this.state = state;
    }

	@Override
	protected void execute() {
		liftWrist.setState(state);
	}

	@Override
	protected boolean isFinished() {
		if (liftWrist.atTarget()) {
			timeAtSetpoint += this.timeSinceInitialized() - lastCheck;
		} else {
			timeAtSetpoint = 0;
		}
		
		lastCheck = this.timeSinceInitialized();
		return liftWrist.atTarget() && timeAtSetpoint >= Constants.kLiftWrist_SetpointWait;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
	}
}
