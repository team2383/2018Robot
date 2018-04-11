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
 * Scores into the right scale
 * 	either multiple into right scale
 * 	or one into right scale and one into right switch
 * @author granjef3
 *
 */
public class RightPath_RightScale extends CommandGroup {
	/*
	 * _R paths
	 */
	
	Waypoint[] toScalePoints = new Waypoint[] {
			new Waypoint(3.21, 3.5, 0),
			new Waypoint(24, 6.7, Pathfinder.d2r(15))
			};

	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(24, 6.7, Pathfinder.d2r(180 + 15)),
			new Waypoint(19, 8, Pathfinder.d2r(180)),
			};
	
	/*
	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(20.9, 5.87, Pathfinder.d2r(180 + 15)),
			new Waypoint(17, 8.1, Pathfinder.d2r(180)),
			new Waypoint(16.3, 8.1, Pathfinder.d2r(180))
			};
			*/
	
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(19, 7, Pathfinder.d2r(-15)),
			new Waypoint(24, 6.4, Pathfinder.d2r(10)),
			};
	
	/*
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(19.41, 8, 0),
			new Waypoint(24, 6.7, Pathfinder.d2r(15))
			};
			*/
	
	Waypoint[] switchForwardPoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(1.0, 0, 0)
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			12, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_forward = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			4, // max velocity in ft/s for the motion profile
			4, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory toScaleTrajectory = PathLoader.get(toScalePoints, config);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config);
	Trajectory backToScaleTrajectory = PathLoader.get(backToScalePoints, config);
	Trajectory switchForwardTrajectory = PathLoader.get(switchForwardPoints, config_forward);

	public RightPath_RightScale(PathStyle style) {
		addParallel(new WaitThenCommand(2.4, new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_DOWN)));
		addSequential(new FollowTrajectory(toScaleTrajectory, true));
		addSequential(new WaitForChildren());
		addSequential(intake.setStateCommand(Intake.State.UNFEED_MID, Intake.State.STOP, 0.3));

		addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, true));
		addParallel(new SetLiftWrist(LiftWrist.Preset.INTAKE));
		addParallel(new IntakeOpenArm());
		addSequential(new WaitCommand(0.9)); //wait time before starting second cube trajectory
		addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(180+15)));
		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		
		switch(style) {
			case SCALE_MULTI_CUBE:
				addParallel(new WaitThenCommand(0.9, new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_DOWN)));
				addSequential(new FollowTrajectory(backToScaleTrajectory, true, Pathfinder.d2r(-15)));
	
				addSequential(new PrintCommand("Waiting for LiftWrist"));
				
				addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_DUNK_BACK_30));

				addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 0.5));
				addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 0.5));

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
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 1.3));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 0.7));
			addSequential(new WaitCommand(1.0));//must be same as the intake timeout
		}
	}
}
