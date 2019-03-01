/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  public static OI m_oi;
  public static Tank_Drive kopchassis = new Tank_Drive();
  public static Hatch hatch;// = new Hatch();
  public static Cargo cargo;// = new Cargo();
  public static Climber climber;
  public static Camera camera;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    try {
      hatch = new Hatch();
    } catch (Exception ex) {
      System.out.println("Failed Create Hatch");
    }

    try {
      climber = new Climber();
    } catch (Exception ex) {
      System.out.println("Failed Create Climber");
    }

    try {
      cargo = new Cargo();
    } catch (Exception ex) {
      System.out.println("Failed Create Cargo");
    }

    try {
      camera = new Camera();
    } catch (Exception ex) {
      System.out.println("Failed Create Camera");
    }

    kopchassis.configDrive();

    m_oi = new OI();
    // m_chooser.setDefaultOption("Default Auto", new ExampleCommand());
    // chooser.addOption("My Auto", new MyAutoCommand());
    // SmartDashboard.putData("Auto mode", m_chooser);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This function is called once each time the robot enters Disabled mode. You
   * can use it to reset any subsystem information you want to clear when the
   * robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString code to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional commands to the
   * chooser code above (like the commented example) or additional comparisons to
   * the switch structure below with additional strings & commands.
   */

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    if (m_oi.operator != null) {
      System.out.println("****** OPERATOR JOYSTICK IS NOT NULL *********");
      int pov = m_oi.operator.getPOV();
      switch (pov) {
      case 0:
        Scheduler.getInstance().add(new CargoArmLevel3());
        break;
      case 90:
        Scheduler.getInstance().add(new CargoArmLevel2());
        break;
      case 180:
        Scheduler.getInstance().add(new CargoArmLevel1());
        break;
      case 270:
        Scheduler.getInstance().add(new CargoArmPark());
        break;
      }
    }

    Scheduler.getInstance().run();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
