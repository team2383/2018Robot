package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;

import com.team2383.robot.auto.paths.RightPath_RightScale;
import com.team2383.robot.auto.paths.PathStyle;
import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.ProfiledTurn;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.Lift;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.AutoDescription;
import com.team2383.ninjaLib.PathLoader;
import com.team2383.ninjaLib.WPILambdas;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.WaitForChildren;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * from left, score multiple cubes in scale. 
 * cross if necessary
 */
public class Right_NoAcross_MultiScaleAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreRightScaleMulti = new RightPath_RightScale(PathStyle.SCALE_MULTI_CUBE);
	CommandGroup baseline = new All_BaselineAuto();

	public Right_NoAcross_MultiScaleAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		/*
		 * if its a left scale, score in left scale
		 * else auto run
		 */
		addSequential(new ConditionalCommand(scoreRightScaleMulti, baseline) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(1) == 'R';
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting right, if _R_ multiple cubes in scale, else baseline";
	}
}
