package mqtt_ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class mqtt_ui extends JFrame implements ActionListener {

	// Pub・SubできるTopic数
	static int num_of_topic = 1;

	// UIのWi-FiおよびMQTTサーバー接続設定のテキストエリア（入力フォーム）
	JTextField TOPICNUM;
	JTextField WIFI_SSID;
	JPasswordField WIFI_PASSWORD;
	JTextField MQTT_CLIENTID;
	JTextField MQTT_SERVER;
	JTextField MQTT_PORT;
	JTextField MQTT_USERNAME;
	JPasswordField MQTT_PASSWORD;
	JTextField COMPORT;

	// UIのPubSub設定のテキストエリア,
	JComboBox<String> mqtt_frame = new JComboBox<String>();// Arduinoのボード選択
	ArrayList<JComboBox<String>> pubsub_combo = new ArrayList<JComboBox<String>>();
	ArrayList<JTextField> topic = new ArrayList<JTextField>();
	ArrayList<JButton> config = new ArrayList<JButton>();
	ArrayList<JLabel> what = new ArrayList<JLabel>();

	// UI自体が持つstatic変数
	static int select;
	static String state;
	ArrayList<Topic> topicArray = new ArrayList<Topic>();
	static Connection connect;
	MakeCode code;

	static mqtt_ui frame;
	MqttClient mqtt;
	Test test;

	public static void main(String[] args) {
		connect = new Connection();
		frame = new mqtt_ui("MQTT UI For Arduino");
		frame.setVisible(true);
		frame.setResizable(false);
	}

	// メインUIを描写する関数
	mqtt_ui(String title) {
		setBounds(30, 30, 700, num_of_topic * 50 + 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());

		File file = new File("./\\ins");
		File files[] = file.listFiles();

		// 取得した一覧を表示する

		mqtt_frame.addItem("");
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (filename.substring(0, 3).equals("Con")) {
				mqtt_frame.addItem(filename.substring(4, filename.length() - 4));
			}
		}

		TOPICNUM = new JTextField(String.valueOf(num_of_topic), 2);
		WIFI_SSID = new JTextField("", 20);
		WIFI_PASSWORD = new JPasswordField("", 20);
		MQTT_CLIENTID = new JTextField("", 20);
		MQTT_SERVER = new JTextField("", 20);
		MQTT_PORT = new JTextField("", 20);
		MQTT_USERNAME = new JTextField("", 20);
		MQTT_PASSWORD = new JPasswordField("", 20);
		COMPORT = new JTextField(5);

		for (int i = 0; i < num_of_topic; i++) {
			topicArray.add(new Topic());
			topic.add(new JTextField("", 20));
			what.add(new JLabel(""));
			pubsub_combo.add(new JComboBox<String>());
			pubsub_combo.get(i).addItem("Pub");
			pubsub_combo.get(i).addItem("Sub");
			config.add(new JButton("Config"));
			config.get(i).setActionCommand("Config" + i);
			config.get(i).addActionListener(this);
		}

		JButton button_change = new JButton("Change");
		JButton button_save = new JButton("Save");
		JButton button_open = new JButton("Open");
		JButton button_send = new JButton("Send");
		JButton button_code = new JButton("Code");
		JButton button_test = new JButton("Test");
		button_change.addActionListener(this);
		button_save.addActionListener(this);
		button_open.addActionListener(this);
		button_send.addActionListener(this);
		button_code.addActionListener(this);
		button_test.addActionListener(this);
		mqtt_frame.addActionListener(this);

		setLayout(new BorderLayout());

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));

		p.add(mqtt_frame);
		JLabel topic_print = new JLabel("	The number of Topic");
		p.add(topic_print);
		p.add(TOPICNUM);
		p.add(button_change);
		p.add(button_test);
		p.add(button_save);
		p.add(button_open);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel wifi_ssid_print = new JLabel("Wi-Fi SSID");
		wifi_ssid_print.setPreferredSize(new Dimension(110, 20));
		p.add(wifi_ssid_print);
		p.add(WIFI_SSID);
		JLabel wifi_password_print = new JLabel("Wi-Fi PASSWORD");
		wifi_password_print.setPreferredSize(new Dimension(110, 20));
		p.add(wifi_password_print);
		p.add(WIFI_PASSWORD);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel clientid_print = new JLabel("Client ID");
		clientid_print.setPreferredSize(new Dimension(110, 20));
		p.add(clientid_print);
		p.add(MQTT_CLIENTID);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel server_print = new JLabel("MQTT SERVER");
		server_print.setPreferredSize(new Dimension(110, 20));
		p.add(server_print);
		p.add(MQTT_SERVER);
		JLabel port_print = new JLabel("MQTT PORT");
		port_print.setPreferredSize(new Dimension(110, 20));
		p.add(port_print);
		p.add(MQTT_PORT);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel username_print = new JLabel("MQTT USERNAME");
		username_print.setPreferredSize(new Dimension(110, 20));
		p.add(username_print);
		p.add(MQTT_USERNAME);
		JLabel password_print = new JLabel("MQTT PASSWORD");
		password_print.setPreferredSize(new Dimension(110, 20));
		p.add(password_print);
		p.add(MQTT_PASSWORD);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel pubsub_print = new JLabel("Pub/Sub");
		pubsub_print.setPreferredSize(new Dimension(145, 20));
		JLabel Topic_print = new JLabel("Topic");
		Topic_print.setPreferredSize(new Dimension(135, 20));
		p.add(pubsub_print);
		p.add(Topic_print);
		main.add(p);

		for (int i = 0; i < num_of_topic; i++) {
			p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(pubsub_combo.get(i));
			p.add(topic.get(i));
			p.add(config.get(i));
			p.add(what.get(i));
			main.add(p);
		}

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel COMPORT_print = new JLabel("COM");
		p.add(COMPORT_print);
		p.add(COMPORT);
		p.add(button_send);
		p.add(button_code);

		main.add(p);

		add("North", main);

	}

	// ボタンを押したときに呼ばれる関数
	public void actionPerformed(ActionEvent e) {
		String cmdName = e.getActionCommand();
		connect.setWifi_ssid(WIFI_SSID.getText());
		connect.setWifi_password(WIFI_PASSWORD.getPassword());
		connect.setMqtt_clientid(MQTT_CLIENTID.getText());
		connect.setMqtt_server(MQTT_SERVER.getText());
		connect.setMqtt_port(MQTT_PORT.getText());
		connect.setMqtt_username(MQTT_USERNAME.getText());
		connect.setMqtt_password(MQTT_PASSWORD.getPassword());

		for (int i = 0; i < num_of_topic; i++) {
			topicArray.get(i).setTopic(topic.get(i).getText());
			topicArray.get(i).setPubsub((String) pubsub_combo.get(i).getSelectedItem());
			what.get(i).setText(topicArray.get(i).getWhat());
		}

		if (mqtt_frame.getSelectedItem().equals("ArduinoYun")) {
			WIFI_SSID.setEditable(false);
			WIFI_SSID.setText("");
			WIFI_PASSWORD.setEditable(false);
			WIFI_PASSWORD.setText("");
		} else {
			WIFI_SSID.setEditable(true);
			WIFI_PASSWORD.setEditable(true);
		}

		if (cmdName == "Send" || cmdName == "Code") {
			connect.setup((String) mqtt_frame.getSelectedItem());
			code = new MakeCode(connect, topicArray);
			try {
				String name = System.getProperty("user.home");
				File dir = new File(name + "\\Documents\\arduino\\mufa");
				if (!dir.exists()) {
					dir.mkdir();
				}
				File file = new File(name + "\\Documents\\arduino\\mufa\\mufa.ino");

				if (file.isFile() && file.canWrite() && file.exists()) {
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

					for (int i = 0; i < code.getCodeSize(); i++) {
						pw.println(code.getCode(i));
					}
					pw.close();
				} else {
					file.createNewFile();
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

					for (int i = 0; i < code.getCodeSize(); i++) {
						pw.println(code.getCode(i));
					}

					pw.close();
				}
				Runtime r = Runtime.getRuntime();
				if (cmdName == "Send") {
					r.exec("C:\\Program Files (x86)\\Arduino\\arduino.exe --upload --board " + connect.getboard()
							+ " --port COM" + COMPORT.getText() + " " + name + "/Documents/arduino/mufa/mufa.ino");
				} else {
					r.exec("C:\\Program Files (x86)\\Arduino\\arduino.exe  " + name
							+ "/Documents/arduino/mufa/mufa.ino");

				}
			} catch (IOException error) {
				System.out.println(e);
			}

		} else if (cmdName == "Change") {
			num_of_topic = Integer.parseInt(TOPICNUM.getText());
			frame.setVisible(false);
			frame = new mqtt_ui("MQTT");
			frame.setVisible(true);
			frame.setResizable(false);

		} else if (cmdName == "Test") {
			if (test!=null) {
				test.getDefaultCloseOperation();
				test.dispose();
				test=null;
			}
			test = new Test(connect);
		}else if(cmdName=="Save"){
			try {
				connect.saveData();
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}else if(cmdName=="Open"){
			connect = new Connection();
			connect.openData();

			WIFI_SSID.setText(connect.getWifi_ssid());
			WIFI_PASSWORD.setText(connect.getWifi_password().toString());
			MQTT_CLIENTID.setText(connect.getMqtt_clientid());
			MQTT_SERVER.setText(connect.getMqtt_server());
			MQTT_PORT.setText(connect.getMqtt_port());
			MQTT_USERNAME.setText(connect.getMqtt_username());
			MQTT_PASSWORD.setText(connect.getMqtt_password().toString());


		}else if (cmdName.length() > 5) {
			if (cmdName.substring(0, 6).equals("Config")) {

				select = Integer.parseInt(cmdName.substring(6, cmdName.length()));
				state = (String) pubsub_combo.get(select).getSelectedItem();
				if (state.equals("Pub")) {
					new PubConfig("PubConfig", topicArray.get(select));
				} else {
					new SubConfig("SubConfig", topicArray.get(select));
				}
			}
		}

	}

}