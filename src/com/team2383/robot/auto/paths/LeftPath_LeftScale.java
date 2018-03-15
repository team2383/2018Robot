package com.team2383.robot.auto.paths;

import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;

import com.team2383.ninjaLib.PathLoader;
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

/**
 * Scores into the left scale
 * 	either multiple into left scale
 * 	or one into left scale and one into left switch
 * @author granjef3
 *
 */
public class LeftPath_LeftScale extends CommandGroup {
	/*
	 * _L paths
	 */
	
	Waypoint[] toScalePoints = new Waypoint[] {
			new Waypoint(3.21, 23.1, 0),
			new Waypoint(23.5, 20.5, Pathfinder.d2r(-18))
			};

	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(23.5, 20.5, Pathfinder.d2r(180 - 18)),
			new Waypoint(18.4, 18.6, Pathfinder.d2r(180 - 4))
			};
	
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(18.4, 19.2, 0),
			new Waypoint(23.5, 19.6, Pathfinder.d2r(-24))
			};
	
	Trajectory.Config config_long = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			12, // max velocity in ft/s for the motion profile
			9, // max acceleration in ft/s/s for the motion profile
			20.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory.Config config_second_cube = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			5, // max acceleration in ft/s/s for the motion profile
			30.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config_long);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config_second_cube);
	Trajectory backToScaleTrajectory = PathLoader.get(backToScalePoints, config_second_cube);

	public LeftPath_LeftScale(PathStyle style) {
		addSequential(new FollowTrajectory(toScaleTrajectory, true));

		addSequential(new SetLiftWrist(LiftWrist.State.SCALE_MID_BACK));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_FIRST, Intake.State.STOP, 0.7));
		addSequential(new SetLiftWrist(LiftWrist.State.INTAKE));
		addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 3.0));
		addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 1.7));

		addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(180-18)));

		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		
		switch(style) {
			case SCALE_MULTI_CUBE:
				addParallel(new SetLiftWrist(LiftWrist.State.SCALE_MID_BACK));

				addSequential(new FollowTrajectory(backToScaleTrajectory, true));

				addSequential(new PrintCommand("Waiting for LiftWrist"));
				addSequential(new WaitForChildren());
				addSequential(new WaitCommand(0.1));
				addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_SECOND, Intake.State.STOP, 1.0));
				break;
			case SCALE_TO_SWITCH:
				addSequential(new SetLiftWrist(LiftWrist.State.SWITCH));
				addSequential(new WaitCommand(0.1));
				addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_SECOND, Intake.State.STOP, 1.0));
				break;
		}
	}
}