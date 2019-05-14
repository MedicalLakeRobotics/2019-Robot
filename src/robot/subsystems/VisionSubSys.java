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
import robot.utils.Rmath;

//import robot.commands.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.Robot;
import robot.utils.MyUdpClient;
import robot.utils.Position;
import robot.utils.Map;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class VisionSubSys extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS
    
    public static final String SERVER = "10.45.13.16";
    public static final int PORT = 5858;
    MyUdpClient client;
    Map map;

    public static int COG_X, COG_Y;
    private static int updateCtr = 0;
    private static double logCtr = 0;

    private static enum HUD_VISION_UPDATE_FLAG {VALID_TGT, NO_MSG, NO_VALID_TGT};
    private static HUD_VISION_UPDATE_FLAG hud_Vision_Update_Flag = HUD_VISION_UPDATE_FLAG.NO_MSG;

    private static String camera;
    private static int valid_Vision_Tgt;    // 0 = no target, 1=valid target 
    private static int ctrX, topY;
    private static double tgtHdg;
    private static double dist_Gap, dist_Top_Rocket_Cargo, dist_Top_Hatch;
    
    private static int leftMostX, rightMostX;
    private static double tgtSkew;

    private static enum HUD_POS_UPDATE_FLAG {POST,NOPOST};
    private static HUD_POS_UPDATE_FLAG hud_Pos_Update_Flag = HUD_POS_UPDATE_FLAG.NOPOST;

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public VisionSubSys() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

        client = new MyUdpClient(SERVER, PORT) ;
        map = new Map();
    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
    } 

    @Override
    public void periodic() {
        //if ( hud_Pos_Update_Flag == HUD_POS_UPDATE_FLAG.POST){
        //        send_PosData_To_HUD();      // Send position data to HUD
        //    }
        check_For_Vision_Data();

        if (valid_Vision_Tgt == 0)
            // no valid visual target in site
            logCtr = 0;
        else {
            if ( logCtr % 10 == 0){
                // we have a valid vision tartget in site
                //logVision_Data();      // Update dsiplay every 200 ms
                logCtr++;
            }
        }

        //if (hud_Vision_Update_Flag == HUD_VISION_UPDATE_FLAG.VALID_TGT) send_VisionData_To_HUD(); 
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS

    public void send_PosData_To_HUD(){
        String msg;

        Position currPos = new Position();
        currPos.x = Robot.drivetrain.getPositionX();
        currPos.y = Robot.drivetrain.getPositionY();
        //currPos.orientCW =  Robot.drivetrain.getTrackerYaw();
        currPos.orientCW =  Robot.drivetrain.getGyroYaw();
        msg = String.format("Position, %g, %g, %g" , (currPos.x/12.0) , (currPos.y/12.0) , currPos.orientCW);
        client.sendMessage(msg);

        // Do we want to send possible target information ???? 
        //Position tgtPos = new Position(map.getTargetPosBasedOnRobotPosition(currPos) );
        //msg = String.format("Target, %s, %g, %g, %g" , tgtPos.name, (tgtPos.x/12.0) , (tgtPos.y/12.0) ,tgtPos.orientCW);
        //client.sendMessage(msg);

        //client.close();
        updateSmartDashboard(msg);
    }

    public void send_VisionData_To_HUD(){
        // Get current robot position data from drivetrain
        double dist;
        Position currPos = new Position();
        currPos.x = Robot.drivetrain.getPositionX();
        currPos.y = Robot.drivetrain.getPositionY();
        currPos.orientCW = Robot.drivetrain.getGyroYaw();

        // ------------- Lookup Possible field based target ----------------------
        // Determine which target were looking at ie) Rocket cargo or Hatch
        // This is to select which distance values to use 
        Position tgtPos = Map.getTargetPosBasedOnRobotPosition(currPos);
        
        if (tgtPos.name.equals("Rocket Left Center") ||
            tgtPos.name.equals("Rocket Right Center")) {
                dist = dist_Top_Rocket_Cargo;
        } else {
                dist = dist_Top_Hatch;
        }

        // Calculate vision position X & Y coords from Vision HDG & Distance data
        // Send to HUD

        Position visPos = calcVisionPosFromVisionHdgDist( currPos, tgtHdg, dist );
        String msg = String.format("Vision, %g,%g" , visPos.x/12.0 , visPos.y/12.0);
        client.sendMessage(msg);

        // Send target location data to HUD
        msg = String.format("Target, %s,%g,%g,%g" , tgtPos.name, tgtPos.x/12.0 , tgtPos.y/12.0, tgtPos.orientCW);
        client.sendMessage(msg);

        // this currently is using Vision Hdg & Distance and current gyro Yaw
        Position newCurrPos = calcCurrPosFromVisionHdgDist( currPos, visPos, tgtHdg, dist);
        msg = String.format("Position, %g,%g,%g" , newCurrPos.x/12.0 , newCurrPos.y/12.0, newCurrPos.orientCW);
        client.sendMessage(msg);
        Robot.drivetrain.setFieldPosition( newCurrPos );        // Reset drivetrain current field position based on vision
        //client.close();

        updateSmartDashboard(msg);
        SmartDashboard.putNumber("Vision_CTR_X", ctrX);
        SmartDashboard.putNumber("Vision_HDG", tgtHdg);

        SmartDashboard.putNumber("Vision_TOP_Y", topY);
        SmartDashboard.putNumber("Vision_Distance_Rocket_Cargo", dist_Top_Rocket_Cargo);
        SmartDashboard.putNumber("Vision_Distance_Hatch", dist_Top_Hatch);

    }

    public void logVision_Data() {
        String msg;
        //System.out.println("Vision Log ");
        // #1. Log raw data from vision

        try {
        msg = String.format("Vision Data 1 (Raw Data),ctrX=,%d,topyY=,%d,tgtHdg=,%g,dist_Top_Rocket_Cargo,%g,dist_Top_Hatch,%g",
                    ctrX, topY, tgtHdg, dist_Top_Rocket_Cargo, dist_Top_Hatch);
        Robot.logger.appendLog(msg);
        } catch(Exception e) {
            Robot.logger.appendLog("Exception while logging vision UDP data");
        }
        // #2. log robot position data
        Robot.logger.appendLogPosition("Vision Data 2 (Robot Position)");

        // #3. log calculated calcVisionPosFromVisionHdgDist
        Position currPos = new Position();
        currPos.x = Robot.drivetrain.getPositionX();
        currPos.y = Robot.drivetrain.getPositionY();
        currPos.orientCW = Robot.drivetrain.getGyroYaw();
        double tgtDist = dist_Top_Hatch;
        Position vis1Pos =  calcVisionPosFromVisionHdgDist( currPos, tgtHdg, tgtDist );
        msg = String.format("Vision Data 3 (Calc Tgt Pos),vis1Pos.x=,%g,visPos.y=,%g,visPos.O,%g",
                    vis1Pos.x,vis1Pos.y,vis1Pos.orientCW);
        Robot.logger.appendLog(msg);

        // #4. log calculated calcCurrPosFromVisionHdgDist
        tgtDist = dist_Top_Hatch;
        Position vis2Pos =  calcCurrPosFromVisionHdgDist(currPos,  vis1Pos, tgtHdg, tgtDist);
        msg = String.format("Vision Data 4 (Calc Robit Pos),vis2Pos.x=,%g,vis2Pos.y=,%g,vis2Pos.O,%g",
                    vis2Pos.x,vis2Pos.y,vis2Pos.orientCW);
        Robot.logger.appendLog(msg);

        // Position currPos, Position visPos, double tgtHdg, double tgtDist
    }


    public void check_For_Vision_Data(){
        String msg = Robot.uDPServerSubSys.getLastMessage(true);
        valid_Vision_Tgt = 0;               // default to 0 no valid vision tgt seen!
        hud_Vision_Update_Flag = HUD_VISION_UPDATE_FLAG.NO_VALID_TGT;       // init in case badd message

        if (msg.equals("")) {
            return;
        }

        if (( msg.equals("No Msg")) ||
            ( msg.equals("(Not Running)"))) {
                return;
        }

        String[] fields = msg.split(" ");
        if (fields.length < 3)  {
            // No vision message received to send to HUD so get out
            return;
        }

        if (fields[0].equals("back")) {
                // No valid vision target in view
                hud_Vision_Update_Flag = HUD_VISION_UPDATE_FLAG.NO_VALID_TGT;
                return;
        }

        if (fields[0].equals("front") && fields[1].equals("0")) {
            // No valid vision target in view
            hud_Vision_Update_Flag = HUD_VISION_UPDATE_FLAG.NO_VALID_TGT;
            return;
        }

        if (fields.length < 8){
            // we dont have thr right number of fields comming in
            hud_Vision_Update_Flag = HUD_VISION_UPDATE_FLAG.NO_VALID_TGT;
            return;
        }

        if (!fields[1].equals("1")){
            return;
        }

        valid_Vision_Tgt = 1;        
        // we must have a valid target

        //Robot.logger.appendLog("Vision Data 0 (Raw UDP Packet)" + msg);        // log raw received data packet
        camera = fields[0];

        ctrX = Integer.parseInt(fields[2]);
        topY = Integer.parseInt(fields[3]);
        tgtHdg = Double.parseDouble(fields[4]);

        dist_Gap  = Double.parseDouble(fields[5]);
        dist_Top_Rocket_Cargo  = Double.parseDouble(fields[6]);
        dist_Top_Hatch = Double.parseDouble(fields[7]);

        update_SmartDashboard();

        // 1 = camera front rear,  2 = success flag 0 or 1,     3 = ctrx int,          4 = topy int
        // 5 = targtetHdg double,  6 = target dist gap double,  7 = target dist vert top rocket cargo
        // 8 = target dist hatch,  9 = leftmost x top x,       10 = rightmosty top x,  11 = target Skew
        
    }

    public void set_HUD_Pos_Update_On(){
        hud_Pos_Update_Flag = HUD_POS_UPDATE_FLAG.POST;
    }


    public void set_HUD_Pos_Update_Off(){
        hud_Pos_Update_Flag = HUD_POS_UPDATE_FLAG.NOPOST;
    }

    public double getDistFt(){
        return 6.2; // this is for a place holder till code is ready
    }

    public double getBearingDegCW(){
        return 22.2; // this is for a place holder till code is ready
    }

    private void updateSmartDashboard(String msg){
        SmartDashboard.putString("UDP Sent to Server", SERVER);
        SmartDashboard.putNumber("UDP Sent to Port", (double)PORT);
        SmartDashboard.putString("UDP Sent Msg", msg);
    }

    private Position calcVisionPosFromVisionHdgDist(Position currPos, double tgtHdg, double tgtDist){
        // calculate new position data fom vision data
        Position newPos = new Position();
        double angle = 90-(currPos.orientCW + tgtHdg);
        double newX = currPos.x + (Math.cos(angle) * tgtDist);
        double newY =  currPos.y + (Math.sin(angle) * tgtDist);
        newPos.x = newX;
        newPos.y = newY;
        newPos.orientCW = 0;   // no idea
        newPos.name = "";
        return newPos;
    }

    private Position calcCurrPosFromVisionHdgDist(Position currPos, Position visPos, double tgtHdg, double tgtDist){
        // calculate new position data fom vision data
        Position newPos = new Position();
        double angle = 90-(currPos.orientCW + tgtHdg);
        double newX = visPos.x - (Math.cos(Math.toRadians(angle)) * tgtDist);
        double newY =  visPos.y - (Math.sin(Math.toRadians(angle)) * tgtDist);
        newPos.x = newX;
        newPos.y = newY;
        newPos.orientCW = currPos.orientCW; 
        newPos.name = "";
        return newPos;
    }

      // ------------------------------------------------------------------------
    // ------------------------ Smart Dashboard Methods -----------------------
    void update_SmartDashboard() {
        SmartDashboard.putNumber("Vision-CtrX", ctrX);
        SmartDashboard.putNumber("Vision-TopY", topY);
        SmartDashboard.putNumber("Vision-Bearing", Rmath.mRound(tgtHdg, 2));
        SmartDashboard.putNumber("Vision-Hatch Dist", Rmath.mRound(dist_Top_Hatch, 2));
        SmartDashboard.putNumber("Vision-Cargo Dist", Rmath.mRound(dist_Top_Rocket_Cargo, 2));
    }
}
