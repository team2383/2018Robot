package com.team2383.ninjaLib;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class StatefulSubsystem<StateT extends Enum<StateT>> extends Subsystem {
	protected StateT state;
	protected Supplier<Subsystem> instanceSupplier;
	
	private class setStateCommand extends Command {
		StateT startState;
		StateT endState;
		boolean ends;

		public setStateCommand(StateT state, boolean ends) {
			super(0);
			requires(instanceSupplier.get());
			this.startState = state;
			this.endState = state;
			this.ends = ends;
		}

		public setStateCommand(StateT state, double timeout) {
			super(timeout);
			this.ends = true;
			this.startState = state;
			this.endState = state;
		}
		
		public setStateCommand(StateT startState, StateT endState, boolean ends) {
			super(0);
			this.startState = startState;
			this.endState = endState;
			this.ends = ends;
		}
		
		public setStateCommand(StateT startState, StateT endState, double timeout) {
			super(timeout);
			this.ends = true;
			this.startState = startState;
			this.endState = endState;
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
	        return ends && this.isTimedOut();
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
	
	/**
	 * end by default
	 * @param state
	 * @return
	 */
	public Command setStateCommand(StateT state) {
		return new setStateCommand(state, false);
	}
	
	public Command setStateCommand(StateT state, boolean ends) {
		return new setStateCommand(state, ends);
	}

	public Command setStateCommand(StateT state, double timeout) {
		return new setStateCommand(state, timeout);
	}
	
	/**
	 * end by default
	 * @param state
	 * @return
	 */
	public Command setStateCommand(StateT startState, StateT endState) {
		return new setStateCommand(startState, endState, false);
	}
	
	public Command setStateCommand(StateT startState, StateT endState, boolean ends) {
		return new setStateCommand(startState, endState, ends);
	}

	public Command setStateCommand(StateT startState, StateT endState, double timeout) {
		return new setStateCommand(startState, endState, timeout);
	}
}
