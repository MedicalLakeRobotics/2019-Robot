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
import robot.*;
//import robot.subsystems.ManipulatorSubSys;

/**
 *
 */
public class ElevToPosCmd extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS
    
    private String m_position;
    private double m_distance;
    private double m_holdMode;
    private double m_timeOut;
 
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

	// m_holdMode not used
    String line;
    double mCurrPos = 0;
	double mTgtPos;
	double tgt;
    //int mDir;
	//int state;	// State 0=stopped, 1=Reached Target Position, 9=

	private static enum MoveState {STOPPED , DONE , MOVING_TO_TOP , MOVING_TO_BOTTOM , MOVING_TO_POSITION, HOLDING};
	private static MoveState moveState = MoveState.STOPPED;

	private static enum MoveDir {RAISING , LOWERING, STOPPED, HOLDING};
	private static MoveDir moveDir = MoveDir.STOPPED;
	
	// Elev position is in inches 0 = bottom 60=top
	
	public static final double ELEV_TOP_VALUE = 59;						// 60 on practice bot
	public static final double ELEV_BOTTOM_VALUE = 0;

	public static final double ELEV_LWR_HATCH_VALUE = 2.25;				// 2.88 mag cnt 10122 
	public static final double ELEV_EJECT_LWR_CARGO_VALUE = 15.33;		// 16.33 mag cnt 33879
	public static final double ELEV_EJECT_CARGOSHIP_CARGO_VALUE = 27.5;	// 26 testing in progress

	public static final double ELEV_EJECT_MID_HATCH_VALUE = 30.0;		// 30.5 mag cnt 65189 
	public static final double ELEV_EJECT_MID_CARGO_VALUE = 46.0;		// 28 mag cnt 94032

	public static final double ELEV_EJECT_HIGH_HATCH_VALUE = 59.0;		// 60 mag cnt 10413
	public static final double ELEV_EJECT_HIGH_CARGO_VALUE = 59.0;		// 60 Top

	public static final double ELEV_RETREIVE_CARGO_LS_VALUE = 43;		// 44mag cnt 62652
	public static final double ELEV_RETREIVE_CARGO_FLOOR_VALUE = 0;

	// position (mode) parameter :
	//		Bottom, Top, Position
	//		Lwr -> 	( Provides Lwr Hatch Eject/Retract, Cargo Eject Position )
	//		Mid-> 	( Provides Mid Hatch Eject, Cargo Eject Position )
	//		High-> 	( Provides High Hatch Eject Position )
	//		LS-> 	( Provides Loading Station Hatch Retract, Cargo Retract Position )
	//		Floor-> ( Provides Floor Hatch Retract, Cargo Retract Position )
	//		CargoshipBay-> (Provides Cargo Eject Position for the Cargoship Bay)


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
    public ElevToPosCmd(String position, double distance, int holdMode, double timeOut) {

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        m_position = position;
        m_distance = distance;
        m_holdMode = holdMode;
        m_timeOut = timeOut;

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.elevSubSys);

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    
    }

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        //	 holdMode = 0, drive to position then exit
    	//   holdMode = 1, drive to position then hold with no return,

		setTimeout(m_timeOut);
		
		mCurrPos = Robot.elevSubSys.getElevPosition();
		mTgtPos = initCmd();		// Look up target position based on string

    	line =  "Elev To Position Cmd Init!   Target=" + m_position  + " distance=" + m_distance + 
				" at (" + mTgtPos + ")  (CurrPos=" + mCurrPos + ")  m_hold Mode = " + m_holdMode;
		System.out.println(line) ;
		Robot.logger.appendLog(line);

		moveState = MoveState.STOPPED;
		
		if (m_position == "Top"){
			moveState = MoveState.MOVING_TO_TOP;
			moveDir = MoveDir.RAISING;
			return;
		} 

		if (m_position == "Bottom"){
			moveState = MoveState.MOVING_TO_BOTTOM;
			moveDir = MoveDir.LOWERING;
			return;
		}
	
    	if (Math.abs(mCurrPos - mTgtPos) <= 0.25) {
			// were within +- 0.25 inches of target ... no need to move
			moveState = MoveState.DONE;
    		return;
		}
		
		moveState = MoveState.MOVING_TO_POSITION;
    	if (mCurrPos <=  mTgtPos )		{ moveDir = MoveDir.RAISING; } 			// We need to raise to target
		else 							{ moveDir = MoveDir.LOWERING; } 		// We need to lower to target
	}

    // Called repeatedly when this Command is scheduled to run
    @Override
    protected void execute() {
		mCurrPos = Robot.elevSubSys.getElevPosition();

		if (moveDir == MoveDir.LOWERING){
			if (Robot.elevSubSys.isLowerLmtSw_NotPressed()) {
				// continue lowereing
				Robot.elevSubSys.elevLower();
			} else {
				// we have hit bottom so were done!
				Robot.elevSubSys.elevStopMtr();
				moveState = MoveState.DONE;
				return;
			}
		}

		if (moveDir == MoveDir.RAISING){
			if (Robot.elevSubSys.isUpperLmtSw_NotPressed()) {
				// continue raiseing
				Robot.elevSubSys.elevRaise();
			} else {
				// we have hit Top so were done!
				Robot.elevSubSys.elevHoldMtr();
				moveState = MoveState.DONE;
				return;
			}
		}

		if (moveState == MoveState.MOVING_TO_POSITION) {
    		if (moveDir == MoveDir.RAISING) {
    			if ( mCurrPos < (mTgtPos - 1.5) ) {
					// continue raiseing were not there yet
					  // Robot.elevSubSys.elevRaise();
					   if ( tgt == ELEV_LWR_HATCH_VALUE ){
						   // if the tartget is the lowest hatch position dont use full speed
						   Robot.elevSubSys.elevRaiseBySpeed(0.75);
					   } else {
						   // were going to higher positions use full speed
						   Robot.elevSubSys.elevRaise();
					   }
    			} else {
					// we have reached our tgt position so were done!
					Robot.elevSubSys.elevHoldMtr();
					moveState = MoveState.DONE;
					return;
    			}
			}
			if (moveDir == MoveDir.LOWERING) {		
    			if ( mCurrPos > (mTgtPos + 1))  {
					// continue lowering were not there yet
					Robot.elevSubSys.elevLower();
    			} else {
					// we have reached our tgt position so were done!
					Robot.elevSubSys.elevHoldMtr();
					moveState = MoveState.DONE;
					return;							
    			}
    		}
		}
		
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean isFinished() {
        if (isTimedOut()) {
    		// Cmd has timed out
    		line = "ElevToPosCmd - Has TIMED OUT !!";
    		Robot.logger.appendLog(line);
    		System.out.println(line) ; 
    		return true;				// used in all modes
    	}
    	if (moveState == MoveState.DONE) return true; 	// we can exit    	

        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
		// In case we are interupted send hold or stop motor
    	if (Robot.elevSubSys.isLowerLmtSw_NotPressed()) {
			Robot.elevSubSys.elevHoldMtr();
    	} else {
    		Robot.elevSubSys.elevStopMtr();
    	}
    	line = "Elev To Position Cmd at end!" ;
    	System.out.println(line) ;
    	Robot.logger.appendLog(line);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
        end();
    }

       
    public double initCmd(){
		//		Bottom, Top, Position
		//		HatchFloor, HatchLow, HatchMid, HatchHigh
		//		CargoFloor, CargoLS, CargoLow, CargoMid
		//		CargoShipBay
    	tgt = 0;
    	switch ( m_position ) {

		case "Position" :
			tgt = m_distance ;
			break;

		case "Top" :
			tgt = ELEV_TOP_VALUE ;			
			break ;
    	case "Bottom" :
			tgt = ELEV_BOTTOM_VALUE ;
			break ;

		case "CargoShipBay" :
			tgt = ELEV_EJECT_CARGOSHIP_CARGO_VALUE ;
			break ;

		case "Lwr" :
			if (Robot.manipulatorSubSys.isHatchModeSelected() == true) {
				tgt = ELEV_LWR_HATCH_VALUE ;
			} else {
				tgt = ELEV_EJECT_LWR_CARGO_VALUE ;
			}
			break ;		

		case "Mid" :
			if (Robot.manipulatorSubSys.isHatchModeSelected() == true) {
				tgt = ELEV_EJECT_MID_HATCH_VALUE ;
			} else {
				tgt = ELEV_EJECT_MID_CARGO_VALUE ;
			}
			break ;

		case "High" :
			if (Robot.manipulatorSubSys.isHatchModeSelected() == true) {
				tgt = ELEV_EJECT_HIGH_HATCH_VALUE ;
			} else {
				tgt = ELEV_EJECT_HIGH_CARGO_VALUE ;
			}
			break ;

		case "LS" :
			if (Robot.manipulatorSubSys.isHatchModeSelected() == true) {
				tgt = ELEV_LWR_HATCH_VALUE ;
			} else {
				tgt = ELEV_RETREIVE_CARGO_LS_VALUE ;
			}
			break ;

		case "Floor" :
			if (Robot.manipulatorSubSys.isHatchModeSelected() == true) {
				tgt = ELEV_LWR_HATCH_VALUE ;
			} else {
				tgt = ELEV_RETREIVE_CARGO_FLOOR_VALUE;
			}
			break ;
			
    	default:
    		tgt = 0;
		}
		
		line = "Elev (" + m_position + ") Position selected! Pos=(" + tgt + ")";
		System.out.println(line);
		Robot.logger.appendLog(line);
    	return tgt;
    }
}