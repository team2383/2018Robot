package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;


import java.util.function.DoubleSupplier;

import com.team2383.robot.OI;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;

public class TeleopDrive extends Command {
	private final DoubleSupplier turn;
	private final DoubleSupplier throttle;
	private boolean gyroHomeSet;

	public TeleopDrive(DoubleSupplier throttle, DoubleSupplier turn) {
		super("Teleop Drive");
		requires(drive);
		this.throttle = throttle;
		this.turn = turn;
		this.gyroHomeSet = false;
	}

	@Override
	protected void initialize() {
		SmartDashboard.putBoolean("Reset Encoders?", false);
	}

	@Override
	protected void execute() {
		double turnAdj = 0;
		
		/*

		if (turn.getAsDouble() <= 0.05 && throttle.getAsDouble() > 0.3) {
			if (!gyroHomeSet) {
				navX.reset();
				gyroHomeSet = true;
			}
			double gyro_heading = navX.getYaw();    // Assuming the gyro is giving a value in degrees
			
			double angleDifference = Pathfinder.boundHalfDegrees(-gyro_heading);
			turnAdj = -0.05 * angleDifference;
		} else {
			gyroHomeSet = false;
			turnAdj = 0;
		}
		
		*/
		
		SmartDashboard.putNumber("Drive TurnAdj", turnAdj);
		
		drive.arcade(throttle.getAsDouble(), turn.getAsDouble()-turnAdj);
		
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
