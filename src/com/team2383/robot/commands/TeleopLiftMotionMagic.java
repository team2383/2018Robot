package com.team2383.robot.commands;

import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.prefs;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TeleopLiftMotionMagic extends Command {
	private double maxLiftRate; //inch per second
	private DoubleSupplier speed;
	private double lastTime;

    public TeleopLiftMotionMagic(DoubleSupplier speed) {
		this.speed = speed;
        requires(lift);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	lastTime = 0;
    	maxLiftRate = prefs.getDouble("kLift_maxRate", 10);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	maxLiftRate = prefs.getDouble("kLift_maxRate", 10);
    	double dt = timeSinceInitialized() - lastTime;
    	double liftChange = speed.getAsDouble() * maxLiftRate;
    	liftChange /= dt; //scale lift rate to the delta time
    	
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
