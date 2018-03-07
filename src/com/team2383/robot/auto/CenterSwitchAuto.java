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
 *
 */
public class CenterSwitchAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(0, 13.6, 0),
			new Waypoint(8, 20, 0)
			};

	Waypoint[] rightPoints = new Waypoint[] {
			new Waypoint(0, 13.6, 0),
			new Waypoint(8, 7.2, 0)
			};

	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			4.5, // max velocity in ft/s for the motion profile
			2.5, // max acceleration in ft/s/s for the motion profile
			5.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = Pathfinder.generate(leftPoints, config);
	Trajectory rightTrajectory = Pathfinder.generate(rightPoints, config);

	public CenterSwitchAuto() {
		addSequential(WPILambdas.runOnceCommand(() -> lift.setPreset(Lift.Preset.AUTO_SWITCH), true));
		addSequential(new WaitForFMSInfo());
		addSequential(new FollowTrajectory(() -> {
			String positions = DriverStation.getInstance().getGameSpecificMessage();

			Trajectory t = (positions.charAt(0) == 'L') ? leftTrajectory : rightTrajectory;

			return t;
		}));
		addSequential(WPILambdas.createCommand(() -> {
			lift.setPreset(Lift.Preset.AUTO_SWITCH);
			return lift.atTarget();
		}));
		addSequential(intake.setStateCommand(Intake.State.UNFEED, Intake.State.STOP));
	}
}
