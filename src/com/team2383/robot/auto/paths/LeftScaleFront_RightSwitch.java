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

public class LeftScaleFront_RightSwitch extends CommandGroup {
	Waypoint[] toSwitchPoints = new Waypoint[] {
			new Waypoint(23.6, 19.8, Pathfinder.d2r(180)),
			new Waypoint(19.6, 16.5, Pathfinder.d2r(290)),
			new Waypoint(16, 10.4, Pathfinder.d2r(180))
			};
	
	Waypoint[] switchForwardPoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(1.0, 0, 0)
			};

	Trajectory.Config config_across = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			7.2, // max velocity in ft/s for the motion profile
			8.5, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_forward = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			4, // max velocity in ft/s for the motion profile
			4, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory toSwitchTrajectory = PathLoader.get(toSwitchPoints, config_across);
	Trajectory switchForwardTrajectory = PathLoader.get(switchForwardPoints, config_forward);

	public LeftScaleFront_RightSwitch() {
		addParallel(new SetLiftWrist(LiftWrist.Preset.INTAKE));
		addParallel(new WaitThenCommand(2.65, new IntakeOpenArm()));
		addSequential(new WaitCommand(0.7)); //wait time before starting right switch trajectory
		addSequential(new FollowTrajectory(toSwitchTrajectory, Pathfinder.d2r(180)));
		addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
		addSequential(new WaitForChildren());
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH));
		addSequential(new FollowTrajectory(switchForwardTrajectory));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 1.0));
	}
	
	private class IntakeOpenArm extends CommandGroup {
		public IntakeOpenArm() {
			addSequential(WPILambdas.createCommand(liftWrist::atTarget));
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 3.0));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 2.4));
			addSequential(new WaitCommand(3.0));//must be same as the intake timeout
		}
	}
}