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
 * from left, score scale to switch if possible, if not try 2 in scale, if not baseline
 */
public class Left_NoAcross_ScaleToSwitchAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreLeftScaleToSwitch = new LeftPath_LeftScale(PathStyle.SCALE_TO_SWITCH);
	CommandGroup baseline = new All_BaselineAuto();
	CommandGroup multiScale = new Left_NoAcross_MultiScaleAuto();

	public Left_NoAcross_ScaleToSwitchAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		/*
		 * if scale and switch are both on left, then run scaleToSwitch. else, try multi scale, else, baseline
		 */
		addSequential(new ConditionalCommand(scoreLeftScaleToSwitch, multiScale) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(0) == 'L' && positions.charAt(1) == 'L'; //scale and switch on left
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting left, if LL_ scale to switch, else try 2 in scale, else baseline";
	}
}
