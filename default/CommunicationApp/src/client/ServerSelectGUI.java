package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ServerSelectGUI extends JFrame implements ActionListener{

	JPanel jp;
	JLabel massage1;
	JLabel massage2;
	JLabel blank;
	JTextField text1;
	JTextField text2;
	JButton connect;

	public ServerSelectGUI() {
		setTitle("サーバーアドレスとポート番号を指定してください。");
		setSize(500, 125);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		jp = new JPanel();
		jp.setLayout(new GridLayout(3, 2, 5, 5));
		massage1 = new JLabel("server address : ");
		massage2 = new JLabel("server port : ");
		blank = new JLabel();
		text1 = new JTextField("localhost");
		text2 = new JTextField("10003");
		connect = new JButton("接続");
		connect.addActionListener(this);
		connect.setContentAreaFilled(false);
		text1.setCaretPosition(text1.getText().length());
		text2.setCaretPosition(text2.getText().length());
		jp.add(massage1);
		jp.add(text1);
		jp.add(massage2);
		jp.add(text2);
		jp.add(blank);
		jp.add(connect);
		add(jp);
		setVisible(true);
	}

	public void disableGUI(){
		connect.setEnabled(false);
		setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String IP = text1.getText();
		int port = Integer.valueOf(text2.getText());
		Socket socket = null;
		try {
			socket = new Socket(IP, port);
			Start.serverSelectedStep(socket);
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "接続に失敗しました。");
		}
	}
}
