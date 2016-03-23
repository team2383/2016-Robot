package com.team2383.robot;

import static com.team2383.robot.HAL.arms;
import static com.team2383.robot.HAL.feeder;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

import com.team2383.ninjaLib.DPadButton;
import com.team2383.ninjaLib.DPadButton.Direction;
import com.team2383.ninjaLib.Gamepad;
import com.team2383.ninjaLib.LambdaButton;
import com.team2383.ninjaLib.OnChangeButton;
import com.team2383.ninjaLib.Values;
import com.team2383.robot.Constants.Preset;
import com.team2383.robot.commands.MoveHood;
import com.team2383.robot.commands.SetSpoolRPM;
import com.team2383.robot.commands.SetState;
import com.team2383.robot.commands.ShiftTo;
import com.team2383.robot.commands.Shoot;
import com.team2383.robot.commands.SpoolToRPM;
import com.team2383.robot.commands.TeleopDrive;
import com.team2383.robot.commands.ToggleHoodStop;
import com.team2383.robot.commands.UsePreset;
import com.team2383.robot.subsystems.Arms;
import com.team2383.robot.subsystems.Drivetrain.Gear;
import com.team2383.robot.subsystems.Feeder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.

	/* Sticks */

	private static DoubleUnaryOperator deadband = (x) -> {
		return Math.abs(x) > Constants.inputDeadband ? x : 0;
	};

	public static Gamepad gamepad = new Gamepad(0);

	public static Button shiftDown = gamepad.getLeftShoulder();
	public static Button shiftUp = gamepad.getRightShoulder();
	public static DoubleSupplier leftStick = () -> deadband.applyAsDouble(gamepad.getLeftY());
	public static DoubleSupplier rightStick = () -> deadband.applyAsDouble(gamepad.getRightX());

	public static Button drive = new LambdaButton(() -> {
		return leftStick.getAsDouble() != 0 || rightStick.getAsDouble() != 0;
	});

	public static Joystick operator = new Joystick(2);

	public static DoubleSupplier hood = () -> deadband.applyAsDouble(operator.getY());;
	public static DoubleSupplier shooterSpeed = () -> {
		return Values.mapRange(-1.0, 1.0, Constants.shooterMinRPM, Constants.shooterMaxRPM)
				.applyAsDouble(operator.getThrottle());
	};

	public static Button alignBall = new JoystickButton(operator, 7);
	public static Button feedIn = new JoystickButton(operator, 8);
	public static Button presetOnBatter = new JoystickButton(operator, 9);
	public static Button presetTowerWall = new JoystickButton(operator, 10);
	public static Button presetCourtyardFar = new JoystickButton(operator, 11);
	public static Button presetCourtyardMid = new JoystickButton(operator, 12);

	public static Button extendArms = new DPadButton(operator, Direction.UP);
	public static Button retractArms = new DPadButton(operator, Direction.DOWN);

	public static Button shoot = new JoystickButton(operator, 1); // trigger
	public static Button spoolToLastSet = new JoystickButton(operator, 2); // thumb

	public static Button hoodPancake = new JoystickButton(operator, 3);
	public static Button manualSpool = new JoystickButton(operator, 4);
	public static Button vision = new JoystickButton(operator, 5);
	public static Button feedOutSlow = new JoystickButton(operator, 6);

	public static Button setShooterSpeed = new OnChangeButton(OI.shooterSpeed);

	public static Button moveHood = new LambdaButton(() -> {
		return hood.getAsDouble() != 0;
	});

	// use buttons
	public OI() {
		shiftDown.whileHeld(new ShiftTo(Gear.LOW));
		shiftUp.whileHeld(new ShiftTo(Gear.HIGH));

		feedIn.whileHeld(new SetState<Feeder.State>(feeder, Feeder.State.FEEDING, Feeder.State.STOPPED));
		feedOutSlow.whileHeld(new SetState<Feeder.State>(feeder, Feeder.State.OUTFEEDINGSLOW, Feeder.State.STOPPED));

		extendArms.whileHeld(new SetState<Arms.State>(arms, Arms.State.EXTENDING, Arms.State.STOPPED));
		retractArms.whileHeld(new SetState<Arms.State>(arms, Arms.State.RETRACTING, Arms.State.STOPPED));

		/*
		 * if (dualCams != null && dualCams instanceof CameraFeeds) {
		 * switchCamera.whenPressed(WPILambdas.createCommand(() -> {
		 * dualCams.switchCam(); return true; })); }
		 */

		// when the shooterRPM throttle (operator throttle)
		// stops moving, we set the shooter rpm setpoint
		setShooterSpeed.whenReleased(new SetSpoolRPM(OI.shooterSpeed));

		// spools to the last set RPM
		spoolToLastSet.whileHeld(new SpoolToRPM());

		alignBall.whileHeld(new SpoolToRPM(() -> -2000));
		// when alignBall is released, set shooter RPM to the shooterRPM
		// throttle (operator throttle)
		alignBall.whenReleased(new SetSpoolRPM(OI.shooterSpeed));

		// shoots ball (same as feedIn)
		shoot.whileHeld(new Shoot());

		moveHood.whileHeld(new MoveHood(OI.hood));
		drive.whileHeld(new TeleopDrive(OI.leftStick, OI.rightStick));

		presetTowerWall.whenPressed(new UsePreset(Preset.tower));
		presetOnBatter.whenPressed(new UsePreset(Preset.onBatter));
		presetCourtyardMid.whenPressed(new UsePreset(Preset.courtyardMid));
		presetCourtyardFar.whenPressed(new UsePreset(Preset.courtyardFar));

		hoodPancake.toggleWhenActive(new ToggleHoodStop());
	}
}
