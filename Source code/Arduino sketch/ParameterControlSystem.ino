#include "DHT.h" 
const int digital = 2; // детектор пламени 2 пин (Digital)
int MQ2_a = 2; // Датчик газа на 2 пин (Analog)
int A;
int D; 
DHT dht(7, DHT11); // Датчик темп/вл 7. пин (Digital)

void setup() {
  pinMode(digital, INPUT);
  Serial.begin(9600);
  dht.begin();
}

void loop() {
  D = digitalRead(digital);
  if (D == 0)
    {
    //fire_true
      Serial.write("1");
      delay(500);
    }
 A = analogRead(MQ2_a);
  if (A > 100)
  {
     Serial.write("2"); 
      delay(500); 
  }

  

  int h = dht.readHumidity();
  int t = dht.readTemperature();
  Serial.write("4");
  delay(400);
  Serial.print(t,DEC);
  delay(500);
  Serial.write("5");
  delay(400);
  Serial.print(h,DEC);
  delay(500);
}
