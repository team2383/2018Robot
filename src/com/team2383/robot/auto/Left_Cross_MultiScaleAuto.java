package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.Robot;
import com.team2383.robot.auto.paths.LeftPath_LeftScale;
import com.team2383.robot.auto.paths.LeftPath_ScoreAcrossToRightScale;
import com.team2383.robot.auto.paths.PathStyle;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.AutoDescription;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

/**
 * from left, score multiple cubes in scale. 
 * cross if necessary
 */
public class Left_Cross_MultiScaleAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreLeftScaleMulti = new LeftPath_LeftScale(PathStyle.SCALE_MULTI_CUBE);
	CommandGroup scoreAcrossToRightScaleMulti = new LeftPath_ScoreAcrossToRightScale(PathStyle.SCALE_MULTI_CUBE);
	
	public Left_Cross_MultiScaleAuto() {
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitForFMSInfo());
		/*
		 * if its a left scale, score in left scale
		 * else score across to the right scale
		 */
		addSequential(new ConditionalCommand(scoreLeftScaleMulti, scoreAcrossToRightScaleMulti) {
			@Override
			protected boolean condition() {
				String positions = Robot.getGameData();
				return positions.charAt(1) == 'L';
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting left, cross if necessary, multiple cubes in scale";
	}
}