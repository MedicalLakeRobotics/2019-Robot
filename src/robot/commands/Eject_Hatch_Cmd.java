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
import edu.wpi.first.wpilibj.command.Command;
import robot.Robot;

/**
 *
 */
public class Eject_Hatch_Cmd extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    private String line;
    private enum State { STOPPED, MOVING, DONE };
    private State state = State.STOPPED;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public Eject_Hatch_Cmd() {

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
        line = "Eject_Hatch_Cmd Called ";
        Robot.logger.appendLog(line); 
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        // TODO Re-instate this code when detect switch is installed
        //if(Robot.manipulatorSubSys.isHatchDetected() == false){
            // No Hatch to eject
        //    state = State.DONE;
        //    return;
        //}
        //if(Robot.manipulatorSubSys.inContact() == false){
            // Not Touching wall
        //    state = State.MOVING;
        //    return;
        //}

        // Have hatch, in contact, can eject
        Robot.manipulatorSubSys.setGrabberOpen();
        state = State.DONE;
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
        line = "Eject_Hatch_Cmd Ended ";
        Robot.logger.appendLog(line); 
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
