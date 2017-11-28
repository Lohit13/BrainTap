/*
 *  This sketch sends data via HTTP GET requests to data.sparkfun.com service.
 *
 *  You need to get streamId and privateKey at data.sparkfun.com and paste them
 *  below. Or just customize this script to talk to other HTTP servers.
 *
 */

#include <ESP8266WiFi.h>

const char* ssid     = "BrainTap";
const char* password = "braintap";

const char* host = "192.168.1.92";
String s1;
void setup() {
  Serial.begin(115200);
  delay(10);

  // We start by connecting to a WiFi network
   pinMode(13, OUTPUT);  
   digitalWrite(13,LOW);
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

int value = 0;

void loop() {
 // delay(5000);
  ++value;

  Serial.print("connecting to ");
  Serial.println(host);
  
  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  const int httpPort =8080;
  if (!client.connect(host, httpPort)) {
    Serial.println("connection failed");
    return;
  }
  
  // We now create a URI for the request
  String url = "/final";
 
  
  Serial.print("Requesting URL: ");
  Serial.println(url);
  
  // This will send the request to the server
  client.print(String("GET ") + url + " HTTP/1.1\r\n" +
               "Host: " + host + "\r\n" + 
               "Connection: close\r\n\r\n");
  unsigned long timeout = millis();

  String line;
  // Read all the lines of the reply from server and print them to Serial
     while (client.available()){ 
    line = client.readStringUntil('\r');
    //delay(1000);
    }
    Serial.print(line);
    if(line=="\n1"){
    Serial.println("yes");
    digitalWrite(13, HIGH);   // Turn the LED on (Note that LOW is the voltage level
    delay(1000);                                   // but actually the LED is on; this is because                                  // it is acive low on the ESP-01) 
    } 
    else if(line=="\n0"){
    Serial.println("NO");
    digitalWrite(13, LOW);   // Turn the LED on (Note that LOW is the voltage level
    delay(1000);                                   // but actually the LED is on; this is because                                  // it is acive low on the ESP-01) 
    } 
    delay(1000);
  
  Serial.println();
  Serial.println("closing connection");
  
}

