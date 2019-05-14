// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package robot.subsystems;

import robot.Robot;
//import robot.subsystems.*;
import robot.commands.*;
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.PIDOutput;
//import edu.wpi.first.wpilibj.PIDSource;
import robot.utils.*;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */


public class ElevSubSys extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    private WPI_TalonSRX elevMtr;
    private DigitalInput elevUpperLmtSw;
    private DigitalInput elevLwrLntSw;
    private AnalogPotentiometer elevPotentiometer;
    private DigitalInput elevUpper1LmtSw;
    private DigitalInput elevUpper2LmtSw;
    private DigitalInput elevLwr1LmtSw;
    private DigitalInput elevLwr2LmtSw;
    private Solenoid elevValve;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	
	public enum ElevDir {STOPPED, HOLD, RAISING, LOWERING};
	public ElevDir elevDir = ElevDir.STOPPED;

	private static enum ElevState {STOPPED, HOLD, RAISING, LOWERING}
	private static ElevState elevState = ElevState.STOPPED;
	private static double elev_State_Pwr = 0;
        
    //------------Constants-------------
    // --- default values ---
	private static final double ELEV_SAFE_HT_LMT = 8.0;    // Inches below which it is safe to Rotate +-45 degrees

    private static double kTopVoltDefault = 0.441;			//Voltage at top    //THESE DON'T UPDATE IN CODE
    private static double kBotVoltDefault = 3.935;			//Voltage at bottom
    private static double kTopVolt, kBotVolt, kVoltDiff ;

    private static final double ELEV_TOTAL_DIST = 60.0;	//Distance in inches covered by Pot.
    private static double kVoltRange= kBotVoltDefault - kTopVoltDefault;
    private static double ELEV_POT_CONV = (kVoltRange / ELEV_TOTAL_DIST); //Volts per inch

	// Limit switch true states
    //private static final  boolean KLIMIT_SWITCH_PRESSED = 		true;
	//private static final boolean KLIMIT_SWITCH_NOT_PRESSED = 	false;

	private static final boolean KLIMIT_LWR_SW1_PRESSED = 		false;
	//private static final boolean KLIMIT_LWR_SW1_NOT_PRESSED = 	true;
	private static final boolean KLIMIT_LWR_SW2_PRESSED = 		false;
	//private static final boolean KLIMIT_LWR_SW2_NOT_PRESSED = 	true;
	private static final boolean KLIMIT_UPPER_SW1_PRESSED = 	false;
	//private static final boolean KLIMIT_UPPER_SW1_NOT_PRESSED = true;
	private static final boolean KLIMIT_UPPER_SW2_PRESSED = 	false;
	//private static final boolean KLIMIT_UPPER_SW2_NOT_PRESSED = true;

    // Setup these constants as preferences in Roborio so we don't need to delineate  between
    // practice and competition robots
    
    public static final double KRaiseSpeedDefault =  +1.0;		// 
	public static final double KLowerSpeedDefault = -0.50;		// was -0.4
	private static final double LOWER_SPEED_LIMIT = -0.5;  
    public static final double KClimbSpeedDefault = -1.0;	
    public static final double KHoldSpeedDefault  = +0.20;		// Competition Bot
    public static double KRaiseSpeed, KLowerSpeed, KHoldSpeed;
	public static final double KRaiseSlowSpeed= 0.6;
    public static final double KLowerSlowSpeed= -.06;	
    public static final double KLimitElevTopPos= 56.0;
    public static final double KLimitElevBotPos= 2;
    public static final double BrakeOn = 0;
	public static final double BrakeOff = 1;
 
    // --------- PID Control Elements ----------------
    
	private static double kTgt_AngleDefault = 0 ;
	private static double kPDefault = 0 ;
	private static double kIDefault = 0 ;
	private static double kDDefault = 0 ;
	private static double kFDefault = 0 ;
	private static double kMaxOutDefault = 0 ;
	private static double kAbs_Tol_Default = 0 ;
	private static double kMoveRtDefault = 0;
	private static double kBBang_UpperDefault = 0 ;
	private static double kBBang_LowerDefault = 0 ; 
	
    static public double K_TGT_ANGLE = 0;			// elev Target Distance
    static public double KP = 0.049;			// elev P constant
    static public double KI = 0.0;				// elev P constant	
    static public double KD = 0.29;				// elev P constant
    static public double KF = 0.0;				// elev P constant
    static public double KMAXOUT = 1.0;			// elev Max Output
    static public double ABS_TOL = 1.0;			// elev Tolerence for Ending
    static public double KMOVERT = 3.6;			// elev Move Rate
    static public double KBBANG_UPPER = 0.4;	
    static public double KBBANG_LOWER = 0.01; 
	
	double kTgt_Angle, kP, kI, kD, kF, kMaxOut, kAbs_Tol, kMoveRt, kBBang_Upper, kBBang_Lower; 

    
 	public enum elevPIDMode {NULL, TELE, TOP, BOTTOM, SWITCH, SCALE, START, MOVING, HOLDING, RUNNING,  STOPPED};
    private elevPIDMode melevPIDMode = elevPIDMode.START ;  	// this keeps track of our current drive mode
    
    public enum elevPIDStatus {INIT, RUNNING, DONE, STOPPED, ATEND };
    private elevPIDStatus melevPIDStatus = elevPIDStatus.INIT;

    // --------- Various Constant and Variables ----------------
 	private double mCurrElevPos = 0;					// to be displayed on smartdash
 	private double mDistRemaining = 0;					// Distance remaining to target position
 	private double mCurrElevPwr = 0;					// to be displayed on smartdash
 	private double mCurrElevVolt = 0 ;					// Voltage of Pot
 	private double mLastPos = 0;
 	private int mPrintFlag = 0;							// log voltages once each time lmt sw is hit
 	//private int mBrakeFlag = 0;						// 0=Brake Off 1=Brake On
	private static boolean overrideFlag = false;		// false =No Override

    // ----------------- Encoder Conversion USED !!!! --------------------------------------------------------
	//private double k_EncConvConst = 2114.0;	//0.00064935  Comp Bot Talon Magnetic Encoder Conv inches/cnt used in 2019
	private double k_EncConvConst = 2084.28;		// 122973 mag encoder counts for 59 inches of travel
    // -------------------------------------------------------------------------------------------------------

	// --- For Gearshift valve
	public enum Gear {HI,LO};
	private Gear trans = Gear.HI;

	private String line;
	private static double  displayCtr = 1;      // used to refresh display on every 5 th. cycle ie) 100ms 10x/sec.
    
    // ------------------------------------------------------------------------
    // ------------------------ Elevator Constructor ------------------------
    // ------------------------------------------------------------------------
    
    public ElevSubSys() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        elevMtr = new WPI_TalonSRX(3);
        
        
        
        elevUpperLmtSw = new DigitalInput(22);
        addChild("ElevUpperLmtSw",elevUpperLmtSw);
        
        
        elevLwrLntSw = new DigitalInput(23);
        addChild("ElevLwrLntSw",elevLwrLntSw);
        
        
        elevPotentiometer = new AnalogPotentiometer(3, 5.0, 0.0);
        addChild("Elev Potentiometer",elevPotentiometer);
        
        
        elevUpper1LmtSw = new DigitalInput(6);
        addChild("ElevUpper1LmtSw",elevUpper1LmtSw);
        
        
        elevUpper2LmtSw = new DigitalInput(7);
        addChild("ElevUpper2LmtSw",elevUpper2LmtSw);
        
        
        elevLwr1LmtSw = new DigitalInput(8);
        addChild("ElevLwr1LmtSw",elevLwr1LmtSw);
        
        
        elevLwr2LmtSw = new DigitalInput(9);
        addChild("ElevLwr2LmtSw",elevLwr2LmtSw);
        
        
        elevValve = new Solenoid(0, 4);
        addChild("elevValve",elevValve);
        
        

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

		getElevPrefs();
		trans = Gear.LO;			// Set default startup to low gear
		initEncoder();

		elevMtr.setSafetyEnabled(false);
    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        setDefaultCommand(new ElevByJoystickCmd());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    }

    @Override
    public void periodic() {
		if (trans == Gear.HI) {
            elevValve.set(true);
        } else {
            elevValve.set(false);
		}

		if (isLowerLmtSw_Pressed() == true) resetEncoder();			// Set encoder to zero when elev is at bottom
		
        if ( displayCtr % 5 == 0) update_SmartDashboard();      			// Update dsiplay every 100 ms
        displayCtr++;
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

      
    // ******************* Elevator Control Motor Commands ************************
    public void elevStopMtr(){
        mCurrElevPwr = 0;
		elevMtr.set(mCurrElevPwr); 	
		elevState = ElevState.STOPPED;
	}
	
    public void elevHoldMtr(){
    	if (isLowerLmtSw_Pressed() == true) {
			mCurrElevPwr = 0;
			elevState = ElevState.STOPPED;	
    	} else {
			mCurrElevPwr = KHoldSpeed ;
			elevState = ElevState.HOLD;			
    	}
    	elevMtr.set(mCurrElevPwr);
	}
	    
    public void elevLower() {
		elevMoveMtr( KLowerSpeed, ElevDir.LOWERING);
    }
    
    public void elevRaise() {
		elevMoveMtr( KRaiseSpeed, ElevDir.RAISING);		
	}

    public void elevRaiseBySpeed(double raiseSpeed) {
		elevMoveMtr( raiseSpeed, ElevDir.RAISING);		
	}

	// -------------------------------------------------------------------
	// ---------------------- MAIN MOTION METHOD -------------------------
	// -------------------------------------------------------------------
    public void elevMoveMtr(double speed, ElevDir dir){
		if (Robot.oi.coPilotJoystick.getPOV(0) == 0){
			overrideFlag = true;
		} else {
			overrideFlag = false;
		}

		// -----------------------------------------------------------------------------
		// ---------------- Check for end of travel limit switchs hit ------------------
		if ((dir == ElevDir.RAISING) && (isUpperLmtSw_Pressed())) {
			elevHoldMtr();
			return;
	   }
	   if ((dir == ElevDir.LOWERING) && (isLowerLmtSw_Pressed())){    		
		elevStopMtr();
		return;
		}

		// -----------------------------------------------------------------------------
		// ------- Check for Lowereing too fast to prevent slamming into bottom --------		
		if (dir == ElevDir.LOWERING) {
			if ((speed < LOWER_SPEED_LIMIT ) && (overrideFlag == false)){
				speed = LOWER_SPEED_LIMIT;
			}
		}
		
		// -----------------------------------------------------------------------------
		// ---------- Check for Near End of travel and need for Slowing ----------------
    	if(dir == ElevDir.RAISING) {
			// This is for slowing down as we reach Top	
			if	((getElevPosition() >= KLimitElevTopPos) &&
				  (mCurrElevPwr	    >= KRaiseSlowSpeed)  && 
				  (overrideFlag == false)) {
						mCurrElevPwr = KRaiseSlowSpeed;
						elevMtr.set(mCurrElevPwr);
						elevState = ElevState.RAISING;
						return;						
    		}
		}
    	if( dir == ElevDir.LOWERING) {
			 // This is for slowing down as we reach Bottom
			if	((getElevPosition() <= KLimitElevBotPos) &&
				 (mCurrElevPwr      <= KLowerSlowSpeed)  &&
				  (overrideFlag == false)) {
						mCurrElevPwr = KLowerSlowSpeed;
						elevMtr.set(mCurrElevPwr);
						elevState = ElevState.LOWERING;
						return;	
			}
			
		}

		// -----------------------------------------------------------------------------
		// -------------- Check for Joystick power at zero SO Hold Elev ----------------
    	if (speed == 0) {
			// Joystick is at zero position so hold
			elevHoldMtr();
			return;
			} 


		// All OK so send power out to Motors
		mCurrElevPwr = speed;
		elevMtr.set(mCurrElevPwr);
		if( dir == ElevDir.LOWERING) elevState = ElevState.LOWERING;
		if( dir == ElevDir.RAISING)  elevState = ElevState.RAISING;
    }
    
	
    // ------------------------------------------------------------------------
    // ------------------------------ Pneumatic Methods -----------------------
    public void setGear(Gear gear)      { trans = gear; }
    public void setGearHI()             { trans = Gear.HI; }
    public void setGearLO()             { trans = Gear.LO; }
    public Gear getGear()               { return trans; }

    public boolean isInLowGear() {
		if (trans == Gear.LO) 	{ return true; }
		else 					{ return false; }
	}
	
	public boolean isInHighGear() {
		if (trans == Gear.HI)	{ return true ;	}
		else 					{ return false; }
	}
	
	
    // ------------------------------------------------------------------------
    // ------------------------ Encoder Methods -------------------------------
    void initEncoder() {
		elevMtr.setSensorPhase(false);   
    }

    public void resetEncoder() {				// Set Encoders to zero
		elevMtr.getSensorCollection().setQuadraturePosition(0, 10);
    }

	public int 		getElevEncoder()	 			{ return elevMtr.getSelectedSensorPosition(); }
	public double 	getElevPosition()		        { return (getElevEncoder() / k_EncConvConst); }
    //public int getElevEncoderVel() 				{ return elevMtr.getSelectedSensorVelocity(0); }    

    // ------------------------------------------
    // ---------  Get Limit Switch Data ---------
	// ------------------------------------------	
	
	// --------------------- Upper Limit Switch ------------------------
    public boolean isUpperLmtSw_Pressed() {
		if ( isUpperLmtSw1_Pressed() == true) 					return true;
		else													return false;
	}

	public boolean isUpperLmtSw_NotPressed() {
		if ( isUpperLmtSw_Pressed() == false)					return true;
		else													return false;
	}
	
	// --------------------- Lower Limit Switch ------------------------
	public boolean isLowerLmtSw_Pressed() {
		if	( isLowerLmtSw2_Pressed() == true) 					return true;
		else													return false;
	}
		
	public boolean isLowerLmtSw_NotPressed() {
		if ( isLowerLmtSw_Pressed() == false) 					return true;
		else													return false;
	}

	// -------------------------------------------------------------------	
	public boolean isUpperLmtSw1_Pressed() {
		if (elevUpper1LmtSw.get() == KLIMIT_UPPER_SW1_PRESSED) return true;
		else										 		   return false;
	}
	
	public boolean isUpperLmtSw2_Pressed() {
		if (elevUpper2LmtSw.get() == KLIMIT_UPPER_SW2_PRESSED) return true;
 		else										 		   return false;
	}
	
	public boolean isLowerLmtSw1_Pressed() {
		if (elevLwr1LmtSw.get() == KLIMIT_LWR_SW1_PRESSED)		return true;
 		else										 		  	return false;
	}
	
	public boolean isLowerLmtSw2_Pressed() {
		if (elevLwr2LmtSw.get() == KLIMIT_LWR_SW2_PRESSED)		return true;
 		else										 		  	return false;
	}
	// -------------------------------------------------------------------
    public double getPot(){
    	mCurrElevVolt = elevPotentiometer.get() ;
   	 	return mCurrElevVolt;
    }

	
    // ------------------------------------------------------------------------
    // ------------------------ Roborio Preferences Methods -----------------------
    public void getElevPrefs() {
     	// Init Potentiometer voltage points upon startup
    	kTopVolt = Robot.prefs.getDouble	("elev_40_Volt_Upper", kTopVoltDefault) ;
    	kBotVolt = Robot.prefs.getDouble	("elev_41_Volt_Lower", kBotVoltDefault) ;    	
    	if (isUpperLmtSw_Pressed()) {
    		kTopVolt =  getPot() ;					// if Robot Elev starts at Top calibrate at this time
    	} else if (isLowerLmtSw_Pressed()) {
    		kBotVolt =  getPot() ;					// if Robot Elev starts at Bottom calibrate at this time	
    	}
    	kVoltDiff = kBotVolt - kTopVolt ;
    	ELEV_POT_CONV = (kVoltDiff / ELEV_TOTAL_DIST); //Volts per inch
    	line = ("Elev Init kTopVolt=" + kTopVolt + " kBotVolt=" + kBotVolt + " ELEV_TOTAL_DIST=" + ELEV_TOTAL_DIST);
    	Robot.logger.appendLog(line);
    	System.out.println(line);

    	KRaiseSpeed  = Robot.prefs.getDouble	("elev_50_Raise_Pwr", KRaiseSpeedDefault) ;
    	KLowerSpeed = Robot.prefs.getDouble		("elev_51_Lower_Pwr", KLowerSpeedDefault) ;
    	KHoldSpeed = Robot.prefs.getDouble		("elev_52_Hold_Pwr", KHoldSpeedDefault) ;
    	// remove next 3 lines afet getting these into prefs
    	KRaiseSpeed  = KRaiseSpeedDefault ;
    	KLowerSpeed = KLowerSpeedDefault ;
    	KHoldSpeed =  KHoldSpeedDefault ;    	
    }


	// ------------------------- SmartDash board Methods ------------------------------------------
	public void update_SmartDashboard() {
     	if( isLowerLmtSw_Pressed())		SmartDashboard.putString("elev_LowerLmtSw", "Pressed") ;
		else			     			SmartDashboard.putString("elev_LowerLmtSw", "NOT Pressed") ;
		
     	if( isUpperLmtSw_Pressed())		SmartDashboard.putString("elev_UpperLmtSw", "Pressed") ;
     	else			     			SmartDashboard.putString("elev_UpperLmtSw", "NOT Pressed") ;
	 
		if( isLowerLmtSw1_Pressed())	SmartDashboard.putString("elev_LowerLmtSw1", "Pressed") ;
     	else			     			SmartDashboard.putString("elev_LowerLmtSw1", "NOT Pressed") ;

		if( isLowerLmtSw2_Pressed())	SmartDashboard.putString("elev_LowerLmtSw2", "Pressed") ;
     	else			     			SmartDashboard.putString("elev_LowerLmtSw2", "NOT Pressed") ;

     	if( isUpperLmtSw1_Pressed())	SmartDashboard.putString("elev_UpperLmtSw1", "Pressed") ;
     	else			     			SmartDashboard.putString("elev_UpperLmtSw1", "NOT Pressed") ;

     	if( isUpperLmtSw2_Pressed())	SmartDashboard.putString("elev_UpperLmtSw2", "Pressed") ;
		else			     			SmartDashboard.putString("elev_UpperLmtSw2", "NOT Pressed") ;
		 
		SmartDashboard.putNumber("elev_Position", 	Rmath.mRound(getElevPosition(),2)) ;
		SmartDashboard.putNumber("elev_Encoder",	Rmath.mRound(getElevEncoder(), 3)) ;
		      	
		SmartDashboard.putNumber("elev_CurrPwr", 	mCurrElevPwr) ;
		 
		if (trans == Gear.HI) 			SmartDashboard.putString("Elev_Gear", "High");
		else 							SmartDashboard.putString("Elev_Gear", "Low");
	}
	
	public void logData() {
		line =  "Debug - elev PID,";
    	line += " Tgt=," + kTgt_Angle;
    	line += " , Remaing Dist=," + (kTgt_Angle - mCurrElevPos);
    	line += " , CurrPos=," + Rmath.mRound(mCurrElevPos, 2);
    	line += " , CurrVolt=," + Rmath.mRound(mCurrElevVolt, 3);    	
    	line += " , PidOut=," + mCurrElevPwr;    	
    	line += " , LmtSw UL=,";
    	if (isUpperLmtSw_Pressed())
    		line += "T";
    	else
    		line += "F";
    	if (isLowerLmtSw_Pressed())
    		line += "T";
    	else
    		line += "F";
    	// ---- send line to Logger ---------
    	Robot.logger.appendLog(line);
	}
	
	public void logStartData() {
		line =  "Debug - elev PID STARTING,";
    	line += " Tgt=," + kTgt_Angle;		
    	// ---- send line to Logger ---------
    	Robot.logger.appendLog(line);
	}	
}

