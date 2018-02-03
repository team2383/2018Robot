package com.team2383.robot.commands;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;

import com.team2383.ninjaLib.PathFollower;
import com.team2383.robot.Constants;

public class FollowTrajectory extends Command implements Sendable  {
	PathFollower leftFollower;
	PathFollower rightFollower;
	
	public FollowTrajectory(Trajectory leftTrajectory, Trajectory rightTrajectory) {
		super("Follow Trajectory");
		requires(drive);

		leftFollower = new PathFollower(leftTrajectory);
		rightFollower = new PathFollower(rightTrajectory);
		
		leftFollower.configurePIDVA(Constants.kDrive_Motion_P, 0.0, Constants.kDrive_Motion_D, Constants.kDrive_Motion_V, Constants.kDrive_Motion_A);
		rightFollower.configurePIDVA(Constants.kDrive_Motion_P, 0.0, Constants.kDrive_Motion_D, Constants.kDrive_Motion_V, Constants.kDrive_Motion_A);
	}

	@Override
	protected void initialize() {
		leftFollower.reset();
		rightFollower.reset();
		drive.resetEncoders();
		navX.reset();
	}

	@Override
	protected void execute() {
		double leftOutput = leftFollower.calculate(drive.getMotion().leftPosition);
		double rightOutput = rightFollower.calculate(drive.getMotion().rightPosition);
		
		double gyro_heading = navX.getYaw();    // Assuming the gyro is giving a value in degrees
		double desired_heading = Pathfinder.r2d(-leftFollower.getHeading());  // Should also be in degrees, make sure its in phase

		double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
		double turn = 0.8 * (-1.0/80.0) * angleDifference;

		SmartDashboard.putNumber("MP Target Left Position (ft)", leftFollower.getSegment().position);
		SmartDashboard.putNumber("MP Target Left Velocity (ft/s)", leftFollower.getSegment().velocity);

		SmartDashboard.putNumber("MP Target Right Position (ft)", rightFollower.getSegment().position);
		SmartDashboard.putNumber("MP Target Right Velocity (ft/s)", rightFollower.getSegment().velocity);
		
		SmartDashboard.putNumber("MP Target Heading", leftFollower.getSegment().heading);
		
		SmartDashboard.putNumber("MP Left Output (%)", leftOutput);
		SmartDashboard.putNumber("MP Right Output (%)", rightOutput);
		SmartDashboard.putNumber("MP Heading Adj. Output (%)", turn);
		
		drive.tank(leftOutput + turn, rightOutput - turn);
	}

	@Override
	protected boolean isFinished() {
		return leftFollower.isFinished() && rightFollower.isFinished();
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
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Follower");
	    builder.setSafeState(this::initialize);
	    builder.addDoubleProperty("p", this.leftFollower::getKp, (kP) -> {
	    	this.leftFollower.setKp(kP);
	    	this.rightFollower.setKp(kP);
	    });
	    builder.addDoubleProperty("d", this.leftFollower::getKd, (kD) -> {
	    	this.leftFollower.setKd(kD);
	    	this.rightFollower.setKd(kD);
	    });
	    builder.addDoubleProperty("v", this.leftFollower::getKv, (kV) -> {
	    	this.leftFollower.setKv(kV);
	    	this.rightFollower.setKv(kV);
	    });
	    builder.addDoubleProperty("a", this.leftFollower::getKa, (kA) -> {
	    	this.leftFollower.setKa(kA);
	    	this.rightFollower.setKa(kA);
	    });
	    builder.addDoubleProperty("setpoint", () -> this.leftFollower.getSegment().position, (x) -> {});
	    super.initSendable(builder);
	}
}