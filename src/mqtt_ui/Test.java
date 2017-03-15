package mqtt_ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Test extends JFrame implements ActionListener,WindowListener {
	static Test frame;
	JTextField topic = new JTextField();
	JComboBox<String> Test_Combo = new JComboBox<String>();
	Test_Display test;

	@Override
	public void actionPerformed(ActionEvent e) {
		String selected = (String) Test_Combo.getSelectedItem();
		String cmdName = e.getActionCommand();


		switch(cmdName){
		case "close":
			Component c = (Component) e.getSource();
			Window w = SwingUtilities.getWindowAncestor(c);
			w.dispose();
			break;
		case "ok":
			test.add(topic.getText(),selected);
			break;
		}


	}




	Test(Connection connect) {


		test = new Test_Display(connect);
		addWindowListener(this);

		// 取得した一覧を表示する
		Test_Combo.addItem("");
		Test_Combo.addItem("log");
		Test_Combo.addItem("line");
		Test_Combo.addItem("varbutton");

		// insファイルからPD1属性ファイルを取得する

		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());


		Test_Combo.setPreferredSize(new Dimension(100, 20));

		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel Topic_print = new JLabel("Topic");
		topic.setPreferredSize(new Dimension(100, 20));
		Topic_print.setPreferredSize(new Dimension(100, 20));
		p.add(Topic_print);
		p.add(topic);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel Test_print = new JLabel("Test");
		Test_print.setPreferredSize(new Dimension(100, 20));
		p.add(Test_print);
		p.add(Test_Combo);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));

		JButton ok = new JButton("ok");
		ok.addActionListener(this);

		p.add(ok);
		main.add(p);


		add("North", main);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setBounds(40, 40, 250, 200);
		setResizable(false);
	}




	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}




	@Override
	public void windowClosed(WindowEvent arg0) {
		test.closeall();
		if(test!=null){
			test.disconnect();
			test.dispose();
			test=null;
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
