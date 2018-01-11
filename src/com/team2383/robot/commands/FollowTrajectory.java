package com.team2383.robot.commands;

import static com.team2383.robot.HAL.drivetrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.DistanceFollower;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

import static com.team2383.robot.HAL.drivetrain;
import static com.team2383.robot.HAL.navX;

public class FollowTrajectory extends Command {
	DistanceFollower leftFollower;
	DistanceFollower rightFollower;
	
	public FollowTrajectory(Trajectory leftTrajectory, Trajectory rightTrajectory) {
		super("Follow Trajectory");
		requires(drivetrain);
		leftFollower = new DistanceFollower(leftTrajectory);
		rightFollower = new DistanceFollower(rightTrajectory);
		
		leftFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / 2, 0);
		rightFollower.configurePIDVA(1.0, 0.0, 0.0, 1 / 2, 0);
		
		navX.reset();
	}

	@Override
	protected void initialize() {
	}

	@Override
	protected void execute() {
		double leftOutput = leftFollower.calculate(drivetrain.getLeftFeet());
		double rightOutput = rightFollower.calculate(drivetrain.getRightFeet());
		
		double gyro_heading = navX.getYaw();    // Assuming the gyro is giving a value in degrees
		double desired_heading = Pathfinder.r2d(leftModifier.getHeading());  // Should also be in degrees

		double angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
		double turn = 0.8 * (-1.0/80.0) * angleDifference;
		
		drivetrain.tank(leftOutput + turn, rightOutput + turn);
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
