package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;


import java.util.function.DoubleSupplier;

import com.team2383.robot.OI;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TeleopDrive extends Command {
	private final DoubleSupplier turn;
	private final DoubleSupplier throttle;

	public TeleopDrive(DoubleSupplier throttle, DoubleSupplier turn) {
		super("Teleop Drive");
		requires(drive);
		this.throttle = throttle;
		this.turn = turn;
	}

	@Override
	protected void initialize() {
		SmartDashboard.putBoolean("Reset Encoders?", false);
	}

	@Override
	protected void execute() {
		drive.arcade(throttle.getAsDouble(), turn.getAsDouble());
		if (OI.driver.getButtonStateA()) {
			drive.resetEncoders();
		}
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		drive.tank(0, 0);
	}

	@Override
	protected void interrupted() {
		drive.tank(0, 0);
	}
}
