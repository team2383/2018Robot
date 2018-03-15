package com.team2383.robot;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;


import com.team2383.ninjaLib.DPadButton;
import com.team2383.ninjaLib.DPadButton.Direction;
import com.team2383.ninjaLib.Gamepad;
import com.team2383.ninjaLib.LambdaButton;
import com.team2383.ninjaLib.OnChangeButton;
import com.team2383.ninjaLib.Values;
import com.team2383.ninjaLib.WPILambdas;
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
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.

	/* Sticks functions */
	
	private static DoubleUnaryOperator deadband = (x) -> {
		return Math.abs(x) > Constants.inputDeadband ? x : 0;
	};
	
	
	// All-in-one
	public static Joystick operator = new Joystick(2);
	
	public static DoubleSupplier throttle;
	public static DoubleSupplier turn;
	public static Button driveStraight;
	public static Button feed;
	
	public static DoubleSupplier liftSpeed = () -> (operator.getY());
	
	public static BooleanSupplier lockout;
	
	public static Button unfeedSlow;

	public static Button unfeedFast;

	public static Button driver_intake;
	public static Button driver_intake_2;
	
	public static Button closeArms = new DPadButton(operator, DPadButton.Direction.DOWN);
	public static Button openArms = new DPadButton(operator, DPadButton.Direction.UP);
	public static Button leftArms = new DPadButton(operator, DPadButton.Direction.LEFT);
	public static Button rightArms = new DPadButton(operator, DPadButton.Direction.RIGHT);

	public static Button liftManual = new JoystickButton(operator, 5);
	public static Button wristManual = new JoystickButton(operator, 6);

	public static Button liftWristPresetHighBack_TiltDown = new JoystickButton(operator, 3);
	public static Button liftWristPresetHighBack_TiltUp = new JoystickButton(operator, 4);
	
	public static Button liftWristPresetScaleHighFwd = new JoystickButton(operator, 7);
	public static Button liftWristPresetScaleHighBack = new JoystickButton(operator, 8);
	
	public static Button liftWristPresetScaleMidFwd = new JoystickButton(operator, 9);
	public static Button liftWristPresetScaleMidBack = new JoystickButton(operator, 10);
	
	public static Button liftWristPresetIntake = new JoystickButton(operator, 11);
	public static Button liftWristPresetSwitch = new JoystickButton(operator, 12);
	
	public static Button updateMotorControllers = new NetworkButton("SmartDashboard", "Update Motor Controllers");
	
	public enum ControlScheme {
		XBOX_JOYOperator,
		YOKE_JOYThrottle_JOYOperator,
		YOKE_YOKEThrottle_JOYOperator
	}
	
	public OI() {
		//init the button
		SmartDashboard.putBoolean("Update Motor Controllers", false);

		ControlScheme cs = ControlScheme.values()[Constants.controlScheme];
		
		switch(cs) {
			case YOKE_JOYThrottle_JOYOperator:
				System.out.println("yokeJoy");
				Joystick yoke = new Joystick(0);
				Joystick driver_throttle = new Joystick(1);
				
				turn = () -> (yoke.getX());
				throttle = () -> (-driver_throttle.getY());
				
				lockout = () -> driver_throttle.getRawButton(2);

				driveStraight = new JoystickButton(yoke, 1);
				feed = new JoystickButton(driver_throttle, 1);
				break;
			case YOKE_YOKEThrottle_JOYOperator:
				System.out.println("yokeThrottle");
				Joystick yokeT = new Joystick(0);
				
				turn = () -> (yokeT.getX());
				throttle = () -> (-yokeT.getY());
				
				lockout = () -> (yokeT.getRawButton(1) && yokeT.getRawButton(2));

				driveStraight = new JoystickButton(yokeT, 1);
				feed = new JoystickButton(yokeT, 2);
				break;
			default:
			case XBOX_JOYOperator:
				System.out.println("xbox");
				Gamepad driver = new Gamepad(0);
				
				turn = () -> (driver.getRightX());
				throttle = () -> (-driver.getLeftY());
				
				lockout = () -> (driver.getLeftTriggerClick() && driver.getRightTriggerClick());
				
				driver_intake = new JoystickButton(driver, Gamepad.BUTTON_A);
				driver_intake_2 = new JoystickButton(driver, Gamepad.BUTTON_Y);
				
				driveStraight = new JoystickButton(driver, Gamepad.BUTTON_SHOULDER_LEFT);
				feed = new JoystickButton(driver, Gamepad.BUTTON_SHOULDER_RIGHT);
				break;
		}
		
		unfeedSlow = new LambdaButton(() -> {
			return lockout.getAsBoolean() && operator.getRawButton(2);
		});
		unfeedFast = new LambdaButton(() -> {
			return lockout.getAsBoolean() && operator.getRawButton(1);
		});
		
		unfeedSlow.whileHeld(intake.setStateCommand(Intake.State.UNFEED_SLOW, Intake.State.STOP));
		unfeedFast.whileHeld(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP));
		
		feed.whileHeld(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP, false));
		
		closeArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.CLOSED));
		openArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.OPEN));
		leftArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.LEFT));
		rightArms.whileHeld(intakeArms.setStateCommand(IntakeArms.State.RIGHT));
		
		liftManual.whenPressed(liftWrist.setStateCommand(LiftWrist.State.MANUAL_LIFT));
		wristManual.whenPressed(liftWrist.setStateCommand(LiftWrist.State.MANUAL_WRIST));
		liftManual.whenReleased(liftWrist.setStateCommand(LiftWrist.State.STOPPED));
		wristManual.whenReleased(liftWrist.setStateCommand(LiftWrist.State.STOPPED));
		
		liftWristPresetHighBack_TiltUp.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_HIGH_BACK_TILTUP));
		liftWristPresetHighBack_TiltDown.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_HIGH_BACK_TILTDOWN));
		
		liftWristPresetScaleHighFwd.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_HIGH_FWD));
		liftWristPresetScaleHighBack.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_HIGH_BACK));
		
		liftWristPresetScaleMidFwd.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_MID_FWD));
		liftWristPresetScaleMidBack.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SCALE_MID_BACK));

		driver_intake.whenPressed(liftWrist.setStateCommand(LiftWrist.State.INTAKE));
		driver_intake_2.whenPressed(liftWrist.setStateCommand(LiftWrist.State.INTAKE_2));
		liftWristPresetIntake.whenPressed(liftWrist.setStateCommand(LiftWrist.State.INTAKE));
		liftWristPresetSwitch.whenPressed(liftWrist.setStateCommand(LiftWrist.State.SWITCH));
		
		updateMotorControllers.whenReleased(WPILambdas.runOnceCommand(() -> {
			liftWrist.configMotorControllers(10);
			drive.configMotorControllers(10);
			}, true));
	}
}
