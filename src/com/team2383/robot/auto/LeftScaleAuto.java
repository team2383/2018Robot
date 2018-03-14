package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.ProfiledTurn;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.Lift;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.PathLoader;
import com.team2383.ninjaLib.WPILambdas;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.WaitForChildren;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * score in left scale if its on our side
 */
public class LeftScaleAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(3.21, 23.1, 0),
			new Waypoint(23.5, 20.5, Pathfinder.d2r(-18))
			};

	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(4.0, 2.8, Pathfinder.d2r(40))
			};
	
	Waypoint[] backToScalePoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(3.85, -3.15, Pathfinder.d2r(-22))
			};

	Trajectory.Config config_long = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			12, // max velocity in ft/s for the motion profile
			9, // max acceleration in ft/s/s for the motion profile
			20.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			12, // max velocity in ft/s for the motion profile
			9, // max acceleration in ft/s/s for the motion profile
			20.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory leftTrajectory = PathLoader.get(leftPoints, config_long);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config);
	Trajectory backToScaleTrajectory = PathLoader.get(backToScalePoints, config);

	public LeftScaleAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		addSequential(new ConditionalCommand(new ScoreLeftScale(), new BaselineAuto()) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(1) == 'L';
			}
		});
	}
	
	private class ScoreLeftScale extends CommandGroup {
		public ScoreLeftScale() {
			addSequential(new FollowTrajectory(leftTrajectory, true));

			addSequential(new SetLiftWrist(LiftWrist.State.SCALE_MID_BACK));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_FIRST, Intake.State.STOP, 1.0));
			addSequential(new SetLiftWrist(LiftWrist.State.INTAKE));
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 2.3));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 2.0));

			addSequential(new FollowTrajectory(secondCubeTrajectory));

			addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
			addSequential(new WaitForChildren());
			addParallel(new SetLiftWrist(LiftWrist.State.SCALE_MID_BACK));

			addSequential(new FollowTrajectory(backToScaleTrajectory, true));

			addSequential(new PrintCommand("Waiting for LiftWrist"));
			addSequential(new WaitForChildren());
			addSequential(new WaitCommand(0.1));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_SECOND, Intake.State.STOP, 1.0));
		}
	}
}
