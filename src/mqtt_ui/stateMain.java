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

public class stateMain extends JFrame implements ActionListener {

		// Pub・SubできるTopic数
		static int num_of_topic = 1;
		static int num_of_state = 1;
		static String name = "MS";

		// UIのWi-FiおよびMQTTサーバー接続設定のテキストエリア（入力フォーム）
		JTextField TOPICNUM;//Topic数
		JTextField WIFI_SSID;//Wi-Fi通信におけるのSSID
		JPasswordField WIFI_PASSWORD;//Wi-Fi通信におけるのPassword
		JTextField MQTT_CLIENTID;//MQTT通信におけるClientID.他のパブリッシャー、サブスクライバーとClientIDで被ってはいけない。
		JTextField MQTT_SERVER;//MQTT通信における接続するサーバー名
		JTextField MQTT_PORT;//MQTT通信における接続するのサーバーのポート
		JTextField MQTT_USERNAME;//MQTT通信におけるユーザーネーム
		JPasswordField MQTT_PASSWORD;//MQTT通信におけるユーザーネームに対するパスワード
		JTextField COMPORT;//COMポートを入力、ArduinoのIDEで確認


		// UIのPubSub設定のテキストエリア
		JComboBox<String> mqtt_frame = new JComboBox<String>();// Arduinoのボード選択
		ArrayList<JComboBox<String>> pubsub_combo = new ArrayList<JComboBox<String>>();//PubかSubを設定するコンボボックス
		ArrayList<JTextField> topic = new ArrayList<JTextField>();//topicを入力する欄
		ArrayList<JButton> config = new ArrayList<JButton>();//topicの設定をするボタン
		ArrayList<JLabel> what = new ArrayList<JLabel>();//
		ArrayList<stateMain> state = new ArrayList<stateMain>();

		// UI自体が持つstatic変数
		static int select;//Topicを複数設定するときに使う。何番目のTopicか
		ArrayList<Topic> topicArray = new ArrayList<Topic>();//TopicクラスのArrayList
		static Connection connect;//Wi-FiやMQTT通信に関するデータが入っているクラス

		static stateMain frame;//UIに関するクラス

		Test test;//実際にTestするときに使用するクラス

		public static void main(String[] args) {
			connect = new Connection();//connectのクラス生成
			frame = new stateMain("MQTT UI For Arduino");//UIを呼び出す
			frame.setVisible(true);//UIを可視化
			frame.setResizable(false);//UIのサイズ変更不可
		}
		
		stateMain(int i){
			name = name + i;
		}
		
		// メインUIを描写する関数
		stateMain(String title) {

			//UIの初期設定
			setBounds(30, 30, 700, num_of_topic * 50 + 300);//UIのサイズ指定
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//画面の×ボタン押したときに綴じるように設定
			setTitle(title);//UIのタイトル指定
			ImageIcon icon = new ImageIcon("./img/MQTT.png");//UIのアイコンURL指定
			setIconImage(icon.getImage());//アイコンをUIに張り付ける

			//ボードのファイルの選択肢
			File file = new File("./\\ins");//insファイルを参照
			File files[] = file.listFiles();
			mqtt_frame.addItem("");//ボードのコンボボックスに空白を追加
			for (int i = 0; i < files.length; i++) {//このfor分でinsフォルダの中のうち、ファイル名の最初3文字がConであるファイルをボードのコンボボックスに追加
				String filename = files[i].getName();
				if (filename.substring(0, 3).equals("Con")) {
					mqtt_frame.addItem(filename.substring(4, filename.length() - 4));
				}
			}

			//UIの通信関係の入力フォーム・ボタンの初期化・設定
			TOPICNUM = new JTextField(String.valueOf(num_of_topic), 2);
			WIFI_SSID = new JTextField("", 20);
			WIFI_PASSWORD = new JPasswordField("", 20);
			MQTT_CLIENTID = new JTextField("", 20);
			MQTT_SERVER = new JTextField("", 20);
			MQTT_PORT = new JTextField("", 20);
			MQTT_USERNAME = new JTextField("", 20);
			MQTT_PASSWORD = new JPasswordField("", 20);
			COMPORT = new JTextField(5);
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


			JButton button_wait = new JButton("Wait");
			JButton button_prepare = new JButton("Prepare");
			JButton button_close = new JButton("Close");
			button_wait.addActionListener(this);
			button_prepare.addActionListener(this);
			button_close.addActionListener(this);





			//UIのTopic設定の入力フォームの初期化・設定
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


			//以下、UIの描写に関する内容。一行空きはUIにおける改行部分
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
			String cmdName = e.getActionCommand();//コマンド名の取得

			//connectクラスに通信系の設定をsetする
			connect.setWifi_ssid(WIFI_SSID.getText());
			connect.setWifi_password(WIFI_PASSWORD.getPassword());
			connect.setMqtt_clientid(MQTT_CLIENTID.getText());
			connect.setMqtt_server(MQTT_SERVER.getText());
			connect.setMqtt_port(MQTT_PORT.getText());
			connect.setMqtt_username(MQTT_USERNAME.getText());
			connect.setMqtt_password(MQTT_PASSWORD.getPassword());

			//topicのArrayにTopicの情報を保存
			for (int i = 0; i < num_of_topic; i++) {
				topicArray.get(i).setTopic(topic.get(i).getText());
				topicArray.get(i).setPubsub((String) pubsub_combo.get(i).getSelectedItem());
				what.get(i).setText(topicArray.get(i).getWhat());
			}


			//もしボードがArduinoYunならWi-Fiの設定を入力不可にする
			if (mqtt_frame.getSelectedItem().equals("ArduinoYun")) {
				WIFI_SSID.setEditable(false);
				WIFI_SSID.setText("");
				WIFI_PASSWORD.setEditable(false);
				WIFI_PASSWORD.setText("");
			} else {
				WIFI_SSID.setEditable(true);
				WIFI_PASSWORD.setEditable(true);
			}

			//以下コマンド名による場合分け、コマンド名はボタン名に準ずる
			if (cmdName == "Send" || cmdName == "Code") {//もしボタン名がSend,Codeなら

				connect.setup((String) mqtt_frame.getSelectedItem());//ボード情報を読み取り、Arduinoコード化をする
				MakeCode code = new MakeCode(connect, topicArray);//MakeCodeクラスにて、Topicで設定した部分をコード化する
				try {
					String name = System.getProperty("user.home");//ユーザーのディレクトリのパスを取得（Windows限定）
					File dir = new File(name + "\\Documents\\arduino\\mufa");//arduinoのファイルが入っているフォルダのディレクトリを指定（Windows限定）
					if (!dir.exists()) {//もしdirのフォルダが存在しなかったら
						dir.mkdir();//dirのフォルダを作成する
					}
					File file = new File(name + "\\Documents\\arduino\\mufa\\mufa.ino");//本UIで作成されたarduinoのファイルのディレクトリを指定（Windows限定）

					//本UIにて作成されるArduinoファイルの名前はmufaである
					if (file.isFile() && file.canWrite() && file.exists()) {//もし以前に本UIを用い、Arduinoファイルが作られていたら内容を新しいのに書き換える
						PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
						for (int i = 0; i < code.getCodeSize(); i++) {
							pw.println(code.getCode(i));
						}
						pw.close();
					} else {//もし初めてUIを使用するときは新しいArduinoファイルを作成、書き込む
						file.createNewFile();
						PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
						for (int i = 0; i < code.getCodeSize(); i++) {
							pw.println(code.getCode(i));
						}
						pw.close();
					}

					//以下OSのコマンドラインを用いてArduinoを起動する
					Runtime r = Runtime.getRuntime();
					if (cmdName == "Send") {//もしボタン名がSendなら
						//OSに以下のコマンドを送り、Arduino本体にコードを送る
						r.exec("C:\\Program Files (x86)\\Arduino\\arduino --upload --board " + connect.getboard()
								+ " --port COM" + COMPORT.getText() + " " + name + "/Documents/arduino/mufa/mufa.ino");
					} else {//もしボタン名がCodeなら
						//OSに以下のコマンドを送り、コードをArduinoのIDEで開く
						r.exec("C:\\Program Files (x86)\\Arduino\\arduino " + name + "\\Documents\\arduino\\mufa\\mufa.ino");
					}
				} catch (IOException error) {
					System.out.println(e);
				}

			} else if (cmdName == "Change") {//もしボタン名がChangeなら
				//topic数を変更、UIを開きなおす
				num_of_topic = Integer.parseInt(TOPICNUM.getText());
				frame.setVisible(false);
				frame = new stateMain("MQTT");
				frame.setVisible(true);
				frame.setResizable(false);

			} else if (cmdName == "Test") {//もしボタン名がTestなら
				//MQTT通信のライブラリで開けるのは1クラスのみなので他にTestクラスが開いていた場合、全て閉じる
				if (test!=null) {
					test.getDefaultCloseOperation();
					test.dispose();
					test=null;
				}
				test = new Test(connect);//TestのUIクラスを呼び出す

			}else if(cmdName=="Save"){//もしボタン名がSaveなら
				try {
					connect.saveData();//Connectionクラスから通信関係のデータを保存する
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

			}else if(cmdName=="Open"){//もしボタン名がOpenなら
				connect = new Connection();
				connect.openData();//Connectionクラスから通信関係のデータを呼び出す

				//以下、呼び出したデータを入力フォームに書き込む
				WIFI_SSID.setText(connect.getWifi_ssid());
				WIFI_PASSWORD.setText(String.valueOf(connect.getWifi_password()));
				MQTT_CLIENTID.setText(connect.getMqtt_clientid());
				MQTT_SERVER.setText(connect.getMqtt_server());
				MQTT_PORT.setText(connect.getMqtt_port());
				MQTT_USERNAME.setText(connect.getMqtt_username());
				MQTT_PASSWORD.setText(String.valueOf(connect.getMqtt_password()));


			}else if (cmdName.length() > 5) {//もし文字が5文字以上
				if (cmdName.substring(0, 6).equals("Config")) {//かつボタン名の名前の最初6文字がConfigなら
					select = Integer.parseInt(cmdName.substring(6, cmdName.length()));//Topicの何番目かをselectに保存
					String state = (String) pubsub_combo.get(select).getSelectedItem();//指定されたTopicがPub/Subのどちらかを取得
					if (state.equals("Pub")) {//もしPubなら
						new PubConfig("PubConfig", topicArray.get(select),select);//PubConfigのUIクラスを呼び出す
					} else {//もしSubなら
						new SubConfig("SubConfig", topicArray.get(select),select);//SubConfigのUIクラスを呼び出す
					}
				}
			}

		}

	}
