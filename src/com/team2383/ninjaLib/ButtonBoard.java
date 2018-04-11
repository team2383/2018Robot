package com.team2383.ninjaLib;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class ButtonBoard extends Joystick {
	private boolean isJoystickHorizontal;
	public ButtonBoard(int port, boolean isJoystickHorizontal) {
		super(port);
		this.isJoystickHorizontal = isJoystickHorizontal;
	}
	
	public Button getButton(int indice) {
		return new JoystickButton(this, indice);
	}
	
	public Button getJoystick(Direction dir) {
		if (!isJoystickHorizontal) {
			return new LambdaButton(() -> {
				boolean result = false;
				switch(dir) {
					case UP:
						result = getY() <= -0.8;
						break;
					case DOWN:
						result = getY() >= 0.8;
						break;
					case LEFT:
						result = getX() <= -0.8;
						break;
					case RIGHT:
						result = getX() >= 0.8;
						break;
				}
				
				return result;
			});
		} else {
			return new LambdaButton(() -> {
				boolean result = false;
				switch(dir) {
					case LEFT:
						result = getY() >= 0.8;
						break;
					case RIGHT:
						result = getY() <= -0.8;
						break;
					case UP:
						result = getX() <= -0.8;
						break;
					case DOWN:
						result = getX() >= 0.8;
						break;
				}
				
				return result;
			});
		}
	}

	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
}