  package com.team2383.robot.commands;

import com.team2383.ninjaLib.WPILambdas;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

/**
 *
 */
public class DeployOutrigger extends CommandGroup {
	Solenoid latch = new Solenoid(1,2);
	
    public DeployOutrigger() {
        addSequential(WPILambdas.runOnceCommand(() -> {
        	System.out.println("BANG!");
        	latch.set(true);
        }, true));
    }
}
