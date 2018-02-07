package com.team2383.robot;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;


import com.team2383.ninjaLib.DPadButton;
import com.team2383.ninjaLib.DPadButton.Direction;
import com.team2383.ninjaLib.Gamepad;
import com.team2383.ninjaLib.LambdaButton;
import com.team2383.ninjaLib.OnChangeButton;
import com.team2383.ninjaLib.SetState;
import com.team2383.ninjaLib.Values;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.Lift;
import com.team2383.ninjaLib.SetState;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.prefs;



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
		return Math.abs(x) > prefs.getDouble("inputDeadband", 0.05) ? x : 0;
	};
	
	
	// All-in-one
	public static Gamepad driver = new Gamepad(0);
	
	public static DoubleSupplier throttle = () -> (-driver.getLeftY());
	public static DoubleSupplier turn = () -> (driver.getRightX());
	
	public static Button leftBumper = driver.getLeftShoulder();
	public static Button rightBumper = driver.getRightShoulder();
	
	public static Button rev = new JoystickButton(driver, 3);
	
	//public static Button climbUp = new JoystickButton(advancedOperator,4);
	public static Button climbUp = new DPadButton(driver, Direction.UP);
	//public static Button climbDown = new JoystickButton(advancedOperator, 1);
	public static Button climbDown = new DPadButton(driver, Direction.DOWN);
	
	public OI() {
		
		leftBumper.whileHeld(new SetState<Intake.State>(intake, Intake.State.UNFEED, Intake.State.STOPPED));
		rightBumper.whileHeld(new SetState<Intake.State>(intake, Intake.State.FEED, Intake.State.STOPPED));
		
		rev.toggleWhenPressed(new SetState<Intake.State>(intake, Intake.State.REV, Intake.State.STOPPED));
		
		climbUp.whileHeld(new SetState<Lift.State>(lift, Lift.State.UP, Lift.State.STOPPED));
		climbDown.whileHeld(new SetState<Lift.State>(lift, Lift.State.DOWN, Lift.State.STOPPED));
	}
}
