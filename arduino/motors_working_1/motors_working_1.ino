/*
  Recieves The eventt sent from android app
*/
 
#include <MeetAndroid.h>

// create the meetAndsoid for recieving data from the android app
MeetAndroid meetAndroid;

int motor_speed_left = 100;
int motor_speed_right = 100;

int rightMotor1 = 6;      //connects to 6 on sumophor
int rightMotor2 = 9;      //connects to 5 on sumophor
int leftMotor1 = 10;    //connects to 4 on sumophor
int leftMotor2 = 11;    //connects to 3 on sumophor

boolean forward = false;
boolean backward = false;

void setup() {  
  // use the baud rate your bluetooth module is configured to 
  // not all baud rates are working well, i.e. ATMEGA168 works best with 57600
  Serial.begin(9600); 
  
  // register callback functions, which will be called when an associated event occurs.
  // - the first parameter is the name of your function (see below)
  // - match the second parameter ('A', 'B', 'a', etc...) with the flag on your Android application
  meetAndroid.registerFunction(leftEvent, 'l');  
  meetAndroid.registerFunction(rightEvent, 'r');
  meetAndroid.registerFunction(forwardEvent, 'f');
  meetAndroid.registerFunction(reverseEvent, 'b');
  meetAndroid.registerFunction(stopEvent, 's');
  
  // initialize the digital pin as an output.
  pinMode(leftMotor1, OUTPUT);  
  pinMode(leftMotor2, OUTPUT); 
  pinMode(rightMotor1, OUTPUT); 
  pinMode(rightMotor2, OUTPUT); 
  
  //make all motors off on startup
  digitalWrite(rightMotor1, LOW);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, LOW);
  digitalWrite(leftMotor2, LOW);
}

void loop() {
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
  
  if(forward){
    analogWrite(leftMotor1,motor_speed_left);
    analogWrite(rightMotor2,motor_speed_right);
  
    digitalWrite(leftMotor2, LOW);
    digitalWrite(rightMotor1, LOW);
  }
  else if(backward){
    analogWrite(leftMotor2,motor_speed_left);
    analogWrite(rightMotor1,motor_speed_right);
  
    digitalWrite(leftMotor1, LOW);
    digitalWrite(rightMotor2, LOW);
  }
}

/*
 * This method is called constantly.
 * note: flag is in this case 'r'
 */
void rightEvent(byte flag, byte numOfValues) {
  motor_speed_right = 50;
  motor_speed_left = 100;
}

void leftEvent(byte flag, byte numOfValues) {
  motor_speed_right= 100;
  motor_speed_left = 50;
}

//turn both wheels forward at motor_speed
void forwardEvent(byte flag, byte numOfValues) {
  forward = true;
  backward = false;
  motor_speed_left = 100;
  motor_speed_right = 100;
}

//turn both wheels backwards at motor_speed
void reverseEvent(byte flag, byte numOfValues) {
  forward = false;
  backward = true;
  motor_speed_left = 100;
  motor_speed_right = 100;
}

void stopEvent(byte flag, byte numOfValues) {
  motor_speed_left = 0;
  motor_speed_right = 0;
}

