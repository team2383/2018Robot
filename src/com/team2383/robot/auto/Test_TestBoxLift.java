package com.team2383.robot.auto;

import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;
import static com.team2383.robot.HAL.liftWrist;

import com.team2383.robot.commands.FollowTrajectory;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.LiftWrist;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.command.WaitForChildren;
import jaci.pathfinder.Pathfinder;

/**
 *
 */
public class Test_TestBoxLift extends CommandGroup {

    public Test_TestBoxLift() {
    	addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		addSequential(new WaitCommand(1.0));
		addSequential(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_LEVEL));
    }
}
