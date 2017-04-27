package ev3code.demo.demo;

import java.awt.Point;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Button;

public class NetworkThread implements Runnable{
	
    private String[] oldPalets;
    private boolean[] check;
    private boolean first;
    private DatagramSocket dsocket;
    private Point robot;
    private Point badGuy;
    private float angleRobot = 0;
    private float angleBadGuy = 0;
    private final static int PRECISION = 2;
    private boolean startleft;

	@Override
	public void run() {
		first = true;
		oldPalets = new String[0];
    	bindNet();
        this.robot = new Point();
        this.badGuy = new Point();
    	while(Button.RIGHT.isUp()){
    		network();
    		
    	}
	}
	

    /**
     * Use to start to listen to the server
     */
    public synchronized void bindNet(){
    	int port = 8888;
        // Create a socket to listen on the port.
        try {
			dsocket = new DatagramSocket(port);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //MUST bindNet first
    /**
     * Fetch a new packet, and updates the map accordingly
     * DO remember to use bindNet before calling it
     */
    public synchronized void network(){
    	try 
        {
          int l = 0;
          Point r = new Point();

          // Create a buffer to read datagrams into. If a
          // packet is larger than this buffer, the
          // excess will simply be discarded!
          byte[] buffer = new byte[2048];

          // Create a packet to receive data into the buffer
          DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Wait to receive a datagram
            dsocket.receive(packet);
            
            // Convert the contents to a string, and display them
            String msg = new String(buffer, 0, packet.getLength());
            //System.out.println(packet.getAddress().getHostName() + ": " + msg);
            
            String[] palets = msg.split("\n");
            
            if(!first){
            	check = new boolean[palets.length];
            	for (int i = 0; i < palets.length; i++) 
                {
                	String[] coord = palets[i].split(";");
                	int x = Integer.parseInt(coord[1]);
                	int y = Integer.parseInt(coord[2]);
                	for (int j = 0; j <oldPalets.length; j++) 
                    {
                		String[] oldCoord = oldPalets[j].split(";");
                    	int oldx = Integer.parseInt(oldCoord[1]);
                    	int oldy = Integer.parseInt(oldCoord[2]);
                    	if((x-oldx<PRECISION && x-oldx>-PRECISION && y-oldy<PRECISION && y-oldy>-PRECISION)){
                    		check[i] = true;
                    	}
                    }
                
                	//System.out.println(Integer.toString(x) + " / " + Integer.toString(x) );
                }
            	for (int i = 0; i < palets.length; i++) 
                {
            		
            		if(!check[i]){
            			
            			
            			//System.out.println("MOVED : OBJECT" + i);
            			
            			String[] coord = palets[i].split(";");
                    	int x = Integer.parseInt(coord[1]);
                    	int y = Integer.parseInt(coord[2]);
            			r.setLocation(x,y);
            			setOneRobot(r);
            		}
                }
            }
            else{
            	first= false;
            	int lefty = 99999;
            	int leftx = 42;
            	int righty = -99999;
            	int rightx = 42;
            	for (int i = 0; i < palets.length; i++) {
                    	String[] coord = palets[i].split(";");
                    	int x = Integer.parseInt(coord[1]);
                    	int y = Integer.parseInt(coord[2]);
                    	if(lefty > y){
                    		leftx = x;
                    		lefty = y;
                    	}
                    	if(righty < y){
                    		rightx =x;
                    		righty =y;
                    	}
                }
            	 if(startleft){
                 	robot.x = leftx;
                 	robot.y = lefty;
                 	badGuy.x = rightx;
                 	badGuy.y = righty;
                 }
                 else{
                	badGuy.x = leftx;
                	badGuy.y = lefty;
                  	robot.x = rightx;
                  	robot.y = righty;
                 }
            }
           
            setAllPos(msg);
            

            // Reset the length of the packet before reusing it.
            packet.setLength(buffer.length);
         
        } 
        catch (Exception e) 
        {
          System.err.println(e);
        }
    }
    
    
    /**
     * Set the position of our robot
     * @param r Point to set
     */
    private synchronized void setRobot(Point r){
    	this.robot.x = r.x;
    	this.robot.y = r.y;
    }
    
    /**
     * Set the position of the robot of the opponents
     * @param r Point to set
     */
    private synchronized void setBadGuy(Point r){
    	this.badGuy.x = r.x;
    	this.badGuy.y = r.y;
    }
    
    /**
     * Calculate which robot is the closest to the point and updates the position of the robot
     * @param r The point
     */
    private synchronized void setOneRobot(Point r){
    	int distanceR = Math.abs(robot.x-r.x) + Math.abs(robot.y-r.y);
    	int distanceB = Math.abs(badGuy.x-r.x) + Math.abs(badGuy.y-r.y);
    	if (distanceR < distanceB){
    		angleRobot = calculateAngle(getRobot(),r);
    		setRobot(r);
    	}
    	else{
    		angleBadGuy = calculateAngle(badGuy,r);
    		setBadGuy(r);
    	}
    }
    
    /**
     * @return the position of our robot
     */
    public synchronized Point getRobot(){
    	return robot;
    }
    
    /**
     * @return the position of the opponent
     */
    public synchronized Point getOppononent(){
    	return badGuy;
    }
    
    /**
     * @return the angle of our robot
     */
    public synchronized float getAngleRobot(){
    	return angleRobot;
    }
    
    /**
     * @return the angle of the opponent
     */
    public synchronized float getAngleOppononent(){
    	return angleBadGuy;
    }
    
    
    /**
     * Calculates the position of the palet in play the closest to our robot
     * @return the position of the closest palet
     */
    public synchronized Point getClosestPalet(){
    	
    	double maxdist = 9999999;
    	double dist;
    	Point res = new Point(-42,-42);
    	for (int j = 0; j <oldPalets.length; j++) 
        {
    		String[] oldCoord = oldPalets[j].split(";");
        	int x = Integer.parseInt(oldCoord[1]);
        	int y = Integer.parseInt(oldCoord[2]);
        	dist = Math.sqrt(Math.pow(x-robot.x,2)+Math.pow(y-robot.y,2));
        	//si le palet est trop proche, on risque de de ne pas pouvoir l'attraper et juste le pousser en tournant
        	if (dist < maxdist && (Math.abs(x-robot.x) > 4 || Math.abs(y-robot.y) > 4)){
        		maxdist = dist;
        		res.setLocation(x, y);
        	}
        }
    	System.out.println(""+res.x+" "+res.y);
    	return res;
    	
    	
    }
    
    /**
     * @return angle to the closest palet
     */
    public synchronized float getTurnAngle(){
    	return calculateAngle(robot, getClosestPalet());
    }
    
    
    /**
     * @param startPos true if we start on the left side, else false
     */
    public void startPosLeft(boolean startPos){
    	this.startleft = startPos;
    }
    
    /**
     * Updates the list of positions of all valid palets in play
     * @param msg the latest message from the server
     */
    private synchronized void setAllPos(String msg){
    	List<String> tempList = new ArrayList<String>();
    	oldPalets = new String[msg.split("\n").length];
    	oldPalets= msg.split("\n");
        for (int j = 0; j <oldPalets.length; j++) 
        {
        	
    		String[] oldCoord = oldPalets[j].split(";");
    		int oldx = Integer.parseInt(oldCoord[1]);
        	int oldy = Integer.parseInt(oldCoord[2]);
        	if((oldy > 20 && oldy < 280) && (Math.abs(oldx-robot.x) > 2 || Math.abs(oldy-robot.y) > 2) && (Math.abs(oldx-badGuy.x) > 2 || Math.abs(oldy-badGuy.y) > 2)){
        		tempList.add(oldPalets[j]);
        	}
        	
        }
    	oldPalets = new String[tempList.size()];
    	oldPalets = tempList.toArray(oldPalets);
    }
    
    /**
     * @return the list of the valid palets in play
     */
    public synchronized String[] getAllPos(){
    	return oldPalets;
    }

    
    
    /**
     * Calculates an angle from the position of 2 points
     * @param robotPosition origin
     * @param target target
     * @return the angle
     */
    public static float calculateAngle(Point robotPosition, Point target){
    	  return (float) Math.toDegrees((float)Math.atan2(target.getY()-robotPosition.getY(), target.getX()-robotPosition.getX()));
    }
}
