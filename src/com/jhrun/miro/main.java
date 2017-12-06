package com.jhrun.miro;


import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;



public class main {

	public static void main(String[] args) {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
	
		
		//foward();
		
		EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
		SensorMode ambient = colorSensor.getAmbientMode();
		float[] sample = new float[ambient.sampleSize()];
		ambient.fetchSample(sample, 0);
		
		
	setDisplay(ev3, String.valueOf(sample[0]), sample[0] < 0.02? "black" : "white");
		//
		foward();
		//colorSensor.close();
	}
	
	public static void init() {
		
	}
	
	public static void setDisplay(EV3 ev3, String ambient, String color) {
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		lcd.drawString("Miro Ver 0.1.1207", 0, 0);
		lcd.drawString("Ambient : " + ambient, 0, 2);
		lcd.drawString("Color : " + color, 0, 3);
		lcd.drawString("LeftSpeed : " + Motor.B.getSpeed(), 0, 4);
		lcd.drawString("RightSpeed : " + Motor.C.getSpeed(), 0, 5);

		keys.waitForAnyPress();
	}
	
	
	public static void foward() {
		 Motor.B.setSpeed(720);// 2 RPM
		   Motor.C.setSpeed(720);
		  // Motor.B.rotate(360);
		 //  Motor.C.rotate(360);
		   Motor.B.forward();
		   Motor.C.backward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
}
