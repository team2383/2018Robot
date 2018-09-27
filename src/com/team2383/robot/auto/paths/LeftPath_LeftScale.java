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
			new Waypoint(2.7, 27 - 3, 0),
			new Waypoint(24, 27 - 6.7, Pathfinder.d2r(-17))
			};

	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(24, 27 - 6.7, Pathfinder.d2r(180 - 17)),
			new Waypoint(19.1, 27 - 7.2, Pathfinder.d2r(180 + 15)),
			};
	
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(19.1, 27 - 7.2, Pathfinder.d2r(15)),
			new Waypoint(24, 27 - 7.3, Pathfinder.d2r(-19)),
			};
	
	Waypoint[] thirdCubePoints = new Waypoint[] {
			new Waypoint(24, 27 - 7.3, Pathfinder.d2r(180 - 19)),
			new Waypoint(17.6, 27 - 10, Pathfinder.d2r(180 + 45)),
			};
	
	Waypoint[] backToScale2Points = new Waypoint[] {
			new Waypoint(17.6, 27 - 10, Pathfinder.d2r(45)),
			new Waypoint(24, 27 - 8, Pathfinder.d2r(-29)),
			};
	
	Waypoint[] switchForwardPoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(1.0, 0, 0)
			};
	
	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			10, // max velocity in ft/s for the motion profile
			6.5, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_second = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			8, // max velocity in ft/s for the motion profile
			7.8, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_third = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			6.8, // max velocity in ft/s for the motion profile
			6.3, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_forward = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			4, // max velocity in ft/s for the motion profile
			4, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config_second);
	Trajectory backToScaleTrajectory = PathLoader.get(backToScalePoints, config_second);
	Trajectory thirdCubeTrajectory = PathLoader.get(thirdCubePoints, config_third);
	Trajectory backToScale2Trajectory = PathLoader.get(backToScale2Points, config_third);
	Trajectory switchForwardTrajectory = PathLoader.get(switchForwardPoints, config_forward);

	public LeftPath_LeftScale(PathStyle style) {
		addSequential(new WaitCommand(0.1));
		addParallel(new WaitThenCommand(2.4, new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT)));
		addSequential(new FollowTrajectory(toScaleTrajectory, true));
		addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 0.2));
		addSequential(new WaitForChildren());

		addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, true));
		addParallel(new SetLiftWrist(LiftWrist.Preset.INTAKE));
		addParallel(new IntakeOpenArm());
		addSequential(new WaitCommand(0.6)); //wait time before starting second cube trajectory

		addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(180-17)));
		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		
		switch(style) {
		case SCALE_MULTI_CUBE:
			addParallel(new WaitThenCommand(0.6, new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT)));
			addSequential(new FollowTrajectory(backToScaleTrajectory, true, Pathfinder.d2r(15)));

			addSequential(new PrintCommand("Waiting for LiftWrist"));
			addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_MID, Intake.State.STOP, 0.25));

			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, true));
			addParallel(new SetLiftWrist(LiftWrist.Preset.INTAKE));
			addParallel(new IntakeOpenArm2());
			addSequential(new WaitCommand(0.6));
			
			addSequential(new FollowTrajectory(thirdCubeTrajectory, Pathfinder.d2r(180-19)));
			addSequential(new PrintCommand("Waiting for thirdCubeTrajectory"));
			addSequential(new WaitForChildren());
			
			addParallel(new WaitThenCommand(0.8, new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT)));
			addSequential(new FollowTrajectory(backToScale2Trajectory, true, Pathfinder.d2r(45)));
			
			addSequential(new PrintCommand("Waiting for LiftWrist"));
			addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_AUTOSHOT));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 0.3));
			
			addSequential(new SetLiftWrist(LiftWrist.Preset.INTAKE));
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
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 0.85));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 0.7));
			addSequential(new WaitCommand(0.7));//must be same as the intake timeout
		}
	}
	
	private class IntakeOpenArm2 extends CommandGroup {
		public IntakeOpenArm2() {
			addSequential(WPILambdas.createCommand(liftWrist::atTarget));
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 1.0));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 0.8));
			addSequential(new WaitCommand(0.9));//must be same as the intake timeout
		}
	}
}