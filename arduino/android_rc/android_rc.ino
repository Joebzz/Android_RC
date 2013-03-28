/*
  Recieves The eventt sent from android app
  MAX pwm speed 255 min 50
  
  Analog input range A0 - A1 from 4.60V nothing - .187V blocked
  Sensor Reading: IR LED's Sumovore pin 10(right) & 9(left)  connected to Arduino A0 & A1 respectivley
  value 44 object in front
  value 1023 no object
*/
#include <MeetAndroid.h>
#include <String>

#define MAX_SPEED    255
#define STEER_SPEED  90
#define SENSOR_MAX   1023
#define SENSOR_MIN   44

// create the meetAndsoid for recieving data from the android app
MeetAndroid meetAndroid;

float motor_speed_left = 0;
float motor_speed_right = 0;

int steerLeft;
int steerRight;

int rightIRsensorPin = 0; //analog pin 0
int leftIRsensorPin = 1; //analog pin 0

int rightMotor1 = 3;     //connects to 5 on sumophor
int rightMotor2 = 9;     //connects to 6 on sumophor
int leftMotor1 = 5;     //connects to 4 on sumophor
int leftMotor2 = 6;     //connects to 3 on sumophor

boolean forward = false;
boolean backward = false;
boolean leftRotate = false;
boolean rightRotate = false;

void setup() {  
  Serial.begin(9600); 
  stopAll();
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function
  // - the second parameter ('l', 'r', 'f', etc...) is the flag sent from my Android application
  meetAndroid.registerFunction(leftEvent, 'l');  
  meetAndroid.registerFunction(rightEvent, 'r');
  meetAndroid.registerFunction(forwardEvent, 'f');
  meetAndroid.registerFunction(reverseEvent, 'b');
  meetAndroid.registerFunction(stopEvent, 's');
  meetAndroid.registerFunction(resetSteeringEvent, 'v');
  meetAndroid.registerFunction(checkSensorValues, 'c');
  
  // initialize the digital pin as an output.
  pinMode(leftMotor1, OUTPUT);  
  pinMode(leftMotor2, OUTPUT); 
  pinMode(rightMotor1, OUTPUT); 
  pinMode(rightMotor2, OUTPUT); 
  pinMode(rightIRsensorPin, INPUT);
  pinMode(leftIRsensorPin, INPUT);
}

void loop() {
  meetAndroid.receive();
  
  //checks for formward or backwards and turns the motors respectivly
  if(forward) {
    analogWrite(leftMotor1,motor_speed_left + steerRight);
    analogWrite(rightMotor2,motor_speed_right + steerLeft);
  
    digitalWrite(leftMotor2, LOW);
    digitalWrite(rightMotor1, LOW);
  }
  else if(backward) {
    analogWrite(leftMotor2,motor_speed_left + steerRight);
    analogWrite(rightMotor1,motor_speed_right + steerLeft);
  
    digitalWrite(leftMotor1, LOW);
    digitalWrite(rightMotor2, LOW);
  }
  else if(leftRotate) { 
    analogWrite(leftMotor2,MAX_SPEED);
    analogWrite(rightMotor2,MAX_SPEED);
  
    digitalWrite(leftMotor1, LOW);
    digitalWrite(rightMotor1, LOW);
  }
  else if(rightRotate) { 
    analogWrite(leftMotor1,MAX_SPEED);
    analogWrite(rightMotor1,MAX_SPEED);
  
    digitalWrite(leftMotor2, LOW);
    digitalWrite(rightMotor2, LOW);
  }
  else {
    //turn all motors off 
    digitalWrite(rightMotor1, LOW);
    digitalWrite(rightMotor2, LOW);
    digitalWrite(leftMotor1, LOW);
    digitalWrite(leftMotor2, LOW);
  }
}

// function that handles the event if the android device sends and 'r' character.
// it checks whether forward or backward is set, will then set forward to true if neither is set.
// increses the speed of the left motor in order to turn the car to its left.
void rightEvent(byte flag, byte numOfValues) {
  if(!forward && !backward){
    rightRotate = true;
    leftRotate = false;
  }
  else {
    steerLeft = 0;
    steerRight = STEER_SPEED;
  }
}

// function that handles the event if the android device sends and 'l' character.
// it checks whether forward or backward is set, will then set forward to true if neither is set.
// increses the speed of the right motor in order to turn the car to its right
void leftEvent(byte flag, byte numOfValues) {
  if(!forward && !backward) {
    leftRotate = true;
    rightRotate = false;
  }
  else {
    steerLeft = STEER_SPEED;
    steerRight = 0;
  }
}

// function that handles the event if the android device sends and 'f' character.
// set forward to true.
// collects the int sent from the android device by the scroll bar 
void forwardEvent(byte flag, byte numOfValues) {
  forward = true;
  backward = false;
  motor_speed_left  = meetAndroid.getInt()*((MAX_SPEED-STEER_SPEED)/70);
  motor_speed_right = meetAndroid.getInt()*((MAX_SPEED-STEER_SPEED)/70);
}

// turn both wheels backwards
void reverseEvent(byte flag, byte numOfValues) {
  forward = false;
  backward = true;
  motor_speed_left  = meetAndroid.getInt()*((MAX_SPEED-STEER_SPEED)/40);
  motor_speed_right = meetAndroid.getInt()*((MAX_SPEED-STEER_SPEED)/40);
}


void resetSteeringEvent(byte flag, byte numOfValues){
 resetSteering();
}  
  
void stopEvent(byte flag, byte numOfValues) {
  stopAll();
}

void resetSteering(){
 steerLeft = STEER_SPEED;
 steerRight = STEER_SPEED; 
 rightRotate = false;
 leftRotate = false;
}

void stopAll(){
  forward = false;
  backward = false;
  motor_speed_left = 0;
  motor_speed_right = 0;
  resetSteering();
}

void checkSensorValues(byte flag, byte numOfValues){
  checkSensors();
}
void checkSensors(){
  int rightIRsensorVal = analogRead(rightIRsensorPin);
  int leftIRsensorVal = analogRead(leftIRsensorPin);
  
  //put sensorvalues together and seperate with ','
  char charBuf[50];
  String out = "";
  
  out += rightIRsensorVal;
  out += ',';
  out += leftIRsensorVal;
  
  out.toCharArray(charBuf, 10); //convert it to a char array in charBuf
  meetAndroid.send(charBuf);    //send to android
}

