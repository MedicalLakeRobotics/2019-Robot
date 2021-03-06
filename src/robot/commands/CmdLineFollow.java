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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.wpi.first.wpilibj.command.Command;
import robot.Robot;
import robot.utils.LineFollowController;

public class CmdLineFollow extends Command {
	
    // for computing deltaT for the controller
    // (this could also be done in the controller update method)
    private double mPreviousTime = -1;
    
    // the controller
    LineFollowController mController ;
    boolean mAbort = false ;

    // controller variables passed to this command and sent to 
    // the controller in initialize    
    double mStartOrientationDegCCW ;
    String mImageFname ;
    double mBaseAccel ;    // base drive power
    double mPixPerIn ;     // image scale in pix/in
    double mLineWidthInches ;  // width of the line in inches
    double mStartTheta ;   // start orient deg CCW relative to field
    double mEndTheta ;     // desired ending theta    

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public CmdLineFollow(
            double baseAccel,    // base drive power
            String path,         // filename for image of path
            double pix_per_in,   // image scale in pix/in
            double linewinches,  // width of the line in inches
            double startTheta,   // start orient deg CCW relative to field
            double endTheta) {   // desired ending theta
                                 // Pass Double.NaN if don't care

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING


        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.drivetrain);
        
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    	
        mStartOrientationDegCCW = startTheta ;
        mImageFname = path ; 
        mBaseAccel = baseAccel ;    // base drive power
        mPixPerIn = pix_per_in;     // image scale in pix/in
        mLineWidthInches = linewinches ;  // width of the line in inches
        mStartTheta = startTheta ;   // start orient deg CCW relative to field
        mEndTheta = endTheta;     // desired ending theta    
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        // load the path image
        mAbort = false ; 
        BufferedImage img = null ;
        try {
            img = ImageIO.read(new File("/home/lvuser/" + mImageFname)) ;
        } catch(IOException e) {
            // TODO: change this to go into a logging file?
            System.out.println("error loading path: "+e.getMessage());
            mAbort = true ;
            return ;
        }
               
        // create and pass params to a controller
        // the init call could move to the command initialize() method
        // at the cost of storing more member vars
        mController = LineFollowController.getInstance() ;
        double x = Robot.drivetrain.getXinFt() ;
        double y = Robot.drivetrain.getYinFt() ;
        double orient = Robot.drivetrain.getOrientDegCCW() ;
        mController.init(
                        mBaseAccel,   // base drive power
                        img,          // image of path
                        mPixPerIn,    // image scale in pix/in
                        mLineWidthInches,   // width of the line in inches
                        mEndTheta,    // final desired angle (Double.Nan if don't care)
                        x, y,         // current location (usually 0,0)
                        orient) ;     // current orientation (usually 90)
       
        Robot.drivetrain.tankDrive(mBaseAccel, mBaseAccel);
        Robot.drivetrain.setInitialOrientationDegCCW(mStartOrientationDegCCW);        
    	Robot.drivetrain.resetGyro();    	
    	Robot.drivetrain.resetEncodersAndStats();    	
    	Robot.drivetrain.resetPosition(true); 
 
        // initialize for computing deltaT
        mPreviousTime = System.currentTimeMillis(); 
        mController.start();
        
        System.out.println("CmdLineFollow Init");
        Robot.logger.appendLog("CmdLineFollow Init");
    	Robot.drivetrain.setLoggingOn();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
        if (mAbort) return ;
        // get deltaT in seconds for the controller
        double now = System.currentTimeMillis();
        double elapsed = (now - mPreviousTime) / 1000.0 ;        
        mPreviousTime = now;
        
        double vel = Robot.drivetrain.getVelocityInFtPerSec() ;
        double x = Robot.drivetrain.getXinFt() ;
        double y = Robot.drivetrain.getYinFt() ;
        double orient = Robot.drivetrain.getOrientDegCCW() ;
        LineFollowController.MotorControlStruct 
                ctrl =  mController.update(x,y,orient,vel,elapsed);
        
        Robot.drivetrain.tankDrive(ctrl.left, ctrl.right);

    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        // let the controller decide when it is finished
        // because the logic depends on state information not known here
        return (mAbort || mController.isFinished()) ;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() { 
        if (mAbort) return ; 
        mController.stop();
        Robot.drivetrain.tankDrive(0, 0);
        System.out.println("CmdLineFollow End");
        Robot.logger.appendLog("CmdLineFollow End");   	
        Robot.drivetrain.setLoggingOff();
    	Robot.logger.appendLog("CmdGrpLineFollow.log");
   }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    	end() ;
    }
    
    @Override 
    public String toString() {
        return "CmdLineFollow" ;
    }
}
