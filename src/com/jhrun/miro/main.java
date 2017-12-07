package com.jhrun.miro;


import java.awt.Button;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.motor.UnregulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;



public class main {
	
	private static EV3GyroSensor gyro = null;
	   private static SampleProvider gyroSamples = null;
	   private UnregulatedMotor leftMotor = null;
	   private UnregulatedMotor rightMotor = null;
	   private static Audio audio = null;
	   static float[] angle = { 0.0f };
	   static float gyroTacho = 0;
	   static int mode = 10; // 0 :  직진
	   static boolean isBlack = false;
	   static boolean[] anglesplus;
	   static boolean[] anglesminus;
	   static int haveToRotateAngle = 0; //사용안함
	   static int fowardcount = 0;
	   static boolean turned = false;
	   static boolean tooShortFoward = false;
	   static int rotatenum = 0;
	   static float wboundary = 0;


	public static void main(String[] args) {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
	
		EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
		
		SensorMode ambient = colorSensor.getAmbientMode();
		float[] sample = new float[ambient.sampleSize()];
		audio = ev3.getAudio();
		 audio.systemSound(0);
	      gyro = new EV3GyroSensor(SensorPort.S3);
	     
	   
	      gyroSamples = gyro.getAngleMode();
	      audio.systemSound(Audio.DOUBLE_BEEP);
	   
	      wboundary = (float) ((sample[0] + 0.04) / 2);
		//foward();
		
		while(true) {
	
			ambient.fetchSample(sample, 0);
			//ISBLACK?
			isBlack =  sample[0] < 0.04;
			
		setDisplay(ev3, String.valueOf(sample[0]), isBlack ? "black" : "white");
			//
		//	foward();
		//블랙일때
		if(mode == 0 && isBlack) mode = 1;
		
		
		//init
		if(mode == 10) {
			gyro.reset();
			fowardcount =0;
			mode =0;
		}
		
		if(mode == 0) {
			//reset gyro
		fowardcount ++;
			foward();
			tooShortFoward = fowardcount <  100 ? true : false; //너무 짧은지 확
			
		}
		
	
		
		if(mode == 1) {
			//WOW Its black! 검은 선을 만났을때 해야하는 
			//stopMove();
		
			rotatea(-70);
			
			if(!isBlack) {
				mode = 2;
				
			}
				
		//break;
		}
		
		if(mode == 2) {
			
			
		if(tooShortFoward) {
			//너무 짧은 시간내에 다시 부딪힌 경우
		fowardcount = 0; //초기
			
	//이미 180도 백스탭 밟았는지 확
		if(turned && getGyroAngle() < 183 && getGyroAngle() >177) {
			//왼쪽으로 90도
			rotateToAngle(-90);
		}else {
			//오른쪽을 180도
			rotateToAngle(180);
			turned = true;
		}
		
	
			
		}else {
			turned =false;
			//오른쪽을 90
			rotateToAngle(90);
			
			
		}
			
			
			
			
			
			
			
		}
		
		//Mode3 오른쪽 방향이 정확한지 확인하기
		if(mode == 3) {
			
		}
			
		
		}
		
		
		
		
		//colorSensor.close();
	}
	
	public static void init() {
		
	}
	
public static void rotateToAngle(int a) {
	rotatenum = a;
	//이건 오른쪽으로 90도 돌때 해야하는 일
	if(getGyroAngle() > a+3 || getGyroAngle() < a-3) {
		rotate((a - getGyroAngle())*3,(int) (a - getGyroAngle()));
	}else {
	stopMove();
	mode = 10;
}
	
}
	
	public static void setDisplay(EV3 ev3, String ambient, String color) {
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		lcd.clear();
		lcd.drawString("Miro Ver 0.4.1207", 0, 0);
		lcd.drawString("Rotate : " + rotatenum, 0, 1);
		lcd.drawString("Ambient : " + ambient, 0, 2);
		lcd.drawString("Color : " + color, 0, 3);
		lcd.drawString("Speed : " + Motor.B.getSpeed() + ", " + Motor.C.getSpeed(), 0, 4);
		lcd.drawString("Gyro : " + getGyroAngle(), 0, 5);
		lcd.drawString("Mode : " +  mode, 0, 6);
		lcd.drawString("WBoundary : " +  wboundary, 0, 7);

	//	keys.waitForAnyPress();
	}
	
	

	public static void foward() {
		 Motor.B.setSpeed(400);// 2 RPM720
		   Motor.C.setSpeed(400);
		  // Motor.B.rotate(360);
		 //  Motor.C.rotate(360);
		   Motor.B.forward();
		   Motor.C.forward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	public static void backward() {
		 Motor.B.setSpeed(400);// 2 RPM720
		   Motor.C.setSpeed(400);
		  // Motor.B.rotate(360);
		 //  Motor.C.rotate(360);
		   Motor.B.backward();
		   Motor.C.backward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
	
	public static void setHaveToRotateRight() {
		haveToRotateAngle += 90;
	}
	public static void initAngle() {
		anglesplus = new boolean[360];
		anglesminus = new boolean[360];
	}
	
	public static void rotatea(int a) {
		 Motor.B.setSpeed(200);// 2 RPM720
		   Motor.C.setSpeed(200);
		   Motor.B.rotate(a, true);
		   Motor.C.rotate(a, false);
		  // Motor.B.backward();
		   //Motor.C.backward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
	
	public static void stopMove() {

		   Motor.B.stop();
		  // Motor.B.get();
		   Motor.C.stop();
	}
	
	
	public static void rotate(float f,int a) {
		 Motor.B.setSpeed(f);// 2 RPM
		   Motor.C.setSpeed(f);
		  // Motor.B.rotate(360);
		 //  Motor.C.rotate(360);
		   Motor.B.rotate(a, true);
		   Motor.C.rotate(-a, true);
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
	
	public static float getGyroAngleRaw() {
	      gyroSamples.fetchSample(angle, 0);
	      return angle[0];
	   }

	   public static float getGyroAngle() {
	      float rawAngle = getGyroAngleRaw();
	      return rawAngle - gyroTacho;
	   }

	   
	public void resetGyro() {
	      if (gyro != null) {
	         Delay.msDelay(1000); //wait until the hands are off the robot
	         audio.systemSound(0);
	         
	         gyro.reset();
	         gyroSamples = gyro.getAngleMode();
	         gyroTacho = 0;
	         System.out.println("Gyro is reset");
	      }
	

	}
	
}
