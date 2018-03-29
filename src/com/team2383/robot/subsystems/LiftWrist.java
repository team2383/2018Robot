package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.liftWrist;

import com.team2383.ninjaLib.StatefulSubsystem;
import com.team2383.ninjaLib.WPILambdas;
import com.team2383.robot.Constants;
import com.team2383.robot.OI;
import com.team2383.robot.subsystems.Wrist.Preset;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class LiftWrist extends Subsystem {
	private Lift lift;
	private Wrist wrist;
	
	private State state;
	
	private double desiredLiftPos;
	private double desiredWristPos;
	
	public static enum Preset {
		INTAKE(Lift.Preset.BOTTOM, Wrist.Preset.INTAKE),
		INTAKE_2(Lift.Preset.INTAKE_2, Wrist.Preset.INTAKE),
		
		PORTAL(Lift.Preset.PORTAL, Wrist.Preset.PORTAL),

		SWITCH_AUTO(Lift.Preset.BOTTOM, Wrist.Preset.STARTING),
		SWITCH(Lift.Preset.SWITCH, Wrist.Preset.INTAKE),
		SWITCH_DRIVE_BY(Lift.Preset.BOTTOM, Wrist.Preset.UP),
		

		SCALE_LOW_FWD(Lift.Preset.SCALE_LOW, Wrist.Preset.FORWARD_LOW),
		SCALE_LOWMID_FWD(Lift.Preset.SCALE_LOWMID, Wrist.Preset.FORWARD_LOW),
		SCALE_MID_FWD(Lift.Preset.SCALE_HIGH, Wrist.Preset.FORWARD_LOW),
		SCALE_MIDHIGH_FWD(Lift.Preset.SCALE_MIDHIGH, Wrist.Preset.FORWARD_HIGH),
		SCALE_HIGH_FWD(Lift.Preset.SCALE_HIGH, Wrist.Preset.FORWARD_HIGH),
		
		SCALE_MID_BACK_DUNK(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_DUNK),
		SCALE_MID_BACK_DOWN(Lift.Preset.SCALE_MID, Wrist.Preset.BACKWARDS_DOWN),
		SCALE_MID_BACK_LEVEL(Lift.Preset.SCALE_MID, Wrist.Preset.BACKWARDS),
		SCALE_MID_BACK_UP(Lift.Preset.SCALE_MID, Wrist.Preset.BACKWARDS_UP),	

		SCALE_HIGH_BACK_DOWN(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_DOWN),
		SCALE_HIGH_BACK_LEVEL(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS),
		SCALE_HIGH_BACK_UP(Lift.Preset.SCALE_HIGH, Wrist.Preset.BACKWARDS_UP);
		
		public Lift.Preset liftP;
		public Wrist.Preset wristP;

		private Preset(Lift.Preset liftPreset, Wrist.Preset wristPreset) {
			this.liftP = liftPreset;
			this.wristP = wristPreset;
		}
	}
	
	public static enum State {
		STOPPED,
		MANUAL_OUTPUT_LIFT,
		MANUAL_OUTPUT_WRIST,
		MOTION_MAGIC
	}
	

    public LiftWrist(boolean isPracticeBot) {
		wrist = new Wrist(isPracticeBot);
		lift = new Lift(isPracticeBot);
		
		setState(State.STOPPED);
	}

    public void configMotorControllers(int timeout) {
    	lift.configMotorControllers(timeout);
    	wrist.configMotorControllers(timeout);
    }
    
    private void setState(State state) {
		this.state = state;
	}

	public void stop() {
		setState(State.STOPPED);
	}

	public void setPreset(Preset preset) {
		setState(State.MOTION_MAGIC);
		desiredLiftPos = preset.liftP.liftPosition;
		desiredWristPos = preset.wristP.wristPosition;
	}
	
	public void wantsLift(double liftPos) {
		setState(State.MOTION_MAGIC);
		desiredLiftPos = liftPos;
	}
	
	public void wantsWrist(double wristPos) {
		setState(State.MOTION_MAGIC);
		desiredWristPos = wristPos;
	}
	
	public void setManualLift() {
		setState(State.MANUAL_OUTPUT_LIFT);
	}
	
	public void setManualWrist() {
		setState(State.MANUAL_OUTPUT_WRIST);
	}
	
	
	/**
	 * handles all movement and states for the Lift + Wrist
	 */
	public void periodic() {
		switch(this.state) {
			case MANUAL_OUTPUT_LIFT:
				lift.setOutput(OI.manualSpeed.getAsDouble());
				break;
			case MANUAL_OUTPUT_WRIST:
				wrist.setOutput(OI.manualSpeed.getAsDouble());
				break;
			case MOTION_MAGIC:
				handleMotionMagicLogic();
				break;
			case STOPPED:
				lift.stop();
				wrist.stop();
				break;
		}
	}

	public void handleMotionMagicLogic() {
		//does the wrist want to go past clearance angle?
		if(desiredWristPos > Constants.kLiftWrist_WristClearanceAngle) {	
			SmartDashboard.putBoolean("L desiredWristPastClearance", true);
			//can the wrist go past lift at desired lift height?
			if(desiredLiftPos < Constants.kLiftWrist_LiftClearanceHeight) {
				SmartDashboard.putBoolean("L desiredLiftWasOverride", true);
				//the wrist wants to go back, but the desired lift height isn't high enough to allow.
				//so update desired lift height to the clearance height
				desiredLiftPos = Constants.kLiftWrist_LiftClearanceHeight;
				
				return; //don't do any more checks, desired state was invalid for this iteration so the next one will execute the fixed one.
			} else {
				//the wrist wants to go back, and the desired lift height is high enough.
				
				//desired lift height is high enough for wrist clearance, so we are safe to move to it
				lift.setPosition(desiredLiftPos);
				
				//make sure the wrist clears the lift before moving wrist to past clearance angle
				if (lift.getCurrentPosition() < Constants.kLiftWrist_LiftClearanceHeight) {
					SmartDashboard.putBoolean("L wrist In transit", true);
					//lift is currently below clearance height, so hold wrist in transit position
					wrist.setPreset(Wrist.Preset.TRANSIT);
				} else {
					SmartDashboard.putBoolean("L wrist In transit", false);
					wrist.setPosition(desiredWristPos);
				}
			}
		} else {
			SmartDashboard.putBoolean("L desiredLiftWasOverride", false);
			SmartDashboard.putBoolean("L desiredWristPastClearance", false);
			//this is a safe desired wrist position, go ahead and set it
			wrist.setPosition(desiredWristPos);
		}
		
		//does the lift want to go below clearance height?
		if(desiredLiftPos < Constants.kLiftWrist_LiftClearanceHeight) {
			SmartDashboard.putBoolean("L desired lift below clearanceheight", true);
			//can the wrist clear the lift at desired lift height?
			if(desiredWristPos > Constants.kLiftWrist_WristClearanceAngle) {
				SmartDashboard.putBoolean("L invalid wrist was ovverode", true);
				//the lift wants to go down, but the desired wrist angle will cause a collision
				//so update desired wrist angle to the transit angle
				desiredWristPos = Wrist.Preset.TRANSIT.wristPosition;
				
				return; //don't do any more checks, desired state was invalid for this iteration so the next one will execute the fixed one.
			} else {
				//the lift wants to go down, and the desired wrist angle will clear it.
				
				//desired wrist angle can't collide with the lift, so we are safe to move to it
				wrist.setPosition(desiredWristPos);
				
				//make sure the wrist clears the lift before moving lift below clearance height
				if (wrist.getCurrentPosition() > Constants.kLiftWrist_WristClearanceAngle) {
					SmartDashboard.putBoolean("L lift waiting for wrist", true);
					//wrist is currently past clearance angle, so move lift to clearance height while we wait for it to come past
					lift.setPosition(Constants.kLiftWrist_LiftClearanceHeight);
				} else {
					SmartDashboard.putBoolean("L lift waiting for wrist", false);
					lift.setPosition(desiredLiftPos);
				}
			}
		} else {
			SmartDashboard.putBoolean("L invalid wrist was ovverode", false);
			SmartDashboard.putBoolean("L desired lift below clearanceheight", false);
			//this is a safe desired lift position, go ahead and set it
			lift.setPosition(desiredLiftPos);
		}
	}

	public double getDesiredLiftPosition() {
		return desiredLiftPos;
	}
	
	public double getDesiredWristPosition() {
		return desiredWristPos;
	}
	
	public boolean doesWantPreset(Preset preset) {
		return desiredLiftPos == preset.liftP.liftPosition &&
			   desiredWristPos == preset.wristP.wristPosition;
	}

	public boolean atTarget() {
		boolean wristAtTarget = Math.abs(desiredWristPos - wrist.getCurrentPosition()) < Constants.kWrist_Tolerance;
		boolean liftAtTarget = Math.abs(desiredLiftPos - lift.getCurrentPosition()) < Constants.kLift_Tolerance;
		return wristAtTarget && liftAtTarget;
	}
	
	public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

	public void setLiftZero() {
		lift.setZero();
	}
	
	public void setWristZero() {
		wrist.setZero();
	}
}

