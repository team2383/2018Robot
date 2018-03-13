package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.LiftWristStateWait;
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
public class CenterSwitchAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(0, 13.4, 0),
			new Waypoint(9, 17.5, 0)
			};

	Waypoint[] rightPoints = new Waypoint[] {
			new Waypoint(0, 13.6, 0),
			new Waypoint(9, 9, 0)
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			50.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = Pathfinder.generate(leftPoints, config);
	Trajectory rightTrajectory = Pathfinder.generate(rightPoints, config);

	public CenterSwitchAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		addSequential(new FollowTrajectory(() -> {
			String positions = DriverStation.getInstance().getGameSpecificMessage();

			Trajectory t = (positions.charAt(0) == 'L') ? leftTrajectory : rightTrajectory;

			return t;
		}));
		addSequential(new PrintCommand("trajectory done"));
		addSequential(new LiftWristStateWait(LiftWrist.State.SWITCH_AUTO));
		addSequential(new PrintCommand("Unfeeding"));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_SWITCHAUTO, Intake.State.STOP, 2.0));
	}
}
