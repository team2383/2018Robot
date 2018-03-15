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
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.command.WaitForChildren;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * score in opposite scale from left
 */
public class LeftScaleAcrossAuto extends CommandGroup {
	Waypoint[] leftPoints = new Waypoint[] {
			new Waypoint(3.21, 23.1, 0),
			new Waypoint(15.4, 23.1, 0),
			new Waypoint(20, 17, Pathfinder.d2r(-90)),
			new Waypoint(20, 10.5, Pathfinder.d2r(-90)),
			new Waypoint(24, 7.2, Pathfinder.d2r(15)),
			};
	
	Waypoint[] secondCubePoints = new Waypoint[] {
			new Waypoint(24, 7, Pathfinder.d2r(195)),
			new Waypoint(18.4, 8, Pathfinder.d2r(170))
			};

	Trajectory.Config config = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			8, // max velocity in ft/s for the motion profile
			10, // max acceleration in ft/s/s for the motion profile
			5.0); // max jerk in ft/s/s/s for the motion profile
	
	Trajectory.Config config_second = new Trajectory.Config(
			Trajectory.FitMethod.HERMITE_QUINTIC,
			Trajectory.Config.SAMPLES_HIGH,
			0.02, // delta time
			5, // max velocity in ft/s for the motion profile
			5, // max acceleration in ft/s/s for the motion profile
			30.0); // max jerk in ft/s/s/s for the motion profile

	Trajectory leftTrajectory = PathLoader.get(leftPoints, config);
	Trajectory secondCubeTrajectory = PathLoader.get(secondCubePoints, config_second);

	public LeftScaleAcrossAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		addSequential(new ConditionalCommand(new ScoreAcrossToRightScale(), new BaselineAuto()) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(1) == 'R';
			}
		});
		addSequential(new ConditionalCommand(new ScoreRightSwitch(), new BaselineAuto()) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(0) == 'R';
			}
		});
	}
	
	private class ScoreAcrossToRightScale extends CommandGroup {
		public ScoreAcrossToRightScale() {
			addSequential(new FollowTrajectory(leftTrajectory, true));
			addSequential(new SetLiftWrist(LiftWrist.State.SCALE_MID_BACK));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_AUTO_SCALE_FIRST, Intake.State.STOP, 0.7));
		}
	}
	
	private class ScoreRightSwitch extends CommandGroup {
		public ScoreRightSwitch() {
			addSequential(new SetLiftWrist(LiftWrist.State.INTAKE));
			addParallel(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, 3.0));
			addParallel(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, 1.7));

			addSequential(new FollowTrajectory(secondCubeTrajectory, Pathfinder.d2r(195)));

			addSequential(new PrintCommand("Waiting for secondCubeTrajectory"));
			addSequential(new WaitForChildren());
			addSequential(new PrintCommand("Waiting for LiftWrist"));
			addSequential(new SetLiftWrist(LiftWrist.State.SWITCH));
			addSequential(new WaitCommand(0.1));
			addSequential(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, 1.0));
		}
	}
}
