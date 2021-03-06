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
public class Intake_RollerMotor_Set_Cmd extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    private String m_mode;
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    private String line;


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public Intake_RollerMotor_Set_Cmd(String mode) {
  
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        m_mode = mode;

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.intakeSubSys);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        line = "Intake_RollerMotor_Set_Cmd Called   mode=" + m_mode;
        Robot.logger.appendLog(line);
        System.out.println(line);
        if      (m_mode == "Retract")  Robot.intakeSubSys.rollerMotorSetRetract();
        else if (m_mode == "Eject")    Robot.intakeSubSys.rollerMotorSetEject();
        else                           Robot.intakeSubSys.rollerMotorStop();
    }
    //intakeMtrStop
    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
        line = "Intake_RollerMotor_Set_Cmd Ended";
        Robot.logger.appendLog(line);
        System.out.println(line);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
