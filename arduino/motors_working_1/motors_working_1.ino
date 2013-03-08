/*
  Recieves The eventt sent from android app
*/
 
#include <MeetAndroid.h>

// create the meetAndsoid for recieving data from the android app
MeetAndroid meetAndroid;

int leftMotor1 = 8;
int leftMotor2 = 9;
int rightMotor1 = 10;
int rightMotor2 = 11;

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
  
  digitalWrite(rightMotor1, LOW);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, LOW);
  digitalWrite(leftMotor2, LOW);
}

void loop() {
  meetAndroid.receive(); // you need to keep this in your loop() to receive events
}

/*
 * This method is called constantly.
 * note: flag is in this case 'A' and numOfValues is 0 (since test event doesn't send any data)
 */
void rightEvent(byte flag, byte numOfValues) {
  digitalWrite(rightMotor1, HIGH);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, LOW);
  digitalWrite(leftMotor2, LOW);
}

void leftEvent(byte flag, byte numOfValues) {
  digitalWrite(rightMotor1, LOW);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, HIGH);
  digitalWrite(leftMotor2, LOW);
}

void forwardEvent(byte flag, byte numOfValues) {
  digitalWrite(rightMotor1, HIGH);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, HIGH);
  digitalWrite(leftMotor2, LOW);
}

void reverseEvent(byte flag, byte numOfValues) {
  digitalWrite(rightMotor1, LOW);
  digitalWrite(rightMotor2, HIGH);
  digitalWrite(leftMotor1, LOW);
  digitalWrite(leftMotor2, HIGH);
}

void stopEvent(byte flag, byte numOfValues) {
  digitalWrite(rightMotor1, LOW);
  digitalWrite(rightMotor2, LOW);
  digitalWrite(leftMotor1, LOW);
  digitalWrite(leftMotor2, LOW);
}

