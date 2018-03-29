package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.auto.paths.RightPath_RightScale;
import com.team2383.robot.auto.paths.RightPath_ScoreAcrossToLeftScale;
import com.team2383.robot.Robot;
import com.team2383.robot.auto.paths.PathStyle;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.AutoDescription;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

/**
 * from right, score multiple cubes in scale. 
 * cross if necessary
 */
public class Right_Cross_MultiScaleAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreRightScaleMulti = new RightPath_RightScale(PathStyle.SCALE_MULTI_CUBE);
	CommandGroup scoreAcrossToLeftScaleMulti = new RightPath_ScoreAcrossToLeftScale(PathStyle.SCALE_MULTI_CUBE);
	
	public Right_Cross_MultiScaleAuto() {
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitForFMSInfo());
		/*
		 * pick 
		 */
		addSequential(new ConditionalCommand(scoreRightScaleMulti, scoreAcrossToLeftScaleMulti) {
			@Override
			protected boolean condition() {
				String positions = Robot.getGameData();
				return positions.charAt(1) == 'R';
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting right, cross if necessary, multiple cubes in scale";
	}
}