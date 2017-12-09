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
	
	static String Appname = "Miro";
	static String version = "0.8.1209";
	
	private static EV3GyroSensor gyro = null;
	   private static SampleProvider gyroSamples = null;
	   private UnregulatedMotor leftMotor = null;
	   private UnregulatedMotor rightMotor = null;
	   private static Audio audio = null;
	   static float[] angle = { 0.0f };
	   static float gyroTacho = 0;
	   static int mode = 9; // 0 :  직진
	   static boolean isBlack = false;
	   static boolean[]rightangles;

	   static int haveToRotateAngle = 0; //사용안함
	   static int fowardcount = 0;
	   static int backwardcount = 0;
	   static boolean turned = false;
	   static boolean tooShortFoward = false;
	   static int rotatenum = 0;
	   static float wboundary = 0;
	   static boolean rotatelock = false;
	  static float whiteam = 0;//하얀색
	  static float lowest_color = 10;//낮은검은색
	  static boolean checkedThisisJustLine = false;
	  static int INIT_MODE = 9;
	  static int NORMAL_MODE = 0;
	  static int WHEN_BLACK_MODE = 1;
	  static int JUST_LINE_MODE = 2;
	  static int SIN_MODE = 3;
	  static int NORMAL_ROTATE_MODE = 4;
	  static int LEFT_ROTATE_MODE = 5;
	  static boolean allowWriteLineCheckArray= false;
	  static int trueCount = 0;



	public static void main(String[] args) {
		EV3 ev3 = (EV3) BrickFinder.getLocal();
	
		EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S4);
		
		SensorMode ambient = colorSensor.getAmbientMode();
		float[] sample = new float[ambient.sampleSize()];
		audio = ev3.getAudio();
	      wboundary = setBoundary(ev3, ambient); //경계값 설정
	      waitForStart(ev3);

	      gyro = new EV3GyroSensor(SensorPort.S3);
	     
	   
	      gyroSamples = gyro.getAngleMode();
	      audio.systemSound(Audio.DOUBLE_BEEP);
	   
	      ambient.fetchSample(sample, 0);
	 
		//foward();
	
	      
	      //	컬러값 가져오기
	     
	      
	      
	
			Keys keys = ev3.getKeys();
		
	while(true) {
			
			ambient.fetchSample(sample, 0);
			//ISBLACK?
			isBlack =  sample[0] <= wboundary;
			
		setDisplay(ev3, String.valueOf(sample[0]), isBlack ? "black" : "white");
			//
		//	foward();
		//블랙일때
		if((int)(getGyroAngle() * 100) % 90 == 0) {
		if(fowardcount < 200) {
			continue;
		}
		fowardcount = 0;
		}
		rotateToAngle(90);

		
		
		}
		
		
		//colorSensor.close();
	}
	

	
	public static void init() {
		
		
	
		
	}
	
	public static void waitForStart(EV3 ev3) {
		 setDisplayMessage(ev3 ,"Wait For Start");
	}
	
	public static void doWhenInit() {
		//init
				if(mode == INIT_MODE) {
					//gyro.reset();
					fowardcount =0;
					mode =NORMAL_MODE;
				}
			}
	
	public static float setBoundary(EV3 ev3, SensorMode ambient ) {
		float[] sample = new float[ambient.sampleSize()];
		float white = 0;
		float black = 0;
		
		
	      ambient.fetchSample(sample, 0);
	      white = sample[0];
	      
		 setDisplayMessage(ev3 ,"Set Black Color");
	      ambient.fetchSample(sample, 0);
		 black = sample[0];
		 
		return (white + black)/2;
	}
	
	public static void doWhenLeftRotateMode() {
	if(mode == LEFT_ROTATE_MODE) {
			
			
			if(tooShortFoward) {
				//너무 짧은 시간내에 다시 부딪힌 경우
			fowardcount = 0; //초기
			tooShortFoward = false;
				
			
			if(turned) {
				//왼쪽으로 90도
				rotateToAngle(-90);
			
			}
			
				
		
				
			}else {
				turned =false;
				//오른쪽을 90
				rotateToAngle(90);
				
				
			}
				
			
			
		}
			
	}
	
	public static void doWhenRotateMode() {

		if(mode == NORMAL_ROTATE_MODE) {
			
			
		if(tooShortFoward) {
			//너무 짧은 시간내에 다시 부딪힌 경우
			
	//이미 180도 백스탭 밟았는지 확
		if(turned && (getGyroAngle() >= haveToRotateAngle -5 && getGyroAngle() <= haveToRotateAngle +5)) {
			//왼쪽으로 90도
			mode =LEFT_ROTATE_MODE;
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
	}
	public static void doWhenNormalMode() {
		if(mode == NORMAL_MODE) {
			//reset gyro
		fowardcount ++;
			foward();
			tooShortFoward = fowardcount <  35 ? true : false; //너무 짧은지 확
			
		}
		
	}
	
	public static void doWhenJustLineMode(){

		if(mode == JUST_LINE_MODE) {
			
         rotatea(-70);
         backwardcount++;
			
			if(!isBlack && backwardcount > 20) {
				//mode = NORMAL_ROTATE_MODE;
				backwardcount=0;
				
			}
			
		}
	}
	
	public static void doWhenBlack() {
		
		if(mode == WHEN_BLACK_MODE) {
			//WOW Its black! 검은 선을 만났을때 해야하는 
		
			//이 부분이 대각선인지 아니면 그냥 부딪친건지 확인한다. 오른쪽 바퀴를 굴려서 확인 약 45도
			if( fowardcount == 0)initAngle();
			if(fowardcount < 20) {
				moveRightWheel(45);
				rightangles[fowardcount] = isBlack;
				fowardcount++;
				
			}
		
			//되돌아가기
			if(backwardcount < 20 && fowardcount >= 20) {
				
				moveRightWheel(-45);
				backwardcount++;
			}

			//되돌아가고 모드 설정
			if(backwardcount >= 20 &&fowardcount >= 20) {
				fowardcount = 0;
				backwardcount = 0;
				if(checkThisisJustLine(rightangles)) {
					mode = JUST_LINE_MODE;
				}else {
					mode = SIN_MODE;
				}
			}
			
				
		//break;
		}
		
	}
	
	public static boolean checkThisisJustLine(boolean[] array) {
		trueCount = 0;
		for (int i = 0; i < array.length; i++) {
		    if (array[i] /* or array[i] */) {
		        trueCount++;
		    }
		    if (trueCount >= 15) {
		        return false;
		    }
		    
		}
		return true;
	}
	
public static void rotateToAngle(int a) {
	rotatenum = a;
a= a- 1;
	if(!rotatelock) {
		haveToRotateAngle +=a;
		rotatelock =true;
	}
	
	//이건 오른쪽으로 90도 돌때 해야하는 일
	if(getGyroAngle()  < haveToRotateAngle -5 || getGyroAngle()  > haveToRotateAngle +5) {
		rotate((haveToRotateAngle - getGyroAngle())*3,(int) (haveToRotateAngle - getGyroAngle()));
	}else {
		
	stopMove();
	rotatelock= false;
	
	mode = 9;
}
	
}
	
	public static void setDisplay(EV3 ev3, String ambient, String color) {
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		
		lcd.drawString(Appname + " Ver " + version, 0, 0);
		lcd.drawString("Rotate : " + haveToRotateAngle, 0, 1);
		lcd.drawString("Ambient : " + ambient, 0, 2);
		lcd.drawString("Color : " + color, 0, 3);
		lcd.drawString("Wb : " + wboundary, 0, 4);
	//	lcd.drawString("Speed : " + Motor.B.getSpeed() + ", " + Motor.C.getSpeed(), 0, 4);
		lcd.drawString("Gyro : " + getGyroAngle(), 0, 5);
		lcd.drawString("Mode : " +  mode, 0, 6);
		lcd.drawString("TC : " +  trueCount, 0, 7);
lcd.refresh();

	//	keys.waitForAnyPress();
	}
	
	public static void setDisplayMessage(EV3 ev3, String message) {
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		
		lcd.clear();
		lcd.drawString("Miro Alert", 0, 0);
		lcd.drawString(message, 0, 1);
		lcd.refresh();
		 audio.systemSound(0);
		keys.waitForAnyPress();
		lcd.clear();
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
		rightangles = new boolean[100];
	}
	
	public static void rotatea(int a) {
		 Motor.B.setSpeed(60);// 2 RPM720
		   Motor.C.setSpeed(60);
		   Motor.B.rotate(a, true);
		   Motor.C.rotate(a, false);
		//   Motor.B.stop
		  // Motor.B.backward();
		   //Motor.C.backward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
	public static void moveRightWheel(int a) {
		   Motor.C.setSpeed(60);
		   Motor.C.rotate(a, true);
		//   Motor.B.stop
		  // Motor.B.backward();
		   //Motor.C.backward();
//		   Motor.B.stop();
//		  // Motor.B.get();
//		   Motor.C.stop();
	}
	
	public static void moveLeftWheel(int a) {
		   Motor.B.setSpeed(60);
		   Motor.B.rotate(a, false);
		//   Motor.B.stop
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
		if(f > 270) f= 270;
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
