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
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * score in left scale if its on our side
 */
public class LeftScaleAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(0, 23.1, 0),
			new Waypoint(23.5, 21.5, Pathfinder.d2r(-30))
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			50.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = Pathfinder.generate(leftPoints, config);

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
			addSequential(WPILambdas.createCommand(() -> {
				liftWrist.setState(LiftWrist.State.SCALE_HIGH_BACK);
				return liftWrist.atTarget();
			}));
			addSequential(new WaitCommand(0.8));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 2.0));
		}
	}
}
