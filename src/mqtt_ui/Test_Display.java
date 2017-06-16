package mqtt_ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import processing.core.PApplet;
import processing.core.PSurface;

//Testクラスから呼び出されるクラス
//Pub,Subのテストをする
//現段階では入力した値をPub,Subした値をログや折れ線グラフにする
//Mqttライブラリの仕様より、この一つのクラスのみを用いて3種類のテストを並列処理する
public class Test_Display extends JFrame implements MqttCallback {
	Connection connect;

	ArrayList<JTextField> input1 = new ArrayList<JTextField>();
	ArrayList<JTextField> input2 = new ArrayList<JTextField>();
	ArrayList<JTextField> input3 = new ArrayList<JTextField>();

	//各UIのウィンドウが保持するクラス
	ArrayList<Applet> app = new ArrayList<Applet>();
	ArrayList<config> con = new ArrayList<config>();
	ArrayList<JFrame> jfr = new ArrayList<JFrame>();

	ArrayList<test> tes = new ArrayList<test>();

	String subscribed;

	int id = 0;

	//画面を閉じたウィンドウが保持しているarraylist部をremoveする
	public void windowClosed(WindowEvent e) {
		String str = e.toString();
		int in = Integer.parseInt(str.substring(str.indexOf("frame") + 5));
		app.remove(in);
		con.remove(in);
		jfr.remove(in);
	}

	//MQTTサーバーとの接続が切れたとき再接続する
	@Override
	public void connectionLost(Throwable cause) { // Called when the client lost
		connect.mqttconnect();

		for (int i = 0; i < app.size(); i++) {
			if (app.get(i).pubsub.equals("sub")) {
				try {
					connect.mqttClient.setCallback(this);
					connect.mqttClient.subscribe(app.get(i).thisTopic);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//MQTTサーバーからメッセージを受け取った時、subscribedにメッセージを代入
	//topicが一致するsub系のUIに更新するようにフラグを出す
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		for (int i = 0; i < app.size(); i++) {

			if (app.get(i).thisTopic.equals(topic) && app.get(i).pubsub.equals("sub")) {
				subscribed = new String(message.getPayload());
				app.get(i).flag = true;
			}

		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	//mqttサーバーと接続をする
	Test_Display(Connection connect) {
		this.connect = connect;
		connect.mqttconnect();
	}

	//mqttサーバーとの接続を断つ
	public void disconnect() {
		if (connect.isConnected) {
			connect.mqttdisconnect();
		}
	}

	//テスト用のウィンドウを同時に閉じる
	public void closeall() {
		for (int i = 0; i < jfr.size(); i++) {
			app.remove(i);
			jfr.get(i).dispose();
			jfr.remove(i);
			con.get(i).config.dispose();
			con.remove(i);
			tes.remove(i);
			i--;
		}
	}

	//新たしいテストウィンドウを加える
	//テストは三種類
	//log:subscribeした内容をログとして表示
	//line:subscribeした内容を折れ線グラフで表示
	//varbutton:入力した内容をpublishする
	public void add(String topic, String type) {
		tes.add(new test());
		int temp = tes.size() - 1;
		switch (type) {
		case "log":
			try {
				connect.mqttClient.setCallback(this);
				connect.mqttClient.subscribe(topic);
			} catch (MqttException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			tes.get(temp).log(topic);
			break;
		case "line":
			try {
				connect.mqttClient.setCallback(this);
				connect.mqttClient.subscribe(topic);
			} catch (MqttException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			tes.get(temp).line(topic);
			break;
		case "varbutton":
			tes.get(temp).varbutton(topic);
			break;
		}
		id++;
	}

	//テスト用のUIを表示するswing画面
	public class test extends JFrame implements WindowListener {
		int num;
		JFrame config;

		test() {
			num = id;
		}

		//logのUIの表示をする
		public void log(String topic) {

			jfr.add(new JFrame());
			jfr.get(jfr.size() - 1).addWindowListener(this);
			int temp = jfr.size() - 1;
			app.add(new Applet(jfr.get(temp), "log", topic, "sub"));

			app.get(temp).thisType = "log";
			app.get(temp).thisTopic = topic;
			app.get(temp).pubsub = "sub";
			app.get(temp).flag = false;

			jfr.get(temp).setSize(200, 400);
			jfr.get(temp).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jfr.get(temp).addWindowListener(this);

			jfr.get(temp).setVisible(true);
			jfr.get(temp).setResizable(false);
			con.add(new config("log"));

		}

		//lineのUIを表示する
		public void line(String topic) {

			jfr.add(new JFrame());
			jfr.get(jfr.size() - 1).addWindowListener(this);
			int temp = jfr.size() - 1;
			app.add(new Applet(jfr.get(temp), "line", topic, "sub"));

			app.get(temp).thisType = "line";
			app.get(temp).thisTopic = topic;
			app.get(temp).pubsub = "sub";
			app.get(temp).flag = false;

			jfr.get(temp).setSize(400, 230);
			jfr.get(temp).addWindowListener(this);

			jfr.get(temp).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jfr.get(temp).setVisible(true);
			jfr.get(temp).setResizable(false);
			con.add(new config("line"));

		}

		//varbuttonのUIを表示する
		public void varbutton(String topic) {

			jfr.add(new JFrame());
			jfr.get(jfr.size() - 1).addWindowListener(this);
			int temp = jfr.size() - 1;
			app.add(new Applet(jfr.get(temp), "varbutton", topic, "pub"));

			app.get(temp).thisType = "varbutton";
			app.get(temp).thisTopic = topic;
			app.get(temp).pubsub = "sub";
			app.get(temp).flag = false;

			jfr.get(temp).setSize(200, 200);
			jfr.get(temp).addWindowListener(this);

			jfr.get(temp).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jfr.get(temp).setVisible(true);
			jfr.get(temp).setResizable(false);
			con.add(new config("varbutton"));

		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

		//ウィンドウが閉じたらarraylistの部分をremoveする
		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・

			for (int i = 0; i < con.size(); i++) {
				if (num == tes.get(i).num) {
					app.remove(i);
					jfr.get(i).dispose();

					jfr.remove(i);
					con.get(i).config.dispose();
					con.remove(i);
					tes.remove(i);
					break;
				}
			}

		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO 自動生成されたメソッド・スタブ

		}

	}

	//設定用のウィンドウ、現段階では折れ線グラフのXやYの上限を決められる
	public class config extends JFrame implements WindowListener {
		int num;
		JFrame config;

		config(String type) {
			num = id;
			switch (type) {
			case "line":
				config = new JFrame();
				ImageIcon icon = new ImageIcon("./img/MQTT.png");
				config.setIconImage(icon.getImage());
				config.addWindowListener(this);

				setLayout(new BorderLayout());

				JPanel main = new JPanel();
				main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));

				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel input1_print = new JLabel("The Range of X");
				input1_print.setPreferredSize(new Dimension(100, 15));
				p.add(input1_print);
				main.add(p);

				p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				input1.add(new JTextField("20", 5));
				p.add(input1.get(id));
				main.add(p);

				p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel input2_print = new JLabel("Max of Y");
				input2_print.setPreferredSize(new Dimension(100, 15));
				p.add(input2_print);
				main.add(p);

				p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				input2.add(new JTextField("100", 5));
				p.add(input2.get(id));
				main.add(p);

				p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				JLabel input3_print = new JLabel("Min of Y");
				input3_print.setPreferredSize(new Dimension(100, 15));
				p.add(input3_print);
				main.add(p);

				p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));
				input3.add(new JTextField("0", 5));
				p.add(input3.get(id));
				main.add(p);

				config.add("North", main);

				config.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				config.setVisible(true);
				config.setBounds(400, 0, 200, 200);
				config.setResizable(false);
				break;
			default:

				config = new JFrame();
				input1.add(new JTextField());
				input2.add(new JTextField());
				input3.add(new JTextField());

				break;
			}
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO 自動生成されたメソッド・
		}

		@Override
		public void windowClosing(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

			for (int i = 0; i < con.size(); i++) {
				if (num == tes.get(i).num) {
					app.remove(i);
					jfr.get(i).dispose();
					jfr.remove(i);
					con.get(i).config.dispose();
					con.remove(i);
					tes.remove(i);
					break;
				}
			}

		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO 自動生成されたメソッド・スタブ

		}
	}

	//Swingのフレームの中にProcessingというGUIに特化した画面を埋め込む
	public class Applet extends PApplet {
		JFrame frame;
		ArrayList<String> value = new ArrayList<String>();
		String data;
		String thisType;
		String thisTopic;
		String pubsub;
		ArrayList<String> timeline = new ArrayList<String>();
		String data_log;
		boolean flag;
		int thisid;

		ArrayList<Float> yline = new ArrayList<Float>();

		Applet(JFrame frame, String type, String topic, String pubsub) {
			super();
			thisid = id;
			thisType = type;
			thisTopic = topic;
			this.pubsub = pubsub;
			flag = false;

			// this.frame=frame;
			try {
				java.lang.reflect.Method handleSettingsMethod = this.getClass().getSuperclass()
						.getDeclaredMethod("handleSettings", null);
				handleSettingsMethod.setAccessible(true);
				handleSettingsMethod.invoke(this, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			PSurface surface = super.initSurface();
			Canvas canvas = (Canvas) surface.getNative();
			surface.placeWindow(new int[] { 0, 0 }, new int[] { 0, 0 });
			frame.add(canvas);
			this.startSurface();
		}

		//Processing部のUIの大きさ決定
		public void settings() {
			switch (thisType) {
			case "log":
				size(200, 400);
				break;
			case "line":
				size(400, 200);
				break;
			case "varbutton":
				size(300, 300);
				break;
			}

		}

		//Processing部の背景の設定
		public void setup() {
			switch (thisType) {
			case "log":
				background(255, 255, 255);
				break;
			case "line":
				background(255, 255, 255);
				break;
			case "varbutton":
				background(255, 255, 255);

				background(255);
				fill(255, 0, 9);
				noStroke();
				rect(50, 50, 100, 100);
				fill(255, 255, 255);
				text("publish", 82, 105);
				fill(0, 0, 0);

				text(thisTopic, 50, 160);

				data = "";
				break;
			}

		}

		//Processingの描画部分
		//ウィンドウが表示されているときはdraw関数をループする
		public void draw() {

			if (thisType.equals("log") && flag) {

				Calendar now = Calendar.getInstance();
				int h = now.get(Calendar.HOUR_OF_DAY);// 時を取得
				int m = now.get(Calendar.MINUTE); // 分を取得
				int s = now.get(Calendar.SECOND); // 秒を取得
				fill(0, 0, 0);
				timeline.add(h + ":" + m + ":" + s);
				value.add(subscribed);
				if (timeline.size() == 36) {
					timeline.remove(0);
					value.remove(0);
				}
				background(255, 255, 255);
				for (int i = 0; i < timeline.size(); i++) {
					text(timeline.get(i) + ":" + value.get(i), 0, i * 10 + 10);
				}
				flag = false;

			} else if (thisType.equals("line") && flag) {

				yline.add(Float.parseFloat(subscribed));
				fill(0, 0, 0);
				int xran = Integer.parseInt(input1.get(thisid).getText());
				Float ymax = Float.parseFloat(input2.get(thisid).getText());
				Float ymin = Float.parseFloat(input3.get(thisid).getText());
				background(255, 255, 255);
				if (yline.size() > 2) {
					for (int i = 0; i < yline.size() - 1; i++) {
						line(i * (400 / xran), 200 - (yline.get(i) / (ymax - ymin)) * 200, (i + 1) * (400 / xran),
								200 - (yline.get(i + 1) / (ymax - ymin)) * 200);
					}
				}
				if (yline.size() == xran) {
					yline.remove(0);
				}
				flag = false;

			} else if ((thisType.equals("varbutton"))) {
				fill(0);
				text(data, 10, 10);
				text(thisTopic, 50, 160);
			}

		}

		//マウスをクリックした時
		//varbuttonで真ん中のボタンを押した時に呼び出される
		public void mouseClicked() {
			if (thisType.equals("varbutton")) {
				if (mouseButton == LEFT) {
					if (mouseX > 50 && mouseX < 150 && mouseY > 50 && mouseY < 150) {
						connect.mqttpublish(thisTopic, data);
					}
				}
			}

		}

		//キーを押したとき
		//varbuttonにて文字を入力した時に呼び出される
		public void keyPressed() {
			if (thisType.equals("varbutton")) {
				background(255);
				fill(255, 0, 9);
				noStroke();
				rect(50, 50, 100, 100);
				fill(255, 255, 255);
				text("publish", 82, 105);
				fill(0, 0, 0);
				if (key == '\b') {
					if (data.length() != 0) {
						data = data.substring(0, data.length() - 1);
					}
				} else {
					data = data + key;
				}
			}

		}

	}

}