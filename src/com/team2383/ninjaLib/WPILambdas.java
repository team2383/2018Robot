package com.team2383.ninjaLib;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Command;

public class WPILambdas {
	public static Button createButton(BooleanSupplier lambda) {
		return new LambdaButton(lambda);
	}
	
	public static Command runOnceCommand(Runnable execute, boolean ends) {
		return new Command() {
			private boolean set;

			@Override
			protected void execute() {
				if (!set) {
					execute.run();
					set = true;
				}
			}

			@Override
			protected boolean isFinished() {
				return ends && set;
			}

			@Override
			protected void initialize() {
				set = false;
			}

			@Override
			protected void end() {
				set = false;
			}

			@Override
			protected void interrupted() {
				set = false;
			}
		};
	}
	
	public static Command runUntil(Runnable execute, double timeout) {
		return new Command(timeout) {

			@Override
			protected void execute() {
				execute.run();
			}

			@Override
			protected boolean isFinished() {
				return this.isTimedOut();
			}

			@Override
			protected void initialize() {
			}

			@Override
			protected void end() {
			}

			@Override
			protected void interrupted() {
			}
		};
	}

	public static Command runForeverCommand(Runnable execute) {
		return new Command() {
			@Override
			protected void execute() {
				execute.run();
			}

			@Override
			protected boolean isFinished() {
				return false;
			}

			@Override
			protected void initialize() {
			}

			@Override
			protected void end() {
			}

			@Override
			protected void interrupted() {
			}
		};
	}

	public static Command createCommand(BooleanSupplier execute) {
		return new Command() {
			private boolean isFinished;

			@Override
			protected void execute() {
				isFinished = execute.getAsBoolean();
			}

			@Override
			protected boolean isFinished() {
				return isFinished;
			}

			@Override
			protected void initialize() {
			}

			@Override
			protected void end() {
			}

			@Override
			protected void interrupted() {
			}
		};
	}
	
	public static Command createCommand(BooleanSupplier execute, double finishedWait) {
		return new Command() {
			private boolean isFinished;
			private double timeAtSetpoint;
			private double lastCheck;

			@Override
			protected void execute() {
				isFinished = execute.getAsBoolean();
				timeAtSetpoint = 0;
				lastCheck = 0;
			}

			@Override
			protected boolean isFinished() {
				if (isFinished) {
					timeAtSetpoint += this.timeSinceInitialized() - lastCheck;
				} else {
					timeAtSetpoint = 0;
				}
				
				lastCheck = this.timeSinceInitialized();
				return isFinished && timeAtSetpoint >= finishedWait;
			}

			@Override
			protected void initialize() {
			}

			@Override
			protected void end() {
			}

			@Override
			protected void interrupted() {
			}
		};
	}

	public static Command createCommand(Runnable initialize, BooleanSupplier execute, Runnable end,
			Runnable interrupted) {
		return new Command() {
			private boolean isFinished;

			@Override
			protected void initialize() {
				initialize.run();
			}

			@Override
			protected void execute() {
				isFinished = execute.getAsBoolean();
			}

			@Override
			protected boolean isFinished() {
				return isFinished;
			}

			@Override
			protected void end() {
				end.run();
			}

			@Override
			protected void interrupted() {
				interrupted.run();
			}
		};
	}

	public static PIDSource createPIDSource(DoubleSupplier lambda, PIDSourceType type) {
		return new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}

			@Override
			public PIDSourceType getPIDSourceType() {
				return type;
			}

			@Override
			public double pidGet() {
				return lambda.getAsDouble();
			}
		};
	}
}
