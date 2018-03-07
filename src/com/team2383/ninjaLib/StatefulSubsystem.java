package com.team2383.ninjaLib;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class StatefulSubsystem<StateT extends Enum<StateT>> extends Subsystem {
	private StateT state;
	
	private class setStateCommand extends Command {
		StateT startState;
		StateT endState;

		public setStateCommand(StateT state) {
			super(0);
			this.startState = state;
			this.endState = state;
		}

		public setStateCommand(StateT state, double timeout) {
			super(timeout);
			this.startState = state;
			this.endState = state;
		}
		
		public setStateCommand(StateT startState, StateT endState) {
			super(0);
			this.startState = startState;
		}
		
		public setStateCommand(StateT startState, StateT endState, double timeout) {
			super(timeout);
			this.startState = startState;
		}

		// Called just before this Command runs the first time
	    protected void initialize() {
	    	_setState(startState);
	    }

	    // Called repeatedly when this Command is scheduled to run
	    protected void execute() {
	    	_setState(startState);
	    }

	    // Make this return true when this Command no longer needs to run execute()
	    protected boolean isFinished() {
	        return this.isTimedOut();
	    }

	    // Called once after isFinished returns true
	    protected void end() {
	    	_setState(endState);
	    }

	    // Called when another command which requires one or more of the same
	    // subsystems is scheduled to run
	    protected void interrupted() {
	    	end();
	    }
	}
	
	public abstract void setState(StateT state);
	
	private void _setState(StateT state) {
		this.state = state;
		setState(state);
	}
	
	public StateT getState() {
		return this.state;
	}
	
	public Command setStateCommand(StateT state) {
		return new setStateCommand(state);
	}

	public Command setStateCommand(StateT state, double timeout) {
		return new setStateCommand(state, timeout);
	}
	
	public Command setStateCommand(StateT startState, StateT endState) {
		return new setStateCommand(startState, endState);
	}

	public Command setStateCommand(StateT startState, StateT endState, double timeout) {
		return new setStateCommand(startState, endState, timeout);
	}
}
