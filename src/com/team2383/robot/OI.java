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
import com.team2383.robot.commands.MoveLift;
import com.team2383.robot.subsystems.Climber;
import com.team2383.robot.subsystems.ClimberPin;
import com.team2383.robot.subsystems.Intake;
import com.team2383.robot.subsystems.IntakePivot;
import com.team2383.robot.subsystems.Lift;
import com.team2383.ninjaLib.SetState;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

import static com.team2383.robot.HAL.drivetrain;
import static com.team2383.robot.HAL.intake;
import static com.team2383.robot.HAL.lift;
import static com.team2383.robot.HAL.intakePivot;
import static com.team2383.robot.HAL.climberPin;
import static com.team2383.robot.HAL.climber;




/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

/*
 * OI Controls:
 * 	
 * 	Driver Controls:
 * 	(Left Stick Y) --> Throttle	
 * 	(Right Stick X) --> Turn
 * 
 * 	Left Button --> Unfeed
 * 	Right Button --> Feed
 * 
 * 	X --> Clamp/Unclamp Intake
 * 
 * 
 * 	Operator Controls:
 * 	Button 2 --> Actuate Climber Pin
 * 	Button 3 (while Held) + Y Axis --> Move Lift Up/Down
 * 	Button 4 --> Actuate Climber
 * 
 * 	TODO Preset Buttons for various Lift Stages, Toggle Button for Intake Speed
 * 
 */
@SuppressWarnings("unused")
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.

	/* Sticks functions */

	private static DoubleUnaryOperator inputExpo = (x) -> {
		return Constants.inputExpo * Math.pow(x, 3) + (1 - Constants.inputExpo) * x;
	};
	
	private static DoubleUnaryOperator deadband = (x) -> {
		return Math.abs(x) > Constants.inputDeadband ? x : 0;
	};
	
	
	// All-in-one
	public static Gamepad driver = new Gamepad(0);
	public static Joystick operator = new Joystick(2);
	
	public static DoubleSupplier throttle = () -> deadband.applyAsDouble(driver.getLeftY());
	public static DoubleSupplier turn = () -> deadband.applyAsDouble(-driver.getRightX());
	
	public static Button leftBumper = driver.getLeftShoulder();
	public static Button rightBumper = driver.getRightShoulder();
	
	
	public static Button moveLift = new JoystickButton(operator,3);
	
	public static DoubleSupplier liftStick = () -> deadband.applyAsDouble(-operator.getRawAxis(1));
	
	//public static Button climbUp = new JoystickButton(advancedOperator,4);
	public static Button liftUp = new DPadButton(operator, Direction.UP);
	//public static Button climbDown = new JoystickButton(advancedOperator, 1);
	public static Button liftDown = new DPadButton(operator, Direction.DOWN);
	
	public static Button actuatePivot = new JoystickButton(driver,3);
	
	public static Button actuateclimberPin = new JoystickButton(operator, 2);
	
	public static Button actuateClimber = new JoystickButton(operator,4);
	
	public OI() {
		
		leftBumper.whileHeld(new SetState<Intake.State>(intake, Intake.State.UNFEED, Intake.State.STOPPED));
		rightBumper.whileHeld(new SetState<Intake.State>(intake, Intake.State.FEED, Intake.State.STOPPED));
		
		
		//liftUp.whileHeld(new SetState<Lift.State>(lift, Lift.State.UP, Lift.State.STOPPED));
		//liftDown.whileHeld(new SetState<Lift.State>(lift, Lift.State.DOWN, Lift.State.STOPPED));
		
		moveLift.whileHeld(new MoveLift(OI.liftStick));
		
		actuatePivot.toggleWhenActive(new SetState<IntakePivot.State>(intakePivot, IntakePivot.State.UP, IntakePivot.State.DOWN));
		actuateclimberPin.toggleWhenActive(new SetState<ClimberPin.State>(climberPin, ClimberPin.State.RETRACTED, ClimberPin.State.EXTENDED));
		actuateClimber.toggleWhenActive(new SetState<Climber.State>(climber, Climber.State.EXTENDED, Climber.State.RETRACTED));

		
	}
}
