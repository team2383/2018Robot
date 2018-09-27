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

public class RightPath_ScoreAcrossToLeftScale extends CommandGroup {
	Waypoint[] toScalePoints = new Waypoint[] {
			new Waypoint(2.7, 3.5, 0),
			new Waypoint(15.4, 3.5, 0),
			new Waypoint(19.8, 8, Pathfinder.d2r(80)),
			new Waypoint(20.5, 17, Pathfinder.d2r(85)),
			new Waypoint(24.5, 19.7, Pathfinder.d2r(-5)),
			};

	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(24.5, 19.7, Pathfinder.d2r(180-5)),
			new Waypoint(20, 19.4, Pathfinder.d2r(180+15))
			};
	
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(20, 19.4, Pathfinder.d2r(15)),
			new Waypoint(24.5, 19.6, Pathfinder.d2r(-5)),
			};
	
	Waypoint[] thirdCubePoints = new Waypoint[] {
			new Waypoint(24.5, 19.6, Pathfinder.d2r(180 - 5)),
			new Waypoint(20, 17, Pathfinder.d2r(180 + 70))
			};
	
	/*
	Waypoint[] secondCubeAcrossPoints = new Waypoint[] {
			new Waypoint(24.3, 19.8, Pathfinder.d2r(180)),
			new Waypoint(20.3, 16.5, Pathfinder.d2r(267)),
			new Waypoint(19.9, 10, Pathfinder.d2r(267)),
			new Waypoint(18.5, 8, Pathfinder.d2r(180))
			};
			*/
	
	Waypoint[] switchForwardPoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(1.0, 0, 0)
			};
	
	Trajectory.Config config_across = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			8, // max velocity in ft/s for the motion profile
			7, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			7, // max velocity in ft/s for the motion profile
			7, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_forward = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			4, // max velocity in ft/s for the motion profile
			4, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config_across);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config);
	Trajectory thirdCubeTrajectory = PathLoader.get(thirdCubePoints, config_forward);
	//Trajectory secondCubeAcrossTrajectory = PathLoader.get(secondCubeAcrossPoints, config_across);
	Trajectory backToScaleTrajectory = PathLoader.get(backToScalePoints, config);
	Trajectory switchForwardTrajectory = PathLoader.get(switchForwardPoints, config_forward);
	

	public RightPath_ScoreAcrossToLeftScale(PathStyle style) {
		addParallel(new WaitThenCommand(4.5, new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT)));
		addSequential(new FollowTrajectory(toScaleTrajectory, true));
		addSequential(new WaitForChildren());
		addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 0.3));
		
		addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, true));
		addParallel(new SetLiftWrist(LiftWrist.Preset.INTAKE));
		addParallel(new IntakeOpenArm());
		addSequential(new WaitCommand(0.9)); //wait time before starting second cube trajectory
		addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(180-5)));
		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		
		switch(style) {
			case SCALE_MULTI_CUBE:
				addParallel(new WaitThenCommand(1.1, new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT)));
				addSequential(new FollowTrajectory(backToScaleTrajectory, true, Pathfinder.d2r(15)));
	
				addSequential(new PrintCommand("Waiting for LiftWrist"));
				
				addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT));

				addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 0.5));

				addSequential(new SetLiftWrist(LiftWrist.Preset.INTAKE));
				/*
				addParallel(new IntakeOpenArm());
				addSequential(new FollowTrajectory(thirdCubeTrajectory, Pathfinder.d2r(180-5)));
				*/
				break;
			case SCALE_TO_SWITCH:
				addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH));
				addSequential(new FollowTrajectory(switchForwardTrajectory));
				addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 1.0));
				break;
		}
	}
	
	private class IntakeOpenArm extends CommandGroup {
		public IntakeOpenArm() {
			addSequential(WPILambdas.createCommand(liftWrist::atTarget));
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 1.5));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 1.3));
			addSequential(new WaitCommand(1.5));//must be same as the intake timeout
		}
	}
}