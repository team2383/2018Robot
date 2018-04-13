package com.team2383.robot.auto.paths;

import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;
import static com.team2383.robot.HAL.liftWrist;

import com.team2383.ninjaLib.PathLoader;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.ninjaLib.WaitThenCommand;
import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.LiftWrist;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.command.WaitForChildren;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class RightPath_LeftScale extends CommandGroup {
	Waypoint[] toScalePoints = new Waypoint[] {
			new Waypoint(3.25, 3.7, 0),
			new Waypoint(15.4, 3.7, 0),
			new Waypoint(19.9, 10, Pathfinder.d2r(87)),
			new Waypoint(20.3, 16.5, Pathfinder.d2r(87)),
			new Waypoint(24.3, 19.8, Pathfinder.d2r(0)),
			};

	Trajectory.Config config_across = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			8, // max velocity in ft/s for the motion profile
			8, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config_across);

	public RightPath_LeftScale() {
		addParallel(new WaitThenCommand(5.4, new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_DOWN)));
		addSequential(new FollowTrajectory(toScaleTrajectory, true));
		addSequential(new WaitForChildren());
		addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 0.3));
	}
}