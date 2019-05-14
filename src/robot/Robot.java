// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.commands.*;
import robot.subsystems.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;

import robot.utils.Map;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in 
 * the project.
 */
public class Robot extends TimedRobot {
	//public static PowerDistributionPanel pdp = new PowerDistributionPanel();
 
    //Command autonomousCommand;
    Command autoCmd;
    
    SendableChooser<Command> chooser = new SendableChooser<Command>();
    SendableChooser<String> locChooser = new SendableChooser<String>();
    SendableChooser<String> orientChooser = new SendableChooser<String>();
    SendableChooser<String> xBoxLRChooser = new SendableChooser<String>();
    SendableChooser<String> dualChooser = new SendableChooser<String>();  
    SendableChooser<String> firstChooser = new SendableChooser<String>(); 
    SendableChooser<String> rocketChooser = new SendableChooser<String>(); 

    public static Timer sysTimer = new Timer();
	String fmsGameData;
	Boolean switchLeftLit, scaleLeftLit, switchRightLit, scaleRightLit;
	Boolean printBatVoltFlag;
	public static Preferences prefs;
    String line;
    double logCntr = 0;

    // Robot Position States enums
    public static enum RobotPosState {TRAVEL, EJECT_MAIN, EJECT_ROCKET_MID, EJECT_ROCKET_HIGH, EJECT_HATCH_FWD,
                                              RETREIVE_CARGO, RETREIVE_HATCH_FLOOR, RETREIVE_HATCH_LS, 
                                              CARGO_HOLD, HATCH_HOLD, OTHER};

    public static RobotPosState robotPosState = RobotPosState.TRAVEL;
    
    public static OI oi;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static Drivetrain drivetrain;
    public static Logger logger;
    public static UDPServerSubSys uDPServerSubSys;
    public static ElevSubSys elevSubSys;
    public static ManipulatorSubSys manipulatorSubSys;
    public static IntakeSubSys intakeSubSys;
    public static VisionSubSys visionSubSys;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        //RobotMap.init();
        printBatVoltFlag = true;
        prefs = Preferences.getInstance();

        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        drivetrain = new Drivetrain();
        logger = new Logger();
        uDPServerSubSys = new UDPServerSubSys();
        elevSubSys = new ElevSubSys();
        manipulatorSubSys = new ManipulatorSubSys();
        intakeSubSys = new IntakeSubSys();
        visionSubSys = new VisionSubSys();

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    uDPServerSubSys.startUdpServer((int)5801);
    // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
        oi = new OI();

        // Add commands to Autonomous Sendable Chooser
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS

        chooser.setDefaultOption("Autonomous Command", new AutonomousCommand());

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS
  
    /***/
        SmartDashboard.putNumber("BEARING_PID_KP", .01) ;
        SmartDashboard.putNumber("BEARING_PID_KD", 0) ;
        SmartDashboard.putNumber("BEARING_PID_BB_UPPER", 0) ;
        SmartDashboard.putNumber("BEARING_PID_BB_LOWER", 0) ;
        SmartDashboard.putNumber("DISTANCE_PID_KP", 2) ;
        SmartDashboard.putNumber("DISTANCE_PID_KD", .5) ;     
    /***/

        SmartDashboard.putData("Auto mode", chooser);

        locChooser.addOption  ("0 Left Level 1", 	"Left Level 1");
    	locChooser.addOption  ("1 Center",	"Ctr");
        locChooser.addOption  ("2 Right Level 1", 	"Right Level 1");
        locChooser.addOption  ("3 Left Level 2", 	"Left Level 2");
        locChooser.addOption  ("4 Right Level 2", 	"Right Level 2");
        locChooser.setDefaultOption("0 Left Level 1", 	"Left Level 1");
        SmartDashboard.putData("Location-Choice",locChooser);
        
        orientChooser.addOption  ("0 Left", 	"Left");
    	orientChooser.addOption  ("1 Forward",	"Fwd");
        orientChooser.addOption  ("2 Right", 	"Right");
        orientChooser.setDefaultOption("0 Left", 	"Left");
        SmartDashboard.putData("Orient-Choice",orientChooser);

        xBoxLRChooser.addOption  ("0 Left", 	"Left");
        xBoxLRChooser.addOption  ("1 Right", 	"Right");
        xBoxLRChooser.setDefaultOption("1 Right", 	"Right");
        SmartDashboard.putData("xBoxLR-Choice",xBoxLRChooser);

        firstChooser.addOption ("0 Do Nothing", "Nothing");
        firstChooser.addOption("1 Off Pad Only",	"OffPad"); 
    	firstChooser.addOption ("2 Rocket Near Hatch", "NearHatch");
        firstChooser.addOption ("3 Rocket Far Hatch", 	"FarHatch");
        firstChooser.setDefaultOption ("0 Do Nothing", "Nothing");
        SmartDashboard.putData ("First-Choice", firstChooser);
        
        rocketChooser.addOption ("0 Left Rocket", 	"Left");
        rocketChooser.addOption  ("1 Right Rocket",	"Right");
        rocketChooser.setDefaultOption ("0 Left Rocket", 	"Left");
    	SmartDashboard.putData("Rocket-Choice",rocketChooser);
    	    	    	
    	dualChooser.addOption ("0 Single Hatch",   "SingleHatch");    	
        dualChooser.addOption  ("1 Dual Hatch", "DualHatch"); 
        dualChooser.setDefaultOption ("0 Single Hatch",   "SingleHatch");
    	SmartDashboard.putData ("Single/Dual Hatch",dualChooser);
    	
    	userInit();
    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    @Override
    public void disabledInit(){
        Robot.manipulatorSubSys.cargoMtrStop();
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        Robot.logger.startTimer();        // Starts logging timer        
        
        setMotorValveToDefaultPos();
        
        autoCmd = null;         // NO auto routine
        //autoCmd = chooser.getSelected();
        //if (autoCmd != null) autoCmd.start();
    	//sysTimer.reset();			// System timer for Competition run
    	//sysTimer.start();   
    	userInit();
    	
    	line = "******************  AutonomousInit started ******************";
    	Robot.logger.appendLog(line);
    	System.out.println(line);
    	//line = "  ***** Battery Voltage = " + pdp.getVoltage() + " *****";
    	//Robot.logger.appendLog(line);
    	//System.out.println(line);    	
        
        
        String locChoice = locChooser.getSelected();
        String orientChoice = orientChooser.getSelected();    	    	
        String xBoxLRChoice = xBoxLRChooser.getSelected();

        String firstChoice = firstChooser.getSelected();
        String dualChoice = dualChooser.getSelected();
        String rocketChoice = rocketChooser.getSelected();
  	
        line = "*** Autonomous Start " +
                            " Loc=" + locChoice +
                            " Orient=" + orientChoice +
                            " xBoxLR=" + xBoxLRChoice +

                            " First Choice= " +firstChoice +
                            " Dual=" + dualChoice +
                            " RocketCh=" + rocketChoice;

    	System.out.println(line);
        Robot.logger.appendLog(line);
            	
        line = " loc Choice= "    + locChoice +  " Orient Choice= "    + orientChoice;
    	System.out.println(line);
		Robot.logger.appendLog(line);
          
    	// ----------------------------------------------------------
    	// ------- Stqrting Location Choice options -----------------
        // ----------------------------------------------------------
        
        double rbt_Pos_x = 0;   // X position on playfield
        double rbt_Pos_y = 0;   // Y position on playfield
        double rbt_Pos_o = 0;   // Robot Orientation 0=fwd, -90=left facing, 90=right facing

    	if ((orientChoice.equals("Fwd")) && (locChoice.equals("Left Level 1"))){
            rbt_Pos_x = Map.StartingPos_Left_FacingFwd_X;
            rbt_Pos_y = Map.StartingPos_Left_FacingFwd_Y;
            rbt_Pos_o = Map.StartingPos_Left_FacingFwd_O;
    	}       

    	if ((orientChoice.equals("Left")) && (locChoice.equals("Left Level 1"))){
            rbt_Pos_x = Map.StartingPos_Left_FacingLeft_X;
            rbt_Pos_y = Map.StartingPos_Left_FacingLeft_Y;
            rbt_Pos_o = Map.StartingPos_Left_FacingLeft_O;
        } 

        if (xBoxLRChoice.equals("Left"))
            Robot.drivetrain.setXBoxLeftMode();
        else
            Robot.drivetrain.setXBoxRightMode();

    	if (locChoice.equals("Ctr")){
            rbt_Pos_x = Map.StartingPos_Ctr_FacingFwd_X;
            rbt_Pos_y = Map.StartingPos_Ctr_FacingFwd_Y;
            rbt_Pos_o = Map.StartingPos_Ctr_FacingFwd_O;
        } 
        
    	if ((orientChoice.equals("Fwd")) && (locChoice.equals("Right Level 1"))){
            rbt_Pos_x = Map.StartingPos_Right_FacingFwd_X;
            rbt_Pos_y = Map.StartingPos_Right_FacingFwd_Y;
            rbt_Pos_o = Map.StartingPos_Right_FacingFwd_O;
        } 
        
    	if ((orientChoice.equals("Right")) && (locChoice.equals("Right Level 1"))){
            rbt_Pos_x = Map.StartingPos_Right_FacingRight_X;
            rbt_Pos_y = Map.StartingPos_Right_FacingRight_Y;
            rbt_Pos_o = Map.StartingPos_Right_FacingRight_O;
        } 

        // Level 2
        if (locChoice.equals("Left Level 2")){
            rbt_Pos_x = Map.StartingPos_Level2_Left_Fwd_FacingLeft_X;
            rbt_Pos_y = Map.StartingPos_Level2_Left_Fwd_FacingLeft_Y;
            rbt_Pos_o = Map.StartingPos_Level2_Left_Fwd_FacingLeft_O;
        } 
        if (locChoice.equals("Right Level 2")){
            rbt_Pos_x = Map.StartingPos_Level2_Right_Fwd_FacingLeft_X;
            rbt_Pos_y = Map.StartingPos_Level2_Right_Fwd_FacingLeft_Y;
            rbt_Pos_o = Map.StartingPos_Level2_Right_Fwd_FacingLeft_O;
        }
     	
        line = " Starting Position X=" + rbt_Pos_x + " Y= " + rbt_Pos_y +  " O= "    + rbt_Pos_o;
    	System.out.println(line);
		Robot.logger.appendLog(line);
    
    	Robot.drivetrain.Field_Position_Startup_X = rbt_Pos_x;
        Robot.drivetrain.Field_Position_Startup_Y = rbt_Pos_y;
        Robot.drivetrain.Field_Position_Startup_O = rbt_Pos_o;
        Robot.drivetrain.resetEncodersAndStats();
        Robot.logger.appendLogPosition("Initializing on Startup Position");
    	// Default is to cross line at least
        //cmdDoNothing("Default"); 

    }
    

    /**
     * This function is called periodically during autonomous
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        if ( logCntr % 10 == 0)             Robot.logger.appendLogPosition("Auto Periodic Position"); // log every 200 ms
        logCntr++;
    }

       
    // ------------------------------------------------------
    //                         Commands
    // ------------------------------------------------------

    public void cmdDoNothing(String choice) {
    	line = "Do Nothing Selected ("  + choice + ") - AutoDoNothingCmdGrp";
    	System.out.println(line);
    	Robot.logger.appendLog(line);
    	autoCmd = new AutoDoNothingCmdGrp();
    	autoCmd.start();	
    }


    @Override
    public void teleopInit() {
        // This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to
        // continue until interrupted by another command, remove
        // this line or comment it out.

        if (autoCmd != null) autoCmd.cancel();
    	//Robot.logger.appendLog("******************  TeleopInit started ******************");
    	//line = "  ***** Battery Voltage = " + pdp.getVoltage() + " *****";
    	//Robot.logger.appendLog(line);
    	//System.out.println(line); 
        //Robot.logger.printLog();		// write out logfile from autonomous run
        
        //setMotorValveToDefaultPos();
        String xBoxLRChoice = xBoxLRChooser.getSelected();
        if (xBoxLRChoice.equals("Left"))
            Robot.drivetrain.setXBoxLeftMode();
        else
            Robot.drivetrain.setXBoxRightMode();
    }

    /**
     * This function is called periodically during operator control
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        if ( logCntr % 10 == 0) Robot.logger.appendLogPosition("Teleop Periodic Position"); // log every 200 ms
        logCntr++;
        /*
        //show/log battery voltage every 15 seconds
        if ((sysTimer.get() % 15) <= 1) {
            if (printBatVoltFlag) {
                printBatVoltFlag = false;		// only want to print once every 15 seconds
                line =  "  ***** Battery Input Voltage = " 	+ pdp.getVoltage() ;
                line +=	"  Battery Total Current = " 		+ pdp.getTotalCurrent() + " *****" ;
                Robot.logger.appendLog(line);
                System.out.println(line); 
            }
        } else {
            // one second has expired reset print flag
            printBatVoltFlag = true;
        }
        */
    }
        
    public void userInit() {
    	Robot.drivetrain.resetGyro();
    	Robot.drivetrain.resetEncodersAndStats();
    	Robot.drivetrain.resetPosition(true); 
    	sysTimer.reset();			// System timer for Competition run
    	sysTimer.start();  
    }

    public void setMotorValveToDefaultPos(){
        Robot.intakeSubSys.liftValveSetLowered();           // Intake Roller Retracted
        Robot.manipulatorSubSys.setManipulatorToCargoPos(); // Default to cargo position
        Robot.manipulatorSubSys.setGrabberClosed();         // Default to closed to hold Hatch
        Robot.intakeSubSys.rollerMotorStop();
        Robot.manipulatorSubSys.cargoMtrStop();
        Robot.intakeSubSys.intakeMtrStop();
    }
}
