package com.team2383.robot.commands;

import static com.team2383.robot.HAL.prefs;

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
		this.modifier = new TankModifier(trajectory).modify(prefs.getDouble("trackwidth", 2.72));
		modifier.modify(prefs.getDouble("trackwidth", 2.72));
		
		leftFollower = new PathFollower(modifier.getLeftTrajectory());
		rightFollower = new PathFollower(modifier.getRightTrajectory());
		
		leftFollower.configurePIDVA(prefs.getDouble("kDrive_Motion_P", 1.0),
									0.0,
									prefs.getDouble("kDrive_Motion_D", 0.0),
									prefs.getDouble("kDrive_Motion_V", 1.0/14.0),
									prefs.getDouble("kDrive_Motion_A", 1.0/10.0));
		rightFollower.configurePIDVA(prefs.getDouble("kDrive_Motion_P", 1.0),
									0.0,
									prefs.getDouble("kDrive_Motion_D", 0.0),
									prefs.getDouble("kDrive_Motion_V", 1.0/14.0),
									prefs.getDouble("kDrive_Motion_A", 1.0/10.0));
		

		leftFollower.reset();
		
		rightFollower.reset();
		drive.resetEncoders();
		navX.reset();
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
		
		double gyro_heading = navX.getAngle();    // Assuming the gyro is giving a value in degrees
		double desired_heading = -Pathfinder.r2d(leftFollower.getHeading());  // Should also be in degrees, make sure its in phase

		angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);
		
		double turn = 1.0 * prefs.getDouble("kDrive_Motion_TurnP", 0.0125) * angleDifference;
		
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
		navX.reset();
	}

	@Override
	protected void interrupted() {
		end();
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