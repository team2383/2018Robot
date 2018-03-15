package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.ninjaLib.StatefulSubsystem;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.subsystems.Wrist.Preset;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftWrist extends StatefulSubsystem<LiftWrist.State> {
	private Lift lift;
	private Wrist wrist;
	
	public static enum State {
		STOPPED(null, null),
		MANUAL_LIFT(null, null),
		MANUAL_WRIST(null, null),
		INTAKE(Lift.Preset.BOTTOM, Wrist.Preset.INTAKE),
		INTAKE_2(Lift.Preset.INTAKE_2, Wrist.Preset.INTAKE),
		SWITCH_AUTO(Lift.Preset.BOTTOM, Wrist.Preset.STARTING),
		SWITCH(Lift.Preset.SWITCH, Wrist.Preset.INTAKE),
		SWITCH_DRIVE_BY(Lift.Preset.SWITCH, Wrist.Preset.UP),
		SCALE_MID_FWD(Lift.Preset.SCALE_MID, Wrist.Preset.FORWARD_MID),
		SCALE_MID_BACK(Lift.Preset.SCALE_MID, Wrist.Preset.BACKWARDS),
		SCALE_HIGH_FWD(Lift.Preset.SCALE_HIGH, Wrist.Preset.FORWARD_HIGH),
		SCALE_HIGH_BACK(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_DOWN),
		SCALE_HIGH_BACK_TILTUP(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_UP),
		SCALE_HIGH_BACK_TILTDOWN(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_DOWN);
		
		protected Lift.Preset liftPreset;
		protected Wrist.Preset wristPreset;

		private State(Lift.Preset liftPreset, Wrist.Preset wristPreset) {
			this.liftPreset = liftPreset;
			this.wristPreset = wristPreset;
		}
	}

    public LiftWrist(boolean isPracticeBot) {
		wrist = new Wrist(isPracticeBot);
		lift = new Lift(isPracticeBot);
		
		instanceSupplier = () -> liftWrist;
		
		this.state = State.STOPPED;
	}

    public void configMotorControllers(int timeout) {
    	lift.configMotorControllers(timeout);
    	wrist.configMotorControllers(timeout);
    }
    
	@Override
	public void setState(State state) {
		//we dont use this now
		this.state = state;
	}
	
	/**
	 * handles all movement and states for the Lift + Wrist
	 */
	public void periodic() {
		switch(this.state) {
			case SWITCH_AUTO:
				//we know it all clears since its called in auto
				wrist.setPreset(state.wristPreset);
				lift.setPreset(state.liftPreset);
				break;
			case INTAKE:
			case INTAKE_2:
			case SWITCH_DRIVE_BY:
			case SWITCH:
				wrist.setPreset(state.wristPreset);
				//make sure the wrist clears the lift
				if (wrist.getCurrentPosition() < Constants.kLiftWrist_Wrist_LiftDownMaxAngle) {
					//don't tell the lift to go down unless the wrist won't hit it
					lift.setPreset(state.liftPreset);
				} else {
					lift.setPosition(Constants.kLiftWrist_Lift_WristBackwardsDownMinHeight);
				}
				break;
			case SCALE_MID_FWD:
			case SCALE_HIGH_FWD:
				wrist.setPreset(state.wristPreset);
				lift.setPreset(state.liftPreset);
				break;

			case SCALE_HIGH_BACK:
			case SCALE_MID_BACK:
			case SCALE_HIGH_BACK_TILTDOWN:
			case SCALE_HIGH_BACK_TILTUP:
				lift.setPreset(state.liftPreset);
				//make sure the wrist clears the lift
				if (lift.getCurrentPosition() > Constants.kLiftWrist_Lift_WristBackwardsUpMinHeight) {
					//don't tell the wrist to go back unless it wont hit the lift
					wrist.setPreset(state.wristPreset);
				} else {
					wrist.setPreset(Wrist.Preset.TRANSIT);
				}
				break;

			case MANUAL_LIFT:
				lift.setOutput(OI.liftSpeed.getAsDouble());
				break;
				
			case MANUAL_WRIST:
				wrist.setOutput(OI.liftSpeed.getAsDouble());
				break;

			default:
			case STOPPED:
				lift.stop();
				wrist.stop();
		}
	}

	public boolean atTarget() {
		boolean wristAtTarget = Math.abs(state.wristPreset.wristPosition - wrist.getCurrentPosition()) < Constants.kWrist_Tolerance;
		boolean liftAtTarget = Math.abs(state.liftPreset.liftPosition - lift.getCurrentPosition()) < Constants.kLift_Tolerance;
		return wristAtTarget && liftAtTarget;
	}
	
	public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

