package com.team2383.robot.auto;

import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.intake;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Lift;
import com.team2383.ninjaLib.WPILambdas;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * score in left switch if its on our side
 */
public class LeftSwitchAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(0, 23, 0),
			new Waypoint(7.5, 25, 0),
			new Waypoint(14, 21, -90),
			};
	
	Waypoint[] baseline = new Waypoint[] {
			new Waypoint(0, 23, 0),
			new Waypoint(14, 23, 0)
			};

	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			4.5, // max velocity in ft/s for the motion profile
			2.5, // max acceleration in ft/s/s for the motion profile
			5.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = Pathfinder.generate(leftPoints, config);
	Trajectory baseTrajectory = Pathfinder.generate(baseline, config);

	public LeftSwitchAuto() {
		addSequential(WPILambdas.runOnceCommand(() -> lift.setPreset(Lift.Preset.AUTO_SWITCH), true));
		addSequential(new WaitForFMSInfo());
		addSequential(new FollowTrajectory(() -> {
			String positions = DriverStation.getInstance().getGameSpecificMessage();

			/*
			 * if its on our side, run left trajectory, otherwise run baseline
			 */
			Trajectory t = (positions.charAt(0) == 'L') ? leftTrajectory : baseTrajectory;

			return t;
		}));
		addSequential(WPILambdas.createCommand(() -> {
			lift.setPreset(Lift.Preset.AUTO_SWITCH);
			return lift.atTarget();
		}));
		addSequential(intake.setStateCommand(Intake.State.UNFEED, Intake.State.STOP));
	}
}
