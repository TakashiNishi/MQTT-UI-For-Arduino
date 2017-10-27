package mqtt_ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class mqtt_ui extends JFrame implements ActionListener {

	// state数
	static int num_of_state = 1;

	// UIのボード設定
	JComboBox<String> mqtt_frame = new JComboBox<String>();// Arduinoのボード選択

	// stateの設定
	ArrayList<stateMain> state = new ArrayList<stateMain>();//MSの生成
	ArrayList<JLabel> mainLabel= new ArrayList<JLabel>();//MSの名前
	ArrayList<JButton> mainConfig = new ArrayList<JButton>();//MSの設定をするボタン
	ArrayList<JButton> mainAdd = new ArrayList<JButton>();//MSを追加するボタン
	ArrayList<JButton> mainDelete = new ArrayList<JButton>();//MSを削除するボタン

	// UI自体が持つstatic変数
	static int select;//MSを複数設定するときに使う。何番目のMSか

	static mqtt_ui frame;//UIに関するクラス

	public static void main(String[] args) {
		frame = new mqtt_ui("MQTT UI For Arduino");//UIを呼び出す
		frame.setVisible(true);//UIを可視化
		frame.setResizable(false);//UIのサイズ変更不可
	}

	// メインUIを描写する関数
	mqtt_ui(String title) {

		//UIの初期設定
		setBounds(30, 30, 700, num_of_state * 50 + 300);//UIのサイズ指定
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
		JButton button_stateWait = new JButton("stateWait");
		JButton button_statePrepare = new JButton("statePrepare");
		JButton button_stateClose = new JButton("stateClose");
		JButton button_send = new JButton("Send");
		JButton button_code = new JButton("Code");
		button_stateWait.addActionListener(this);
		button_statePrepare.addActionListener(this);
		button_stateClose.addActionListener(this);
		button_send.addActionListener(this);
		button_code.addActionListener(this);

		mqtt_frame.addActionListener(this);


		JButton button_wait = new JButton("Wait");
		JButton button_prepare = new JButton("Prepare");
		JButton button_close = new JButton("Close");
		button_wait.addActionListener(this);
		button_prepare.addActionListener(this);
		button_close.addActionListener(this);

		//stateMainの初期設定
		for (int i=0;i<num_of_state;i++){
			state.add(new stateMain(i));
			mainLabel.add(new JLabel(state.get(i).name));
			mainConfig.add(new JButton("Config"));
			mainAdd.add(new JButton("Add"));
			mainDelete.add(new JButton("Delete"));
			mainConfig.get(i).setActionCommand("Config" + i);
			mainAdd.get(i).setActionCommand("Add" + i);
			mainDelete.get(i).setActionCommand("Delete" + i);
			mainConfig.get(i).addActionListener(this);
			mainAdd.get(i).addActionListener(this);
			mainDelete.get(i).addActionListener(this);

		}


		//以下、UIの描写に関する内容。一行空きはUIにおける改行部分
		setLayout(new BorderLayout());

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(mqtt_frame);
		main.add(p);


		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel pubsub_print = new JLabel("Wait");
		pubsub_print.setPreferredSize(new Dimension(145, 20));
		JLabel Topic_print = new JLabel("Topic");
		Topic_print.setPreferredSize(new Dimension(135, 20));
		p.add(pubsub_print);
		p.add(Topic_print);
		main.add(p);

		for (int i = 0; i < num_of_state; i++) {
			p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(mainLabel.get(i));
			p.add(mainConfig.get(i));
			p.add(mainAdd.get(i));
			p.add(mainDelete.get(i));
			main.add(p);
		}

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(button_send);
		p.add(button_code);
		main.add(p);

		add("North", main);

	}

	// ボタンを押したときに呼ばれる関数
	public void actionPerformed(ActionEvent e) {
		String cmdName = e.getActionCommand();//コマンド名の取得

		if(cmdName==""){

		}

	}

}