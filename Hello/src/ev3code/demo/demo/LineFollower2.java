package ev3code.demo.demo;
import lejos.hardware.Button;
import java.awt.Point;

import lejos.hardware.port.MotorPort;

public class LineFollower2
{
    public static void main(String[] args)
    {
        CameraTest controller = new CameraTest(MotorPort.B,MotorPort.C);
        NetworkThread net = new NetworkThread();
        Thread lolz = new Thread( net);
        lolz.start();
        net.startPosLeft(false);
        while(Button.RIGHT.isUp()){
        	
        	//controller.makePoint();
        	Point mrRobot = net.getRobot();
        	//Point mrBadGuy = net.getOppononent();
        	Point closestPalet = net.getClosestPalet();
        	float currentAngle = net.getAngleRobot();
        	float angle = net.getTurnAngle();
        	float finalAngle = ((currentAngle-angle)+360)%360;
        	if(closestPalet.getX() > -1){
        		//System.out.println("palet :"+closestPalet.getX()+" "+closestPalet.getY());
            	//System.out.println("robot :"+mrRobot.getX()+" "+mrRobot.getY());
            	System.out.println("angle : "+ finalAngle);
        	}
        		
        	//System.out.println("robot angle :"+net.getAngleRobot());
        	//System.out.println("bad guy :"+mrBadGuy.getX()+" "+mrBadGuy.getY());
        	//System.out.println("bad guy angle :"+net.getAngleOppononent());
        	
        	
        }
        
    }
}