package frc.robot.subsystems.elevator;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import frc.robot.Constants;

public class ElevatorIOSim2 implements ElevatorIO {
  private FlywheelSim flywheelSim = new FlywheelSim(DCMotor.getNEO(1), 1.5, 0.004);
  private PIDController pid = new PIDController(0.0, 0.0, 0.0);

  private boolean closedLoop = false;
  private double ffVolts = 0.0;
  private double appliedVolts = 0.0;

  @Override
  public void updateInputs(ElevatorIOInputs inputs) {
    if (closedLoop) {
      appliedVolts = MathUtil.clamp(
          pid.calculate(flywheelSim.getAngularVelocityRadPerSec()) + ffVolts, -12.0,
          12.0);
      flywheelSim.setInputVoltage(appliedVolts);
    }

    flywheelSim.update(0.02);
    //double elevator position
    inputs.positionRad = flywheelSim.getAngularVelocityRadPerSec() * Constants.simLoopPeriodSecs;

    inputs.velocityRadPerSec = flywheelSim.getAngularVelocityRadPerSec();
    inputs.appliedVolts = appliedVolts;
    inputs.currentAmps = flywheelSim.getCurrentDrawAmps();
  }

  @Override
  public void setPosition(double positionInch, double ffVolts) {

  }

  @Override
  public void holdCurrent(int amps) {
    flywheelSim.setInputVoltage(4.0 / 12.0);
  }

  @Override
  public void stop() {
    closedLoop = false;
    appliedVolts = 0.0;
    flywheelSim.setInputVoltage(0.0);
  }

  public void configurePID(double kP, double kI, double kD) {
    pid.setPID(kP, kI, kD);
  }
}