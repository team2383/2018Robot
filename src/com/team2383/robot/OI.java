package com.team2383.robot;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import com.team2383.ninjaLib.ButtonBoard;
import com.team2383.ninjaLib.DPadButton;
import com.team2383.ninjaLib.DPadButton.Direction;
import com.team2383.ninjaLib.Gamepad;
import com.team2383.ninjaLib.LambdaButton;
import com.team2383.ninjaLib.OnChangeButton;
import com.team2383.ninjaLib.Values;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.commands.SetIntake;
import com.team2383.robot.commands.SetLiftWrist;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakeArms;
import com.team2383.robot.subsystems.Lift;
import com.team2383.robot.subsystems.LiftWrist;
import com.team2383.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.NetworkButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakeArms;
import static com.team2383.robot.HAL.liftWrist;



/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

/*
 * OI Controls:
 * 
 *  Advanced Operator Controls: 
 *  Left Trigger: Fangs Down
 *  Right Trigger: Fangs Up
 *  
 *  Left Trigger: Feed Out
 *	Right Trigger: Feed In
 *
 *	X: Climb
 *
 *	Left Stick Y : Forwards/Backwards
 *	Right Stick X : Left/Right 
 */
@SuppressWarnings("unused")
public class OI {
	private static DoubleUnaryOperator deadband = (x) -> {
		return Math.abs(x) > Constants.inputDeadband ? x : 0;
	};
	
	public static Joystick operatorStick = new Joystick(3);
	
	public static DoubleSupplier throttle;
	public static DoubleSupplier turn;
	public static DoubleSupplier manualSpeed = () -> (operatorStick.getY());
	
	public static Button closeArms = new DPadButton(operatorStick, DPadButton.Direction.DOWN);
	public static Button openArms = new DPadButton(operatorStick, DPadButton.Direction.UP);
	public static Button leftArms = new DPadButton(operatorStick, DPadButton.Direction.LEFT);
	public static Button rightArms = new DPadButton(operatorStick, DPadButton.Direction.RIGHT);
	
	public static Button feed = new JoystickButton(operatorStick, 1);
	public static Button liftManualMotionMagic_thumb = new JoystickButton(operatorStick, 2);
	public static Button place = new JoystickButton(operatorStick, 3);
	public static Button slow = new JoystickButton(operatorStick, 4);
	public static Button mid = new JoystickButton(operatorStick, 5);
	public static Button fast = new JoystickButton(operatorStick, 6);
	
	public static Button liftManualOutput = new JoystickButton(operatorStick, 7);
	public static Button wristManualOutput = new JoystickButton(operatorStick, 8);
	
	public static Button liftManualMotionMagic = new JoystickButton(operatorStick, 9);
	public static Button wristManualMotionMagic = new JoystickButton(operatorStick, 10);

	public static Button liftManualZero = new JoystickButton(operatorStick, 11);
	public static Button wristManualZero  = new JoystickButton(operatorStick, 12);
	
	public static Button updateMotorControllers = new NetworkButton("SmartDashboard", "Update Motor Controllers");
	
	public enum Driver_ControlScheme {
		XBOX,
		TARANIS,
	}
	
	public enum Operator_ControlScheme {
		TWO_JOYSTICK,
		BUTTONBOARD_RIGHTJOYSTICK
	}
	
	public OI() {
		//init the button
		SmartDashboard.putBoolean("Update Motor Controllers", false);

		Driver_ControlScheme d_cs = Driver_ControlScheme.values()[Constants.driverControlScheme];
		
		switch(d_cs) {
			default:
			//taranis emulates the xbox now
			case TARANIS:
			case XBOX:
				System.out.println("xbox/taranis");
				Gamepad driver = new Gamepad(0);
				
				turn = () -> (driver.getRightX());
				throttle = () -> (-driver.getLeftY());
				
				Button driveStraight = new JoystickButton(driver, Gamepad.BUTTON_Y);
				Button feed = new JoystickButton(driver, Gamepad.BUTTON_SHOULDER_RIGHT);
				Button precision = new LambdaButton(driver::getLeftTriggerClick);
				
				feed.whileHeld(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, false));
				//precision.whileHeld(new PrecisionDrive());
				break;
		}
		
		Operator_ControlScheme o_cs = Operator_ControlScheme.values()[Constants.operatorControlScheme];
		
		switch(o_cs) {
			case TWO_JOYSTICK:
				System.out.println("2joy");
				setupTwoJoy();
				break;
			default:
			case BUTTONBOARD_RIGHTJOYSTICK:
				System.out.println("buttonboard");
				setupButtonBoard();
				break;
		}
		
		//2nd stick controls
		feed.whileHeld(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, false));
		place.whileHeld(intake.setStateCommand(Intake.State.UNFEED_PLACE, Intake.State.STOP, false));
		slow.whileHeld(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, false));
		mid.whileHeld(intake.setStateCommand(Intake.State.UNFEED_MID, Intake.State.STOP, false));
		fast.whileHeld(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, false));
		
		closeArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.CLOSED));
		openArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.OPEN));
		leftArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.LEFT));
		rightArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.RIGHT));
		
		liftManualOutput.whenPressed(WPILambdas.runOnceCommand(liftWrist::setManualLift, true));
		wristManualOutput.whenPressed(WPILambdas.runOnceCommand(liftWrist::setManualWrist, true));
		
		liftManualZero.whenPressed(WPILambdas.runOnceCommand(liftWrist::setLiftZero, true));
		wristManualZero.whenPressed(WPILambdas.runOnceCommand(liftWrist::setWristZero, true));
		
		updateMotorControllers.whenReleased(WPILambdas.runOnceCommand(() -> {
			liftWrist.configMotorControllers(10);
			drive.configMotorControllers(10);
			}, true));
	}
	
	private void setupTwoJoy() {
		Joystick operator = new Joystick(1);
		Button presetDriveby = new JoystickButton(operator, 3);
		Button presetScaleLow = new JoystickButton(operator, 4);

		Button presetHighBack_TiltDown = new JoystickButton(operator, 5);
		Button presetHighBack_TiltUp = new JoystickButton(operator, 6);
		
		Button presetScaleHighFwd = new JoystickButton(operator, 7);
		Button presetScaleHighBack = new JoystickButton(operator, 8);
		
		Button presetScaleMidFwd = new JoystickButton(operator, 9);
		Button presetScaleMidBack = new JoystickButton(operator, 10);
		
		Button presetIntake = new JoystickButton(operator, 11);
		Button presetSwitch = new JoystickButton(operator, 12);
		
		Button unfeedSlow = new JoystickButton(operator, 2);
		Button unfeedFast = new JoystickButton(operator, 1);
		
		unfeedSlow.whileHeld(new SetIntake(Intake.State.UNFEED_SLOW, Intake.State.STOP));
		unfeedFast.whileHeld(new SetIntake(Intake.State.UNFEED_FAST, Intake.State.STOP));
		
		presetDriveby.whenPressed(new SetLiftWrist(LiftWrist.Preset.SWITCH_DRIVE_BY, false));
		
		presetHighBack_TiltUp.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_UP, false));
		presetHighBack_TiltDown.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_DOWN, false));
		
		presetScaleHighFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_FWD, false));
		presetScaleHighBack.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_LEVEL, false));
		
		presetScaleMidFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_FWD, false));
		presetScaleMidBack.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_LEVEL, false));

		presetScaleLow.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_LOW_FWD, false));
		
		presetIntake.whenPressed(new SetLiftWrist(LiftWrist.Preset.INTAKE, false));
		presetSwitch.whenPressed(new SetLiftWrist(LiftWrist.Preset.SWITCH, false));
	}
	
	private void setupButtonBoard() {
		ButtonBoard buttonboardA = new ButtonBoard(1);
		ButtonBoard buttonboardB = new ButtonBoard(2);

		//stuff on A
		Button place = new JoystickButton(buttonboardA, 6);
		Button slow = new JoystickButton(buttonboardA, 7);
		Button mid = new JoystickButton(buttonboardA, 8);
		Button fast = new JoystickButton(buttonboardA, 9);
		
		feed.whileHeld(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, false));
		place.whileHeld(intake.setStateCommand(Intake.State.UNFEED_PLACE, Intake.State.STOP, false));
		place.whileHeld(intakeArms.setStateCommand(IntakeArms.State.OPEN, IntakeArms.State.CLOSED, false));
		
		slow.whileHeld(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP, false));
		mid.whileHeld(intake.setStateCommand(Intake.State.UNFEED_MID, Intake.State.STOP, false));
		fast.whileHeld(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP, false));

		Button closeArms = buttonboardA.getJoystick(ButtonBoard.Direction.DOWN);
		Button openArms = buttonboardA.getJoystick(ButtonBoard.Direction.UP);
		Button leftArms = buttonboardA.getJoystick(ButtonBoard.Direction.LEFT);
		Button rightArms = buttonboardA.getJoystick(ButtonBoard.Direction.RIGHT);
		
		closeArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.CLOSED));
		openArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.OPEN));
		leftArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.LEFT));
		rightArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.RIGHT));
		
		Button presetIntake = new JoystickButton(buttonboardA, 1);
		Button presetIntake_Vertical = new JoystickButton(buttonboardA, 2);
		Button presetIntake_2 = new JoystickButton(buttonboardA, 3);
		Button presetPortal = new JoystickButton(buttonboardA, 4);
		Button presetSwitch = new JoystickButton(buttonboardA, 5);
		
		presetIntake.whenPressed(new SetLiftWrist(LiftWrist.Preset.INTAKE, false));
		presetIntake_Vertical.whenPressed(new SetLiftWrist(LiftWrist.Preset.SWITCH_AUTO, false));
		presetIntake_2.whenPressed(new SetLiftWrist(LiftWrist.Preset.INTAKE_2, false));
		presetPortal.whenPressed(new SetLiftWrist(LiftWrist.Preset.PORTAL, false));
		presetSwitch.whenPressed(new SetLiftWrist(LiftWrist.Preset.SWITCH, false));
		
		//stuff on B
		Button presetScaleLowFwd = new JoystickButton(buttonboardB, 1);
		Button presetScaleLowMidFwd = new JoystickButton(buttonboardB, 2);
		Button presetScaleMidFwd = new JoystickButton(buttonboardB, 3);
		Button presetScaleMidHighFwd = new JoystickButton(buttonboardB, 4);
		Button presetScaleHighFwd = new JoystickButton(buttonboardB, 5);
		
		presetScaleLowFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_LOW_FWD, false));
		presetScaleLowMidFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_LOWMID_FWD, false));
		presetScaleMidFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_FWD, false));
		presetScaleMidHighFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MIDHIGH_FWD, false));
		presetScaleHighFwd.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_FWD, false));
		

		Button presetScaleMidBackDunk = new JoystickButton(buttonboardB, 6);
		Button presetScaleMidBackDunk15 = new JoystickButton(buttonboardB, 7);
		Button presetScaleMidBackDunk30 = new JoystickButton(buttonboardB, 8);
		
		presetScaleMidBackDunk.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_DUNK, false));
		presetScaleMidBackDunk15.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_DUNK_15, false));
		presetScaleMidBackDunk30.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_MID_BACK_DUNK_30, false));

		Button presetScaleBackHighBackDown = new JoystickButton(buttonboardB, 9);
		Button presetScaleBackHighBackLevel = new JoystickButton(buttonboardB, 10);
		Button presetScaleBackHighBackUp = new JoystickButton(buttonboardB, 11);
		
		presetScaleBackHighBackLevel.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_LEVEL, false));
		presetScaleBackHighBackDown.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_DOWN, false));
		presetScaleBackHighBackUp.whenPressed(new SetLiftWrist(LiftWrist.Preset.SCALE_HIGH_BACK_UP, false));
		
		Button adjustLiftDown = buttonboardB.getJoystick(ButtonBoard.Direction.DOWN);
		Button adjustLiftUp = buttonboardB.getJoystick(ButtonBoard.Direction.UP);
		Button adjustWristDown = buttonboardB.getJoystick(ButtonBoard.Direction.LEFT);
		Button adjustWristUp = buttonboardB.getJoystick(ButtonBoard.Direction.RIGHT);
		
		adjustLiftDown.whenPressed(WPILambdas.runOnceCommand(() -> {
			liftWrist.adjustLift(-Constants.kLift_NudgeAmount);
		}, true));
		adjustLiftUp.whenPressed(WPILambdas.runOnceCommand(() -> {
			liftWrist.adjustLift(Constants.kLift_NudgeAmount);
		}, true));
		adjustWristDown.whenPressed(WPILambdas.runOnceCommand(() -> {
			liftWrist.adjustWrist(-Constants.kWrist_NudgeAmount);
		}, true));
		adjustWristUp.whenPressed(WPILambdas.runOnceCommand(() -> {
			liftWrist.adjustWrist(Constants.kWrist_NudgeAmount);
		}, true));
	}
}
