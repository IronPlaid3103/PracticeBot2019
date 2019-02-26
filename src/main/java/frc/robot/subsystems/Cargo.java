/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.robot.commands.CargoStop;

/**
 * Add your docs here.
 */
public class Cargo extends Subsystem {

  WPI_TalonSRX armTalon1 = new WPI_TalonSRX(RobotMap.cargoarmTalon1);
  WPI_TalonSRX armTalon2 = new WPI_TalonSRX(RobotMap.cargoarmTalon2);
  WPI_TalonSRX cargomechTalon1 = new WPI_TalonSRX(RobotMap.cargomechTalon1);
  WPI_TalonSRX cargomechTalon2 = new WPI_TalonSRX(RobotMap.cargomechTalon2);

  Solenoid brake = new Solenoid(RobotMap.cargoBrakeSolenoid);

  //https://www.chiefdelphi.com/t/talonsrx-isfinished-for-close-loop-control/340082
  int finalPositionCounter = 0;
  int intakeHoldCounter = 0;

  int intakeRampCounter = 0;

  double maxCurrent = 0;

  public Cargo() {
    armTalon1.setInverted(false);

    armTalon1.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 10);

    //https://phoenix-documentation.readthedocs.io/en/latest/ch16_ClosedLoop.html
    //TODO: instead of setting these here first, tune them using Phoenix Tuner, then set here in code using those values
    //NOTE: These are saved directly on the Talon and remain there even after power off - need to be adjusted by code or Phoenix Tuner
    armTalon1.config_kP(0, .1);
    armTalon1.config_kI(0, 0);
    armTalon1.config_kD(0, 0);
    armTalon1.config_kF(0, 0);

    armTalon1.setSensorPhase(true);

    armTalon1.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, 30);

    armTalon2.follow(armTalon1);

    cargomechTalon1.setInverted(true);
    cargomechTalon2.setInverted(false);

    cargomechTalon2.follow(cargomechTalon1);
  }

  public void setArmAngle(double angle) {
    brake.set(false);
    armTalon1.set(ControlMode.Position, (angle * 1024.0 / 360.0));
    int error = armTalon1.getClosedLoopError();

    if (error < RobotMap.cargoArmAngleTolerance * 1024.0 / 360.0)
      finalPositionCounter++;
    else
      finalPositionCounter = 0; // reset if we went pass the threshold
  }

  public boolean isArmAtPosition() {
    //TODO: determine if this is a sufficient number of counts/loops (5 ~= 100ms)
    if (finalPositionCounter > 5)
      return true;
    else
      return false;
  }

  public void cargobrake() {
    finalPositionCounter = 0;
    armTalon1.set(ControlMode.PercentOutput, 0);
    brake.set(true);
  }

  public void up() {
    brake.set(false);
    armTalon1.set(ControlMode.PercentOutput, 1);
  }

  public void down() {
    brake.set(false);
    armTalon1.set(ControlMode.PercentOutput, -1);
  }


  public void intake() {
    cargomechTalon1.set(ControlMode.PercentOutput, -1);
    
    //TODO: we may want to track current from both talons - maybe do an OR condition and increment holdCounter if either one is above threshold (I don't think we want an AND condition)
    double current = cargomechTalon1.getOutputCurrent();
    //TODO: current and/or rampCounter thresholds may need to be adjusted after real world testing with the cargo grabber installed on a real bot
    if(current > 7.0 && intakeRampCounter > 25)
      intakeHoldCounter++;
    else
      intakeHoldCounter = 0;

    if(current > maxCurrent) {
      maxCurrent = current;
    }

    intakeRampCounter++;
  }

  public boolean holdingCargo() {
    //TODO: holdCounter may need to be adjusted after real world testing - increasing will wait longer after we think we have the cargo
    if(intakeHoldCounter > 5)
      return true;
    else {
      return false;
    }
  }

  public void deliver() {
    cargomechTalon1.set(ControlMode.PercentOutput, 1);
  }

  public void stop() {
    cargomechTalon1.set(ControlMode.PercentOutput, 0);
    armTalon1.set(ControlMode.PercentOutput, 0);
    intakeHoldCounter = 0;
    intakeRampCounter = 0;
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new CargoStop());
  }
}