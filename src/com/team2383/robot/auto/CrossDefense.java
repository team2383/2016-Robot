package com.team2383.robot.auto;

import com.team2383.robot.commands.DriveDistance;
import com.team2383.robot.subsystems.Drivetrain.Gear;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class CrossDefense extends CommandGroup {
	public CrossDefense() {
		addSequential(new WaitCommand(5));
		addSequential(new DriveDistance(1.0, -167, 4, 0.05, Gear.LOW, false));
	}
}
