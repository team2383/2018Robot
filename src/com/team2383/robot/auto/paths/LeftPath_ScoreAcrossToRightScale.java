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

public class LeftPath_ScoreAcrossToRightScale extends CommandGroup {
	Waypoint[] toScalePoints = new Waypoint[] {
			new Waypoint(3.21, 23.1, 0),
			new Waypoint(15.4, 23.1, 0),
			new Waypoint(20, 17, Pathfinder.d2r(-90)),
			new Waypoint(20, 10.5, Pathfinder.d2r(-90)),
			new Waypoint(23.7, 7.2, Pathfinder.d2r(15)),
			};
	
	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(23.7, 7, Pathfinder.d2r(180 + 15)),
			new Waypoint(18.4, 8, Pathfinder.d2r(180 - 10))
			};
	
	Trajectory.Config config_across = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			8, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			5.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory.Config config_second_cube = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			5, // max acceleration in ft/s/s for the motion profile
			30.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config_across);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config_second_cube);
	//Trajectory right_BackToScaleTrajectory = PathLoader.get(left_backToScalePoints, config_second_cube);
	
	public LeftPath_ScoreAcrossToRightScale(PathStyle style) {
		addSequential(new FollowTrajectory(toScaleTrajectory, true));
		addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_DOWN));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_MID, Intake.State.STOP, 0.7));
		addSequential(new SetLiftWrist(LiftWrist.Preset.INTAKE));
		addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 3.0));
		addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 1.7));

		addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(195)));

		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		addSequential(new PrintCommand("Waiting for LiftWrist"));
		
		switch(style) {
			case SCALE_MULTI_CUBE:
				addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH));
				break;
			case SCALE_TO_SWITCH:
				addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH));
				addSequential(new WaitCommand(0.1));
				addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 1.0));
				break;
		}
	}
}