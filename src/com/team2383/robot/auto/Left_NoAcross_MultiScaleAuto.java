package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.auto.paths.LeftPath_LeftScale;
import com.team2383.robot.auto.paths.PathStyle;
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
public class Left_NoAcross_MultiScaleAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreLeftScaleMulti = new LeftPath_LeftScale(PathStyle.SCALE_MULTI_CUBE);
	CommandGroup baseline = new All_BaselineAuto();

	public Left_NoAcross_MultiScaleAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		/*
		 * if its a left scale, score in left scale
		 * else auto run
		 */
		addSequential(new ConditionalCommand(scoreLeftScaleMulti, baseline) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(1) == 'L';
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting left, if _L_ multiple cubes in scale, else baseline";
	}
}
