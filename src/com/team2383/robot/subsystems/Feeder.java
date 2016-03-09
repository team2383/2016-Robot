package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.feederMotor;

import com.team2383.robot.commands.SetState.StatefulSubsystem;

public class Feeder extends StatefulSubsystem<Feeder.State> {

	public Feeder() {
		feederMotor.setInverted(true);
		feederMotor.setSafetyEnabled(false);
	}

	public enum State {
		FEEDING, OUTFEEDING, STOPPED
	}

	public void feedIn() {
		System.out.println("Feeding!");
		feederMotor.set(1);
	}

	public void feedOut() {
		feederMotor.set(-1);
	}

	public void stop() {
		feederMotor.set(0);
	}

	@Override
	protected void initDefaultCommand() {
	}

	@Override
	public void setState(State state) {
		switch (state) {
		case FEEDING:
			feedIn();
			break;
		case OUTFEEDING:
			feedOut();
			break;
		default:
		case STOPPED:
			stop();
			break;
		}
	}

}
