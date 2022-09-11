// HC05 (Bluetooth модуль)//./////////////////////////////////
#include <SoftwareSerial.h>
SoftwareSerial mySerial(2, 3); // указываем пины rx и tx соответственно. Это на плате. Ответные пины на модуле наоборот.
long counterBluetooth;
// HC05 (Bluetooth модуль)///////////////////////////////////

void setup() {

  // Настройка bluetooth модуля
  pinMode(2, INPUT);
  pinMode(3, OUTPUT);
  mySerial.begin(9600);

  Serial.begin(9600);
}

void loop() {

  blueTest();
  //test();
  //test2();

}

void test2() {
  
  if (Serial.available() > 0) {
    mySerial.println("1");
    Serial.println("Sended");
  }

  
  }

void test() {
  if (Serial.available()) {
    String string = Serial.readString();
    mySerial.println(string);
  }

}

void blueTest() {
  //counterBluetooth
  //mySerial
  if (millis() - counterBluetooth > 180000) {
    counterBluetooth = millis();

    //mySerial.println("Position servo: " + String(myservo.read()));
    //mySerial.println("Test" + String(secondsSinceStart));

    mySerial.println("alarm");
  }
}
