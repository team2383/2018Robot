package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;

import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroTurn extends PIDCommand {

	private double timeAtSetpoint;
	private double lastCheck;
	private final double tolerance;
	private final double wait;
	private boolean finish = true;
	private static final double turnVel = 1.0;

	public GyroTurn(double angle) {
		this(turnVel, angle);
	}

	public GyroTurn(double angle, boolean finish) {
		this(turnVel, angle);
		this.finish = false;
	}

	public GyroTurn(double velocity, double angle) {
		this(velocity, angle, turnVel);
	}

	public GyroTurn(double velocity, double angle, double tolerance) {
		this(velocity, angle, tolerance, turnVel);
	}

	public GyroTurn(double velocity, double angle, double tolerance, double wait) {
		super("Set Heading", 0.020, 0.0, 0.03);
		requires(drive);
		this.getPIDController().reset();
		this.getPIDController().setInputRange(-180.0, 180.0);
		this.getPIDController().setOutputRange(-velocity, velocity);
		this.getPIDController().setContinuous();
		this.getPIDController().setSetpoint(angle);
		this.tolerance = tolerance;
		this.wait = wait;
		SmartDashboard.putData("Turn Controller", this.getPIDController());
	}

	@Override
	protected void initialize() {
		navX.reset();
	}

	@Override
	protected void execute() {
		/*
		 * IZone check
		 */
		if (Math.abs(this.getPIDController().getError()) <= 10) {
			this.getPIDController().setPID(0.020, 0.003, 0.03);
		} else if(Math.abs(this.getPIDController().getError()) <= 1.0) {
			this.getPIDController().setPID(0.020, 0.0, 0.03);
		}
	}

	@Override
	protected boolean isFinished() {
		if (Math.abs(this.getPIDController().getError()) <= tolerance) {
			timeAtSetpoint += this.timeSinceInitialized() - lastCheck;
		} else {
			timeAtSetpoint = 0;
		}
		SmartDashboard.putNumber("error", this.getPIDController().getError());
		SmartDashboard.putNumber("Tolerance", tolerance);
		SmartDashboard.putNumber("timeAtSetpoint", timeAtSetpoint);
		lastCheck = this.timeSinceInitialized();
		return finish && timeAtSetpoint >= wait;
	}

	@Override
	protected void end() {
		drive.tank(0, 0);
	}

	@Override
	protected void interrupted() {
		drive.tank(0, 0);
	}

	@Override
	protected double returnPIDInput() {
		return navX.getYaw();
	}

	@Override
	protected void usePIDOutput(double output) {
		if (this.timeSinceInitialized() > 0.1) {
			drive.tank(output, -output);
		} else {
			System.out.println("Waiting for reset");
		}
	}

}