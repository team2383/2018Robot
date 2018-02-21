package com.team2383.robot.commands;

import static com.team2383.robot.HAL.lift;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Moves until canceled or times out
 *
 * 
 *
 */

public class MoveLift extends Command {
	private final DoubleSupplier liftPower;

	public MoveLift(DoubleSupplier liftPower) {
		super("Move Lift");
		requires(lift);
		this.liftPower = liftPower;
	}

	public MoveLift(DoubleSupplier liftPower, double timeout) {
		super("Move Turret", timeout);
		requires(lift);
		this.liftPower = liftPower;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		lift.moveAtSpeed(liftPower.getAsDouble());
	}

	@Override
	protected boolean isFinished() {
		return this.isTimedOut();
	}

	@Override
	protected void end() {
		lift.stop();
	}

	@Override
	protected void interrupted() {
		lift.stop();
	}

}