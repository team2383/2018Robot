/*package com.team2383.robot.auto;

import com.team2383.robot.commands.FollowTrajectory;

import edu.wpi.first.wpilibj.command.CommandGroup;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class TestAuto extends CommandGroup {
	Waypoint[] points = new Waypoint[] {
		    new Waypoint(0, -13, 0),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
		    new Waypoint(9, -13, 0)
		};

	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.02, 3, 6, 196.0);
	Trajectory trajectory = Pathfinder.generate(points, config);
	TankModifier modifier = new TankModifier(trajectory).modify(2.365);

	public TestAuto() {
		addSequential(new FollowTrajectory(modifier.getLeftTrajectory(), modifier.getRightTrajectory()));
	}
}
*/