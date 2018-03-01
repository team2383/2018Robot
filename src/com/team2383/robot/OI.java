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
import com.team2383.robot.commands.TeleopLiftMotionMagic;
import com.team2383.robot.commands.TeleopLiftOpenLoop;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakePivot;
import com.team2383.robot.subsystems.Lift;
import com.team2383.ninjaLib.SetState;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.MatchType;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

import static com.team2383.robot.HAL.drive;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.intakePivot;
import static com.team2383.robot.HAL.lift;



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
	private static Gamepad driver = new Gamepad(0);
	private static Joystick operator = new Joystick(2);
	
	public static DoubleSupplier throttle = () -> (-driver.getLeftY());
	public static DoubleSupplier turn = () -> (driver.getRightX());
	
	public static DoubleSupplier liftSpeed = () -> (operator.getY());
	
	public static Button unfeed;
	public static Button feed;
	public static Button clamp;
	
	public static Button liftMotionMagicCoarse;
	public static Button liftMotionMagicFine;
	public static Button liftManual;
	
	public static Button rev = new JoystickButton(driver, 3);
	
	public OI() {
 		unfeed = driver.getLeftShoulder();
 		feed = driver.getRightShoulder();
 		clamp = driver.getButtonX();
 		
 		unfeed.whileHeld(new SetState<Intake.State>(intake, Intake.State.UNFEED, Intake.State.STOPPED));
 		feed.whileHeld(new SetState<Intake.State>(intake, Intake.State.FEED, Intake.State.STOPPED));
 		clamp.toggleWhenActive(new SetState<IntakePivot.State>(intakePivot, IntakePivot.State.UP, IntakePivot.State.DOWN));
	    
 		liftMotionMagicCoarse = new JoystickButton(operator, 1);
	    liftMotionMagicFine = new JoystickButton(operator, 2);
		liftManual = new JoystickButton(operator, 5);
		
		liftManual.whileHeld(new TeleopLiftOpenLoop(liftSpeed));
		liftMotionMagicCoarse.whileHeld(new TeleopLiftMotionMagic(liftSpeed));
		liftMotionMagicFine.whileHeld(new TeleopLiftMotionMagic(liftSpeed));
	}
}
