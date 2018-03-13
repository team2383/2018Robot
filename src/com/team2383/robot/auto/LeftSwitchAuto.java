package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.ProfiledTurn;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Lift;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.PathLoader;
import com.team2383.ninjaLib.WPILambdas;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * score in left switch if its on our side
 */
public class LeftSwitchAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(0, 0, 0),
			new Waypoint(14, 0, 0)
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			50.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = PathLoader.get(leftPoints, config);

	public LeftSwitchAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		addSequential(new ConditionalCommand(new ScoreLeftSwitch(), new BaselineAuto()) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(0) == 'L';
			}
		});
	}
	
	private class ScoreLeftSwitch extends CommandGroup {
		public ScoreLeftSwitch() {
			addSequential(new FollowTrajectory(leftTrajectory));
			addSequential(WPILambdas.createCommand(() -> {
				liftWrist.setState(LiftWrist.State.SWITCH_AUTO);
				return liftWrist.atTarget();
			}));
			addSequential(new ProfiledTurn(-90));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_SWITCHAUTO, Intake.State.STOP, 2.0));
		}
	}
}
