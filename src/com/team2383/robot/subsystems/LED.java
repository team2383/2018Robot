package com.team2383.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LED extends Subsystem {
	/**
	 * LEDs are placed up the elevator sides, back of robot etc
	 * 
	 * Top 3rd is always priority status
	 * 
	 * @author granjef3
	 *
	 */
	
	public static enum LED_STATES {
		OFF,
		READY_TO_INTAKE,
		READY_TO_FLIP,
		LIFTING,
		COLLAPSING,
		SCORING,
	}

    public void periodic() {
    	
    }
    
    private void sendStates(LED_STATES[] states) {
    	//interface with LEDs
    }

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
}

