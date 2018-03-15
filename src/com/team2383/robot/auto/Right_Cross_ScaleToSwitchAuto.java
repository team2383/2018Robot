package com.team2383.robot.auto;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.auto.paths.RightPath_RightScale;
import com.team2383.robot.auto.paths.RightPath_ScoreAcrossToLeftScale;

import com.team2383.robot.auto.paths.PathStyle;
import com.team2383.robot.commands.WaitForFMSInfo;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.ninjaLib.AutoDescription;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

/**
 * Starting Right, if LL_ OR RR_ do scale to switch, else run multiscale
 */
public class Right_Cross_ScaleToSwitchAuto extends CommandGroup implements AutoDescription {
	CommandGroup scoreRightScaleToSwitch = new RightPath_RightScale(PathStyle.SCALE_TO_SWITCH);
	CommandGroup scoreAcrossToRightScaleToSwitch = new RightPath_ScoreAcrossToLeftScale(PathStyle.SCALE_TO_SWITCH);
	CommandGroup multiScale = new Right_Cross_MultiScaleAuto();
	CommandGroup scaleToSwitch = new ScaleToSwitch();
	
	public Right_Cross_ScaleToSwitchAuto() {
		addSequential(liftWrist.setStateCommand(LiftWrist.State.SWITCH_AUTO, true));
		addSequential(new WaitForFMSInfo());
		/*
		 * if scale and switch are on same site, proceed to crossScaleToSwitch, else run crossMultiScale
		 */
		addSequential(new ConditionalCommand(scaleToSwitch, multiScale) {
			@Override
			protected boolean condition() {
				String positions = DriverStation.getInstance().getGameSpecificMessage();
				return positions.charAt(0) == positions.charAt(1); //scale and switch on same side, so equal to each other
			}
		});
	}
	
	private class ScaleToSwitch extends CommandGroup {
		public ScaleToSwitch() {
			addSequential(new ConditionalCommand(scoreRightScaleToSwitch, scoreAcrossToRightScaleToSwitch) {
				@Override
				protected boolean condition() {
					String positions = DriverStation.getInstance().getGameSpecificMessage();
					return positions.charAt(0) == 'R' && positions.charAt(1) == 'R'; //scale and switch on right, run rightScaleToSwitch
				}
			});
		}
	}
	
	@Override
	public String getDescription() {
		return "Starting Right, if LL_ OR RR_ do scale to switch, else run multicube";
	}
}
