#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <ESPAsyncWebServer.h>
#include <Arduino_JSON.h>
#include <ESP8266HTTPClient.h>
#include "LittleFS.h"
#include <EEPROM.h>
#include <AccelStepper.h>

#define APSSID "teleLoop"
#define APPSK  "loopedtele"
#define SP1 5
#define DP1 4
#define SP2 14
#define DP2 12

AccelStepper stepperX(AccelStepper::DRIVER, SP1, DP1);
AccelStepper stepperY(AccelStepper::DRIVER, SP2, DP2);

WiFiClient client;
HTTPClient http;

const char *ssidAccessPoint = APSSID;
const char *passwordAccessPoint = APPSK;
const char* serverNameTele = "http://192.168.0.105:8080/tele";
const char* serverNameData = "http://192.168.0.105:8080/data";
const int uid = 44445;

String message = "";
String command = "";
String ssidOfHost;
String passwordOfHost;
String axis;
int degreeX = 0;
int degreeY = 0;
int prevDegreeX = 0;
bool isConnectivity = false;
bool isSendData = false;
bool canMoveX = true;
bool canMoveY = true;

AsyncWebServer server(80);
AsyncWebSocket ws("/ws");

void notifyClientsValueX() {
  ws.textAll("X:" + String(degreeX));
}

void notifyClientsValueY() {
  ws.textAll("Y:" + String(degreeY));
}

void notifyClientsValueCanMoveX() {
  ws.textAll("MX:" + String(canMoveX));
}

void notifyClientsValueCanMoveY() {
  ws.textAll("MY:" + String(canMoveY));
}

void notifyClientsValueS() {
  if (isConnectivity) {
    IPAddress ipAddress = WiFi.localIP();
    String localIp = String(ipAddress[0]) + String(".") + String(ipAddress[1]) + \
                     String(".") + String(ipAddress[2]) + String(".") + String(ipAddress[3]);
    ws.textAll("S:" + String(ssidOfHost) + "|" + localIp);
  }
}

bool testConnectivity() {
  for(int i = 0; i < 20; i++) {
    if (WiFi.status() == WL_CONNECTED) {
      Serial.println("");
      return true;
    }
    delay(200);
    Serial.print("*");
  }
  Serial.println("");
  return false;
}

String readSsidEeprom() {
  Serial.println("Reading Eeprom ssid...");
  String ssidEeprom = "";
  for (int i = 0; i < 32; i++) {
    ssidEeprom += char(EEPROM.read(i));
  }
  return ssidEeprom;
}

String readPassEeprom() {
  Serial.println("Reading Eeprom pass...");
  String passEeprom = "";
  for (int i = 32; i < 96; i++) {
    passEeprom += char(EEPROM.read(i));
  }
  return passEeprom;
}

void cleanEeprom() {
  Serial.println("Clearing eeprom...");
  for (int i = 0; i < 512; i++) {
    EEPROM.write(i, 0);
  }
  EEPROM.commit();
}

void writeSsidEeprom(String ssidParam) {
  for (int i = 0; i < ssidParam.length(); ++i) {
    EEPROM.write(i, ssidParam[i]);
    Serial.print("Writting ssid: ");
    Serial.print(ssidParam[i]);
  }
  Serial.println();
  EEPROM.commit();
}

void writePassEeprom(String passParam) {
  for (int i = 0; i < passParam.length(); i++) {
    EEPROM.write(32 + i, passParam[i]);
    Serial.print("Writting pass: ");
    Serial.print(passParam[i]);
  }
  Serial.println();
  EEPROM.commit();
}

void loginProcessing(String ssidOfHost, String passwordOfHost) {
  ssidOfHost = message.substring(command.length() + 1, message.indexOf(":"));
  passwordOfHost = message.substring(message.indexOf(":")+1, message.length());
  Serial.print("ssidOfHost: ");
  Serial.println(ssidOfHost);
  Serial.print("passwordOfHost: ");
  Serial.println(passwordOfHost);
  cleanEeprom();
  writeSsidEeprom(ssidOfHost);
  writePassEeprom(passwordOfHost);
  ESP.reset();
}

void rotateProcessing(String axis) {
  axis = message.substring(command.length() + 1, message.indexOf(":"));
  Serial.print("axis: ");
  Serial.println(axis);
  
  if (axis == "X" && canMoveX == true) {
    canMoveX = false;
    notifyClientsValueCanMoveX();
    degreeX = message.substring(message.indexOf(":") + 1, message.length()).toInt();
    notifyClientsValueX();
    Serial.print("degreeX: ");
    Serial.println(String(degreeX));
    Serial.print("CurrentPosition: ");
    Serial.println(String(stepperX.currentPosition()));
    stepperX.move((degreeX - stepperX.currentPosition()) - ((degreeX - stepperX.currentPosition()) / 2));
    isSendData = true;
    canMoveX = true;
    notifyClientsValueCanMoveX();

  } else if (axis == "Y" && canMoveY == true) {
    canMoveY = false;
    notifyClientsValueCanMoveY();
    degreeY = message.substring(message.indexOf(":") + 1, message.length()).toInt();
    notifyClientsValueY();
    Serial.print("degreeY: ");
    Serial.println(String(degreeY));
    stepperY.move((degreeY - stepperY.currentPosition()) - ((degreeY - stepperY.currentPosition()) / 2));
    isSendData = true;
    canMoveY = true;
    notifyClientsValueCanMoveY();
  }
}

void resetProcessing(String message) {
  if (message.substring(command.length() + 1, message.length())) {
      Serial.print("Reset eeprom: ");
      cleanEeprom();
      ESP.reset();
  }
}

void handleWebSocketMessage(void *arg, uint8_t *data, size_t len) {
  AwsFrameInfo *info = (AwsFrameInfo*)arg;
  if (info->final && info->index == 0 && info->len == len && info->opcode == WS_TEXT) {
    data[len] = 0;
    message = (char*)data;
    command = message.substring(0, message.indexOf("|"));
    
    Serial.print("message: ");
    Serial.println(message);
    Serial.print("command: ");
    Serial.println(command);
    
    if (command == "LOGIN") {
      loginProcessing(ssidOfHost, passwordOfHost);
    } else if (command == "ROTATE") {
       rotateProcessing(axis);    
    } else if (command == "RESET") {
       resetProcessing(message);
    }
  }
}

void onEvent(AsyncWebSocket *server, AsyncWebSocketClient *client, AwsEventType type, void *arg, uint8_t *data, size_t len) {
  switch (type) {
    case WS_EVT_CONNECT:
      Serial.printf("WebSocket client #%u connected from %s\n", client->id(), client->remoteIP().toString().c_str());
      notifyClientsValueY();
      notifyClientsValueX();
      notifyClientsValueS();
      notifyClientsValueCanMoveX();
      notifyClientsValueCanMoveY();
      break;
    case WS_EVT_DISCONNECT:
      Serial.printf("WebSocket client #%u disconnected\n", client->id());
      break;
    case WS_EVT_DATA:
        handleWebSocketMessage(arg, data, len);
        break;
    case WS_EVT_PONG:
    case WS_EVT_ERROR:
     break;
  }
}

void initWebSocket() {
  ws.onEvent(onEvent);
  server.addHandler(&ws);
  Serial.println("WebSocket server started");
}

void initFS() {
  if (!LittleFS.begin()) {
    Serial.println("An error has occurred while mounting LittleFS");
  } else {
    Serial.println("LittleFS mounted successfully");
  }
}

void initWiFi() {
  WiFi.disconnect();
  ssidOfHost = readSsidEeprom();
  passwordOfHost = readPassEeprom();
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssidOfHost, passwordOfHost);
  
  Serial.println("Checking Connectivity...");
  Serial.print("Configuring access point...");
  WiFi.softAP(ssidAccessPoint, passwordAccessPoint);
  Serial.println("HTTP server started on ip");
  Serial.print("SoftAP IP: ");
  Serial.println(WiFi.softAPIP());
  if (testConnectivity()) {
    isConnectivity = true;
    Serial.println("Connectivity success!");
    Serial.print("Local IP: ");
    Serial.println(WiFi.localIP());
    postTele();
  }
}

void postTele() {
  http.begin(client, serverNameTele);
  http.addHeader("Content-Type", "application/json");
  int httpResponseCode = http.POST("{\"uid\":" + String(uid) + "}");

  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);
      
  http.end();
}

void postData(int x, int y) {
  http.begin(client, serverNameData);
  http.addHeader("Content-Type", "application/json");\
  String msg = "{\"uid\":" + String(uid) + ",\"x\":" + String(x) + ",\"y\":" + String(y) + "}";
  int httpResponseCode = http.POST(msg);

  Serial.print("HTTP Response code: ");
  Serial.println(httpResponseCode);
      
  http.end();
}

void setup() {
  Serial.begin(115200);
  EEPROM.begin(512);
  delay(10);
  
  initWiFi();
  initWebSocket();
  initFS();

  stepperX.setMaxSpeed(60);
  stepperX.setAcceleration(200);
  stepperY.setMaxSpeed(60);
  stepperY.setAcceleration(200);
  
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send(LittleFS, "/index.html", "text/html");
  });
  server.serveStatic("/", LittleFS, "/");
  server.begin();
}

void loop() {
  if (isSendData == true){  
     postData(stepperX.currentPosition(), stepperY.currentPosition());
     isSendData = false;
  }
  ws.cleanupClients();
  stepperX.run();
  stepperY.run();
  
}
