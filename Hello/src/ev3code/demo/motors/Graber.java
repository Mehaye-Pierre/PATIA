package ev3code.demo.motors;


import ev3code.demo.utils.R2D2Constants;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;

public class Graber extends TImedMotor{

	private EV3LargeRegulatedMotor graber = null;
	private Port port                     = null;
	private Long startCalibrateOpen       = null;
	private long movementTimeOpen         = 0;
	private long lastAskedRunning         = -1;
	
	private boolean isClose = false;
	private boolean isOpen  = false;
	private boolean isRunin = false;


	public Graber(){
		port   = LocalEV3.get().getPort(R2D2Constants.PINCH);
		graber = new EV3LargeRegulatedMotor(port);
	}

	/**
	 * Lance la calibration
	 * @param open vrai pour l'ouverture
	 */
	public void startCalibrate(boolean open){
		graber.setSpeed(R2D2Constants.GRAB_CALIBRATE_SPEED);
		if(open){
			graber.forward();
			startCalibrateOpen = System.currentTimeMillis();
		}else{
			graber.backward();
		}
	}

	/**
	 * Arrête la calibration
	 * @param open
	 */
	public void stopCalibrate(boolean open){
		stopMoving();
		Long stoping = System.currentTimeMillis();
		if(open){
			movementTimeOpen = stoping - startCalibrateOpen;
			isOpen  = true;
			isClose = false;
		}else{
			isOpen  = false;
			isClose = true;
		}
	}

	/**
	 * ferme la pince en fonction de la calibration
	 * Le mouvement sera lancé uniquement si aucun mouvement n'est en cours
	 */
	public void close(){
		if(!isRunin){
			graber.setSpeed(R2D2Constants.GRAB_RUNNING_SPEED);
			graber.backward();
			isRunin = true;
			lastAskedRunning = System.currentTimeMillis();
		}
	}
	
	/**
	 * ferme la pince en fonction de la calibration
	 * Le mouvement sera lancé uniquement si aucun mouvement n'est en cours
	 */
	public void open(){
		if(!isRunin){
			graber.setSpeed(R2D2Constants.GRAB_RUNNING_SPEED);
			graber.forward();
			isRunin = true;
			lastAskedRunning = System.currentTimeMillis();
		}
	}

	@Override
	public void stopMoving() {
		lastAskedRunning = -1;
		graber.stop();
		isRunin = false;
		if(isClose){
			isOpen  = true;
			isClose = false;
		}else{
			isOpen  = false;
			isClose = true;
		}
	}

	@Override
	public boolean isStall() {
		return graber.isStalled();
	}

	@Override
	public void runFor(int millis, boolean forward) {
		if(forward){
			open();
		}else{
			close();
		}
	}

	@Override
	public boolean isTimeRunElapsed() {
		if(lastAskedRunning != -1){
			long wait = 0;
			wait = movementTimeOpen / (R2D2Constants.GRAB_RUNNING_SPEED / R2D2Constants.GRAB_CALIBRATE_SPEED);

			long attendeeDate = lastAskedRunning + wait;
			return System.currentTimeMillis() > attendeeDate;
		}
		return false;
	}

	/**
	 * 
	 * @return vrai si la pince est fermée
	 */
	public boolean isClose() {
		return isClose;
	}

	/**
	 * 
	 * @return vrai si la pince est ouverte
	 */
	public boolean isOpen() {
		return isOpen;
	}
	
	/**
	 * 
	 * @return vrai si la pince est en train de bouger
	 */
	public boolean isRunning(){
		return isRunin;
	}

	@Override
	public void run(boolean forward) {
		if(forward){
			open();
		}else{
			close();
		}
	}

	public long getOpenTime() {
		return movementTimeOpen;
	}
	
	public void setOpenTime(long mvt){
		this.movementTimeOpen = mvt;
	}
}
