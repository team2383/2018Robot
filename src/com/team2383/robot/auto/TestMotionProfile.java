package com.team2383.robot.auto;

import static com.team2383.robot.HAL.prefs;

import com.team2383.robot.commands.FollowTrajectory;

import edu.wpi.first.wpilibj.command.CommandGroup;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class TestMotionProfile extends CommandGroup {
	Waypoint[] points = new Waypoint[] {
			new Waypoint(0,0,0),
			new Waypoint(9,5,0)
			/*
			 * 
			 * BASIC SWITCH LEFT PATH
		    new Waypoint(0, 13.6, 0),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
		    new Waypoint(3, 13.6, 10),
		    new Waypoint(6, 17, 50),
		    new Waypoint(12, 18, 0)
		    */
		};

	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
			0.02, 	//delta time
			4.5,		//max velocity in ft/s for the motion profile
			7,		//max acceleration in ft/s/s for the motion profile
			120.0);	//max jerk in ft/s/s/s for the motion zprofile

	Trajectory trajectory = Pathfinder.generate(points, config);

	public TestMotionProfile() {
		addSequential(new FollowTrajectory(trajectory));
	}
}