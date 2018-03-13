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
	boolean backwards;
	
	public FollowTrajectory(Supplier<Trajectory> trajectorySupplier) {
		this(trajectorySupplier, false);
	}
	
	public FollowTrajectory(Trajectory trajectory) {
		this(() -> trajectory, false);
	}
	
	public FollowTrajectory(Trajectory trajectory, boolean backwards) {
		this(() -> trajectory, backwards);
	}
	
	public FollowTrajectory(Supplier<Trajectory> trajectorySupplier, boolean backwards) {
		super("Follow Trajectory");

		this.trajectorySupplier = trajectorySupplier;
		this.backwards = backwards;
		
		requires(drive);
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
		
		double leftOutput;
		double rightOutput;
		//forwards
		if (!backwards) {
			leftOutput = leftFollower.calculate(drive.getMotion().leftPosition);
			rightOutput = rightFollower.calculate(drive.getMotion().rightPosition);
		} else {
			//backwards
			leftOutput = leftFollower.calculate(-drive.getMotion().rightPosition); //left = -right
			rightOutput = rightFollower.calculate(-drive.getMotion().leftPosition); //right = -left
		}
		
		double gyro_heading = -navX.getAngle(); //axis is the same
		
		double desired_heading = Pathfinder.r2d(leftFollower.getHeading());  // Should also be in degrees, make sure its in phase

		SmartDashboard.putNumber("MP1 gyro_heading", gyro_heading);
		SmartDashboard.putNumber("MP2 desired_heading", desired_heading);
		SmartDashboard.putNumber("MP3 error", desired_heading-gyro_heading);
		SmartDashboard.putNumber("MP6 turnP", Constants.kDrive_Motion_turnP);
		
		angleDifference = Pathfinder.boundHalfDegrees(desired_heading - gyro_heading);

		SmartDashboard.putNumber("MP4 angleDiff", angleDifference);
		
		double turn = Constants.kDrive_Motion_turnP * angleDifference;
		SmartDashboard.putNumber("MP Left Output (%)", leftOutput);
		SmartDashboard.putNumber("MP Right Output (%)", rightOutput);
		SmartDashboard.putNumber("MP Left Output BCKWRDS (%)", -rightOutput);
		SmartDashboard.putNumber("MP Right Output BCKWRDS(%)", -leftOutput);
		SmartDashboard.putNumber("MP5 Heading Adj. Output (%)", turn);
	
			//forwards
		if (!backwards) {
			drive.tank(leftOutput - turn, rightOutput + turn);
		} else {
			//backwards
			/*.tank is forwards, so (fwd_left, fwd_right)
			 * back_left = -fwd_right
			 * 	so fwd_right = -back_left
			 * back_right = -fwd_left
			 * 	so fwd_left = -back_right
			 * 
			 * turn input is relative to true (fwd) drivetrain output, not the actual direction
			 * so fwd_left(back_right) has to be less negative (going slower) then fwd_right(back_left), when turning right (negative turn)
			 */
			
			drive.tank(-rightOutput - turn, -leftOutput + turn);
		}
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