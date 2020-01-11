/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private DifferentialDrive m_myRobot;
  private Joystick m_Stick;
  VictorSP LeftFrontDrive = new VictorSP(0);
  VictorSP LeftBackDrive = new VictorSP(1);
  VictorSP RightFrontDrive = new VictorSP(2);
  VictorSP RightBackDrive = new VictorSP(3);
  Spark PivotMotor = new Spark(9);
  SpeedControllerGroup m_right = new SpeedControllerGroup(LeftBackDrive, LeftFrontDrive);
  SpeedControllerGroup m_left = new SpeedControllerGroup(RightBackDrive, RightFrontDrive);
  long button1_time;
  DoubleSolenoid LaunchShirt1 = new DoubleSolenoid(4, 0, 1);
  DoubleSolenoid LaunchShirt2 = new DoubleSolenoid(4, 2, 3);
  DoubleSolenoid.Value LaunchShirtValue;


  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    m_myRobot = new DifferentialDrive(m_left, m_right);
    m_Stick = new Joystick(0);
    LaunchShirt1.set(DoubleSolenoid.Value.kReverse);
    LaunchShirt2.set(DoubleSolenoid.Value.kReverse);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // autoSelected = SmartDashboard.getString("Auto Selector",
    // defaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
    button1_time = System.currentTimeMillis();

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    // Drive Robot
    m_myRobot.arcadeDrive(m_Stick.getY(), m_Stick.getX());	

    // Launch Shirt
    if (m_Stick.getRawButton(1)) {
      if (LaunchShirtValue == DoubleSolenoid.Value.kForward) {
        LaunchShirt1.set(DoubleSolenoid.Value.kReverse);
        LaunchShirt2.set(DoubleSolenoid.Value.kReverse);
      } else {
        LaunchShirt1.set(DoubleSolenoid.Value.kForward);
        LaunchShirt2.set(DoubleSolenoid.Value.kForward);
      }
      button1_time = System.currentTimeMillis();
    } else {
      if (System.currentTimeMillis() - button1_time > 250) {
        LaunchShirtValue = LaunchShirt1.get();
      }
    }

    // Pivot Motor
    double PivotDown = +1 * m_Stick.getRawAxis(3); /* positive is down */
    double PivotUp = -1 * m_Stick.getRawAxis(2); /* negative is up */

    // Pivot Motor - Set Deadband
    if (Math.abs(PivotDown) < 0.10) {
      PivotDown = 0;
    }
    if (Math.abs(PivotUp) < 0.10) {
      PivotUp = 0;
    }

    // Pivot Motor - Both Triggers Pulled?
    if (Math.abs(PivotDown) > 0 && Math.abs(PivotUp) > 0) {
      PivotMotor.set(0);
    } else {
      if (Math.abs(PivotDown) > 0) {
        PivotMotor.set(0.5 * PivotDown);
      }
      if (Math.abs(PivotUp) > 0) {
        PivotMotor.set(0.5 * PivotUp);
      }
      if (PivotDown == 0 && PivotUp == 0) {
        PivotMotor.set(0);
      }
    }

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
