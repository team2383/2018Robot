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
public class Test_TestDriveByRight extends CommandGroup {

    public Test_TestDriveByRight() {
		addSequential(new WaitCommand(1.0));
		addSequential(new SetLiftWrist(LiftWrist.Preset.SWITCH_DRIVE_BY));
		addSequential(intakeArms.setStateCommand(IntakeArms.State.RIGHT, true));
		addSequential(new WaitCommand(0.5));
		addSequential(intake.setStateCommand(Intake.State.UNFEED_DRIVEBY_RIGHT, 1.0));
    }
}
