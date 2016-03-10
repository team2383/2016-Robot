package com.team2383.robot.subsystems;

import static com.team2383.robot.HAL.shooterMotor;

import com.team2383.robot.Constants;

import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterFlywheel extends Subsystem {

	public ShooterFlywheel() {
		shooterMotor.enableBrakeMode(false);
		shooterMotor.reverseSensor(true);
		shooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterMotor.enable();
	}

	/**
	 * call periodically to spool the shooter to a certain RPM BANG-BANG
	 * controller: Off if at or above setpoint, On otherwise.
	 *
	 * @param rpm
	 */
	public void spoolToRPM(double rpm) {
		if (shooterMotor.getSpeed() < rpm) {
			spool();
		} else {
			stop();
		}
	}

	public void set(double speed) {
		shooterMotor.set(speed);
	}

	public void spool() {
		shooterMotor.set(1);
	}

	public void stop() {
		shooterMotor.set(0);
	}

	public void unspool() {
		shooterMotor.set(-1);
	}

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub

	}

	public boolean isAtRPM(double rpm) {
		return Math.abs(shooterMotor.getSpeed() - rpm) < Constants.shooterRPMTolerance;
	}
}