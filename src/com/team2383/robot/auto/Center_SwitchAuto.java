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
			new Waypoint(2.7, 14, 0),
			new Waypoint(11.5, 19, 0)
			//new Waypoint(11.5, 18.2, 0)
			};

	Waypoint[] rightPoints = new Waypoint[] {
			new Waypoint(2.7, 14, 0),
			new Waypoint(11.5, 9, 0)
			//new Waypoint(11.5, 9.10, 0)
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.01, // delta time
			8, // max velocity in ft/s for the motion profile
			7.5, // max acceleration in ft/s/s for the motion profile
			600.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = PathLoader.get(leftPoints, config);
	Trajectory rightTrajectory = PathLoader.get(rightPoints, config);

	public Center_SwitchAuto() {
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitForFMSInfo());
		addSequential(new FollowTrajectory(() -> {
			String positions = Robot.getGameData();

			Trajectory t = (positions.charAt(0) == 'L') ? leftTrajectory : rightTrajectory;

			return t;
		}, true, 0));
		/*
		addSequential(new PrintCommand("trajectory done"));
		addSequential(new SetLiftWrist(LiftWrist.Preset.PORTAL));
		addSequential(new PrintCommand("Unfeeding"));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, 2.0));
		 */
	}
}
