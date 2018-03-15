package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.SetLiftWrist;
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
 * score in opposite
 */
public class RightScaleAcrossAuto extends CommandGroup {
	Waypoint[] rightPoints = new Waypoint[] {
			new Waypoint(3.21, 3.9, 0),
			new Waypoint(15.4, 3.9, 0),
			new Waypoint(20, 10, Pathfinder.d2r(90)),
			new Waypoint(20, 16.5, Pathfinder.d2r(90)),
			new Waypoint(25, 19.7, 0),
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			8, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			5.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory rightTrajectory = PathLoader.get(rightPoints, config);

	public RightScaleAcrossAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		addSequential(new ConditionalCommand(new ScoreRightScale(), new BaselineAuto()) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(1) == 'L';
			}
		});
	}
	
	private class ScoreRightScale extends CommandGroup {
		public ScoreRightScale() {
			addSequential(new FollowTrajectory(rightTrajectory, true));
			addSequential(new SetLiftWrist(LiftWrist.State.SCALE_HIGH_BACK));
			addSequential(new WaitCommand(0.8));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 2.0));
		}
	}
}
