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
        	
        	controller.makePoint();
        	Point mrRobot = net.getRobot();
        	Point mrBadGuy = net.getOppononent();
        	System.out.println("robot :"+mrRobot.getX()+" "+mrRobot.getY());
        	System.out.println("robot angle :"+net.getAngleRobot());
        	//System.out.println("bad guy :"+mrBadGuy.getX()+" "+mrBadGuy.getY());
        	//System.out.println("bad guy angle :"+net.getAngleOppononent());
        	
        	
        }
        
    }
}