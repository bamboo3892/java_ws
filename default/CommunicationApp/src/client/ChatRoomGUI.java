package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ChatRoomGUI extends JFrame implements ActionListener{

	private ChatRoomSelectGUI gui;
	private String roomPath;
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea textArea;
	private JButton button;

	ChatRoomGUI(String roomPath, ChatRoomSelectGUI gui){
		this.roomPath = roomPath;
		this.gui = gui;
		setTitle("チャットルーム");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		textArea.setEnabled(false);
		scrollPane.setViewportView(textArea);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		textField.addActionListener(this);

		button = new JButton("送信");
		button.addActionListener(this);
		panel.add(button, BorderLayout.EAST);

		setVisible(true);
	}

	public void addMessage(String speaker, String message){
		textArea.append(speaker + " < " + message + "\n");
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String message = textField.getText();
		message.replaceAll(",", "");
		gui.sendCommand("say," + roomPath + "," + message);
		textField.setText("");
	}

}
