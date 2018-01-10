package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drivetrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.command.Command;

public class TeleopDrive extends Command {
	private final DoubleSupplier turn;
	private final DoubleSupplier throttle;

	public TeleopDrive(DoubleSupplier throttle, DoubleSupplier turn) {
		super("Teleop Drive");
		requires(drivetrain);
		this.throttle = throttle;
		this.turn = turn;
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		drivetrain.cheesyDrive(throttle.getAsDouble(), turn.getAsDouble());
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		drivetrain.tank(0, 0);
	}

	@Override
	protected void interrupted() {
		drivetrain.tank(0, 0);
	}
}
