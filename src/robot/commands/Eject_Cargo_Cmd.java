// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import robot.Robot;

/**
 *
 */
public class Eject_Cargo_Cmd extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
   
    private String line;
    private enum State { STOPPED, WAITING, MOVING, DONE, EJECTING };
    private State state = State.STOPPED;
    private Timer ejectTimer = new Timer();
    private static final double EJECTTIME = 0.5;     // Seconds to eject cargo

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public Eject_Cargo_Cmd() {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.manipulatorSubSys);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        line = "Eject_Cargo_Cmd Called ";
        Robot.logger.appendLog(line);
        state = State.STOPPED; 
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {

        if (state == State.STOPPED) {
            // Were at beginning check to see if we have cargo
            ejectTimer.start();
            ejectTimer.reset();
            Robot.manipulatorSubSys.cargoMtrEject(); 
            state = State.EJECTING;
        }

        if (state == State.EJECTING) {
            if (ejectTimer.get() > EJECTTIME) {
                // We have timed out so we have completed ejecting
                Robot.manipulatorSubSys.cargoMtrStop();
                Robot.logger.appendLogPosition("Cargo Ejecting Position");
                state = State.DONE; 
                return;
            } else {
                // continue eject motors
                Robot.manipulatorSubSys.cargoMtrEject();
            }    
        }

    }
    

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (state == State.DONE){
            return true;
        }
    return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        Robot.manipulatorSubSys.cargoMtrStop();
        line = "Eject_Cargo_Cmd Ended ";
        Robot.logger.appendLog(line); 
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        end();
    }
}
