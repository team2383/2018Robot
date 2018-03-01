package com.team2383.robot.commands;

import static com.team2383.robot.HAL.lift;

import java.util.function.DoubleSupplier;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class TeleopLiftMotionMagic extends Command {
	private DoubleSupplier speed;
	private double lastTime;

    public TeleopLiftMotionMagic(DoubleSupplier speed) {
		this.speed = speed;
        requires(lift);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	lastTime = 0;
    	lift.setPosition(lift.getCurrentPosition());
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double time = timeSinceInitialized();
    	double dt = time - lastTime;
    	
    	double liftChange = speed.getAsDouble() * Constants.kLift_maxRate;
    	liftChange *= dt; //scale lift rate to the delta time
    	
    	lastTime = time;
    	lift.changePosition(liftChange);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	lift.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
