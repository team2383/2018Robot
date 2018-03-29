package com.team2383.ninjaLib;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class WaitThenCommand extends CommandGroup {
	public WaitThenCommand(double timeout, Command command) {
		addSequential(new WaitCommand(timeout));
		addSequential(command);
	}
}
