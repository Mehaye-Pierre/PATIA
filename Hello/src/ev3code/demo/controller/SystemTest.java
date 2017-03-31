package ev3code.demo.controller;

import lejos.hardware.Button;
import lejos.robotics.Color;

public class SystemTest {

	public static void colorTest(Controler c) {
		c.color.lightOn();
		boolean run = true;
		String couleur = "";
		while(run){
			if(couleur.equals("")){
				c.screen.drawText("Test du calibrage", 
								"Appuyez sur le bouton central "+"pour tester une couleur");
			}else{
				c.screen.drawText("Test de la calibration", 
								"Couleur trouvée : "+couleur,
								"Appuyez sur le bouton central ","pour tester une couleur");
			}
			if(c.input.waitOkEscape(Button.ID_ENTER)){
				switch(c.color.getCurrentColor()){

				case Color.GREEN:
					couleur = "GREEN";
					break;

				case Color.BLUE:
					couleur = "BLUE";
					break;

				case Color.RED:
					couleur = "RED";
					break;

				case Color.BLACK:
					couleur = "BLACK";
					break;

				case Color.WHITE:
					couleur = "WHITE";
					break;
				
				default:
					couleur = "inconnue";
				}
			}else{
				run = false;
			}
		}
	}

	public static void grabberTest(Controler c) {

		for(int i=0; i<2; i++){
			c.screen.drawText("TEST", 
					"Presser le capteur de pression",
					"Avec un palet",
					"pour continuer");
			if(!c.pression.activePressWait())
				return;
			c.graber.close();
			while(c.graber.isRunning()){
				c.graber.checkState();
				//Sécurité d'échappement
				if(Button.ESCAPE.isDown())
					return;
			}
			c.graber.open();
			while(c.graber.isRunning()){
				c.graber.checkState();
				//Sécurité d'échappement
				if(Button.ESCAPE.isDown())
					return;
			}

		}
	}

	public static void sensorTest(Controler c) {
		c.screen.drawText("TEST", 
				"Presser le capteur de pression");
		c.screen.clearDraw();
		boolean lastState = false;
		while(true){
			boolean state = c.pression.isPressed();
			if(state != lastState){
				System.out.println("Pression "+state);
				System.out.println("Pression "+ c.pression.raw()[0]);
				lastState = state;
			}
			if(c.input.enterPressed())
				break;
		}
		c.screen.clearPrintln();
	}

	public static void motorTest(Controler c) {
		c.screen.drawText("Test", "test du moteur", "appuyez sur entrée");
		c.input.waitAny();
		c.propulsion.runFor(1000, true);
		while(c.propulsion.isRunning()){
			c.propulsion.checkState();
			//Sécurité d'échappement
			if(Button.ESCAPE.isDown())
				return;
		}

		c.propulsion.volteFace(true);
		while(c.propulsion.isRunning()){
			//Sécurité d'échappement
			if(Button.ESCAPE.isDown())
				return;
		}

		c.propulsion.runFor(1000, true);
		while(c.propulsion.isRunning()){
			c.propulsion.checkState();
			//Sécurité d'échappement
			if(Button.ESCAPE.isDown())
				return;
		}

		c.propulsion.volteFace(false);
		while(c.propulsion.isRunning()){
			c.propulsion.checkState();
			//Sécurité d'échappement
			if(Button.ESCAPE.isDown())
				return;
		}

	}

}
