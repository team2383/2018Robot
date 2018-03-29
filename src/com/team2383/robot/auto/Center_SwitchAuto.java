package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;

import com.team2383.robot.Robot;
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
import edu.wpi.first.wpilibj.command.PrintCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 *
 */
public class Center_SwitchAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(3.21, 13.6, 0),
			new Waypoint(11.5, 18.2, 0)
			};

	Waypoint[] rightPoints = new Waypoint[] {
			new Waypoint(3.21, 13.6, 0),
			new Waypoint(11.5, 9.10, 0)
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			5, // max acceleration in ft/s/s for the motion profile
			30.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = PathLoader.get(leftPoints, config);
	Trajectory rightTrajectory = PathLoader.get(rightPoints, config);

	public Center_SwitchAuto() {
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitForFMSInfo());
		addSequential(new FollowTrajectory(() -> {
			String positions = Robot.getGameData();

			Trajectory t = (positions.charAt(0) == 'L') ? leftTrajectory : rightTrajectory;

			return t;
		}));
		addSequential(new PrintCommand("trajectory done"));
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO));
		addSequential(new PrintCommand("Unfeeding"));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_STARTING, Intake.State.STOP, 2.0));
	}
}
