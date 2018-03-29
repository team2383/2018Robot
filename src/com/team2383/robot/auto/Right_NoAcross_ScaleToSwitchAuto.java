package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.auto.paths.RightPath_RightScale;
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
 * from right, score scale to switch if possible, if not try 2 in scale, if not baseline
 */
public class Right_NoAcross_ScaleToSwitchAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreRightScaleToSwitch = new RightPath_RightScale(PathStyle.SCALE_TO_SWITCH);
	CommandGroup baseline = new All_BaselineAuto(true);
	CommandGroup multiScale = new Right_NoAcross_MultiScaleAuto();

	public Right_NoAcross_ScaleToSwitchAuto() {
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitForFMSInfo());
		/*
		 * if scale and switch are both on right, then run scaleToSwitch. else, try multi scale, else, baseline
		 */
		addSequential(new ConditionalCommand(scoreRightScaleToSwitch, multiScale) {
			@Override 
			protected boolean condition() {
				String positions = Robot.getGameData();
				return positions.charAt(0) == 'R' && positions.charAt(1) == 'R'; //scale and switch on right
			}
		});
	}

	@Override
	public String getDescription() {
		return "Starting right, if RR_ scale to switch, else try 2 in scale, else baseline";
	}
}
