package com.team2383.robot.auto;

import java.io.File;

import com.team2383.robot.commands.FollowTrajectory;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

public class TestAuto extends CommandGroup{
	File leftFile = new File("testauto_left_detailed.csv");
	File rightFile = new File("testauto_right_detailed.csv");
	Trajectory leftTrajectory = Pathfinder.readFromCSV(leftFile);
	Trajectory rightTrajectory = Pathfinder.readFromCSV(rightFile);
	
	public TestAuto(){
		addSequential(new FollowTrajectory(leftTrajectory, rightTrajectory));
	}

}
