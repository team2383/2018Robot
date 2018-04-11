package com.team2383.robot.auto;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.FollowTrajectoryTalon;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class Test_MeanMotionProfile extends CommandGroup {
	Waypoint[] points = new Waypoint[] {
			new Waypoint(0,0,0),
			new Waypoint(15,0,0)
	};
			
	Waypoint[] points2 = new Waypoint[] {
					new Waypoint(0,0,0),
					new Waypoint(15,0,0)
			/*
			 * 
			 * BASIC SWITCH LEFT PATH
		    new Waypoint(0, 13.6, 0),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
		    new Waypoint(3, 13.6, 10),
		    new Waypoint(6, 17, 50),
		    new Waypoint(12, 18, 0)
		    */
		};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			14, // max velocity in ft/s for the motion profile
			7, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory trajectory = Pathfinder.generate(points, config);
	Trajectory trajectory2 = Pathfinder.generate(points2, config);

	public Test_MeanMotionProfile() {
		addSequential(new FollowTrajectoryTalon(trajectory, false));
	}
}