package com.team2383.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.navX;

import com.team2383.ninjaLib.PathFollower;
import com.team2383.robot.Constants;

public class FollowTrajectory extends Command implements Sendable  {
	PathFollower leftFollower;
	PathFollower rightFollower;
	Supplier<Trajectory> trajectorySupplier;
	Trajectory trajectory;
	TankModifier modifier;
	double angleDifference;
	
	public FollowTrajectory(Supplier<Trajectory> trajectorySupplier) {
		super("Follow Trajectory");

		this.trajectorySupplier = trajectorySupplier;
		
		requires(drive);
	}
	
	public FollowTrajectory(Trajectory trajectory) {
		this(() -> trajectory);
	}

	@Override
	protected void initialize() {
		this.trajectory = trajectorySupplier.get();
		this.modifier = new TankModifier(trajectory).modify(Constants.kDrive_Motion_trackwidth);
		modifier.modify(Constants.kDrive_Motion_trackwidth);
		
		leftFollower = new PathFollower(modifier.getLeftTrajectory());
		rightFollower = new PathFollower(modifier.getRightTrajectory());
		
		leftFollower.configurePIDVA(Constants.kDrive_Motion_P,
				0.0,
				Constants.kDrive_Motion_D,
				Constants.kDrive_Motion_V,
				Constants.kDrive_Motion_A);

		rightFollower.configurePIDVA(Constants.kDrive_Motion_P,
				0.0,
				Constants.kDrive_Motion_D,
				Constants.kDrive_Motion_V,
				Constants.kDrive_Motion_A);
		

		leftFollower.reset();
		rightFollower.reset();
		drive.resetEncoders();
    	navX.zeroYaw();
	}

	@Override
	protected void execute() {
		SmartDashboard.putNumber("MP Target Left Position (ft)", leftFollower.getSegment().position);
		SmartDashboard.putNumber("MP Target Left Velocity (ft-s)", leftFollower.getSegment().velocity);

		SmartDashboard.putNumber("MP Target Right Position (ft)", rightFollower.getSegment().position);
		SmartDashboard.putNumber("MP Target Right Velocity (ft-s)", rightFollower.getSegment().velocity);
		
		SmartDashboard.putNumber("MP Target Heading", leftFollower.getSegment().heading);
		
		double leftOutput = leftFollower.calculate(drive.getMotion().leftPosition);
		double rightOutput = rightFollower.calculate(drive.getMotion().rightPosition);
		
		double gyro_heading = -navX.getAngle();    // Assuming the gyro is giving a value in degrees
		double desired_heading = Pathfinder.r2d(leftFollower.getHeading());  // Should also be in degrees, make sure its in phase

		angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
		
		double turn = 1.0 * Constants.kDrive_Motion_turnP * angleDifference;
		
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
		leftFollower.reset();
		rightFollower.reset();
		drive.resetEncoders();
    	navX.zeroYaw();
	}

	@Override
	protected void interrupted() {
		end();
	}
}