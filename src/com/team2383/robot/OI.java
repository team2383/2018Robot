package com.team2383.robot;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;


import com.team2383.ninjaLib.DPadButton;
import com.team2383.ninjaLib.DPadButton.Direction;
import com.team2383.ninjaLib.Gamepad;
import com.team2383.ninjaLib.LambdaButton;
import com.team2383.ninjaLib.OnChangeButton;
import com.team2383.ninjaLib.Values;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.commands.LiftPreset;
import com.team2383.robot.commands.TeleopLiftMotionMagic;
import com.team2383.robot.commands.TeleopLiftOpenLoop;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Lift;

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
import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.wrist;



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
	public static Gamepad driver = new Gamepad(0);
	public static Joystick operator = new Joystick(2);
	
	public static DoubleSupplier throttle = () -> (-driver.getLeftY());
	public static DoubleSupplier turn = () -> (driver.getRightX());
	
	public static DoubleSupplier liftSpeed = () -> (operator.getY());
	
	public static Button unfeed = new DPadButton(operator, Direction.UP);
	public static Button unfeedFast = new JoystickButton(operator, 1);
	public static Button feed = new DPadButton(operator, Direction.DOWN);
	public static Button clamp = new JoystickButton(operator, 5);

	public static Button liftMotionMagic = new JoystickButton(operator, 2);
	public static Button liftManual = new JoystickButton(operator, 3);
	
	public static Button liftPresetBottom = new JoystickButton(operator, 11);
	public static Button liftPresetSwitch = new JoystickButton(operator, 9);
	public static Button liftPresetScaleMid = new JoystickButton(operator, 7);
	public static Button liftPresetScaleHigh = new JoystickButton(operator, 8);

	public static Button allLiftPresets = new LambdaButton(() -> {
		return operator.getRawButton(9) || operator.getRawButton(8) || operator.getRawButton(7) || operator.getRawButton(11);
	});
	
	public static Button updateMotorControllers = new NetworkButton("SmartDashboard", "Update Motor Controllers");
	
	public static Button rev = new JoystickButton(driver, 3);
	
	public OI() {
		//init the button
		SmartDashboard.putBoolean("Update Motor Controllers", false);

		unfeed.whileHeld(intake.setStateCommand(Intake.State.UNFEED, Intake.State.STOP));
		unfeedFast.whileHeld(intake.setStateCommand(Intake.State.UNFEED_FAST, Intake.State.STOP));
		feed.whileHeld(intake.setStateCommand(Intake.State.FEED, Intake.State.STOP));
		
		liftManual.whileHeld(new TeleopLiftOpenLoop(liftSpeed));
		liftMotionMagic.whileHeld(new TeleopLiftMotionMagic(liftSpeed));
		
		liftPresetBottom.whenPressed(new LiftPreset(Lift.Preset.BOTTOM));
		liftPresetSwitch.whenPressed(new LiftPreset(Lift.Preset.TELEOP_SWITCH));
		liftPresetScaleMid.whenPressed(new LiftPreset(Lift.Preset.SCALE_MID));
		liftPresetScaleHigh.whenPressed(new LiftPreset(Lift.Preset.SCALE_HIGH));
		
		updateMotorControllers.whenReleased(WPILambdas.runOnceCommand(() -> {
			lift.configMotorControllers(10);
			wrist.configMotorControllers(10);
			drive.configMotorControllers(10);
			}, true));
	}
}
