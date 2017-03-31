package ev3code.demo.oldmain;

import ev3code.demo.controller.Controler;

import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		Controler controler = new Controler();
		try{
			controler.start();
		}catch(Throwable e){
			e.printStackTrace();
			Delay.msDelay(10000);
		}
		System.exit(0);
	}

}
