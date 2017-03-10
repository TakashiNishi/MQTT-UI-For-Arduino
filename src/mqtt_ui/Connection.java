package mqtt_ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Connection{
	static String arduino;
	static String wifi_ssid;
	static char[] wifi_password;
	static String mqtt_clientid;
	static String mqtt_server;
	static String mqtt_port;
	static String mqtt_username;
	static char[] mqtt_password;
	static ArrayList<String> log = new ArrayList<String>();
	static ArrayList<String> pos = new ArrayList<String>();
	static ArrayList<String> att = new ArrayList<String>();
	static MqttClient mqttclient;
	String board;
	boolean isConnected = false;
	MqttClient mqttClient;

	public String getboard(){
		return board;
	}

	public void setup(String Con){
		try {
			File file = new File("./ins/Con_" + Con + ".txt");
			FileReader filereader = new FileReader(file);
			board ="";
			log.clear();
			pos.clear();
			att.clear();
			int ch;
			int m = 0;

			String temp = "";
			String command = "";
			while ((ch = filereader.read()) != -1) {
				if (ch == 10) {
					m = 0;
				} else if (ch == 58) {
					if (m == 0) {
						command = temp;
					}
					if (command.equals("Package")) {
						if(m==1){
							board = board + temp;
							board = board + ":";
						}
					}
					if (command.equals("Arch")) {
						if(m==1){
							board = board + temp;
							board = board + ":";
						}
					}
					if (command.equals("Board")) {
						if(m==1){
							board = board + temp;
						}
					}
					if (command.equals("Log")) {
						if (m == 1) {
							log.add(temp);
							att.add("arduino");
						} else if (m == 2) {
							pos.add(temp);
						}
					}
					temp = "";

					m = m + 1;
				} else {
					if (ch != 13) {
						temp = temp + (char) ch;

					}
				}
			}
			filereader.close();
		} catch (FileNotFoundException er) {
			System.out.println(er);
		} catch (IOException er) {
			System.out.println(er);
		}

	}
	public void mqttpublish(Topic topic) {
		MqttMessage message = new MqttMessage(topic.getValue().getBytes());
		message.setQos(topic.getQoS());
		try {
			mqttClient.publish(topic.getTopic(), message);
		} catch (MqttException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}



	public void mqttpublish(String topic, String mes) {
		MqttMessage message = new MqttMessage(mes.getBytes());
		try {
			mqttClient.publish(topic, message);
		} catch (MqttException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}


	public void mqttconnect() {
		try {
			mqttClient = new MqttClient("tcp://" + getMqtt_server() + ":" + getMqtt_port(),getMqtt_clientid());
			MqttConnectOptions connOpt = new MqttConnectOptions();
			connOpt.setUserName(getMqtt_username());
			connOpt.setPassword(getMqtt_password());
			mqttClient.connect(connOpt);
			isConnected = true;
		} catch (MqttException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void mqttdisconnect() {
		try {
			mqttClient.disconnectForcibly();
			isConnected = false;
		} catch (MqttException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public boolean mqttisconnected(){
		return isConnected;
	}


	public String getLog(int i) {
		return log.get(i);
	}

	public String getPos(int i) {
		return pos.get(i);
	}

	public String getAtt(int i) {
		return att.get(i);
	}

	public void setArduino(String arduino) {
		for (int i = 0; i < att.size(); i++) {
			if (att.get(i).equals("arduino")) {
				log.remove(i);
				pos.remove(i);
				att.remove(i);
				i--;
			}
		}

		switch (arduino) {
		case "Arduino Yun":
	//		new CN_ArduinoYun(log, pos, att);
			break;
		case "ESP8266":
		//	new CN_ESP8266(log, pos, att);
			break;
		case "Edison for Arduino":
			//new CN_Edison(log, pos, att);
			break;
		}

	}

	public int getLogsize() {
		return log.size();
	}

	public void setWifi_ssid(String wifi_ssid) {
		Connection.wifi_ssid = wifi_ssid;
	}

	public static String getWifi_ssid() {
		return wifi_ssid;
	}

	public static char[] getWifi_password() {
		return wifi_password;
	}

	public static String getMqtt_clientid() {
		return mqtt_clientid;
	}

	public static String getMqtt_server() {
		return mqtt_server;
	}

	public static String getMqtt_port() {
		return mqtt_port;
	}

	public static String getMqtt_username() {
		return mqtt_username;
	}

	public static char[] getMqtt_password() {
		return mqtt_password;
	}

	public void setWifi_password(char[] cs) {
		Connection.wifi_password = cs;
	}

	public void setMqtt_clientid(String mqtt_clientid) {
		Connection.mqtt_clientid = mqtt_clientid;
	}

	public void setMqtt_server(String mqtt_server) {
		Connection.mqtt_server = mqtt_server;
	}

	public void setMqtt_port(String mqtt_port) {
		Connection.mqtt_port = mqtt_port;
	}

	public void setMqtt_username(String mqtt_username) {
		Connection.mqtt_username = mqtt_username;
	}

	public void setMqtt_password(char[] cs) {
		Connection.mqtt_password = cs;
	}
}
