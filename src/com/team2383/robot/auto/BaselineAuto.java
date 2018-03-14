package com.team2383.robot.auto;

import com.team2383.ninjaLib.PathLoader;
import com.team2383.robot.commands.FollowTrajectory;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class BaselineAuto extends CommandGroup {
	Waypoint[] baseline = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(14, 0, 0)
			};

	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
			0.02, 	//delta time
			4.5,		//max velocity in ft/s for the motion profile
			2.5,		//max acceleration in ft/s/s for the motion profile
			5.0);	//max jerk in ft/s/s/s for the motion profile

	Trajectory trajectory = PathLoader.get(baseline, config);

	public BaselineAuto() {
		addSequential(new FollowTrajectory(trajectory));
	}
}
