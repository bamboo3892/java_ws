package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatRoomSelectGUI extends JFrame implements ActionListener, ListSelectionListener, MouseListener{

	private Socket socket;
	private PrintWriter out = null;
	private JPanel contentPane;
	private JTextField textField;
	private DefaultListModel<String> listModel;
	private JList<String> list;
	private int lastClickedIndex = -1;
	private JButton button;
	private HashMap<String, ChatRoomGUI> chatRoomMap = new HashMap<String, ChatRoomGUI>();

	public ChatRoomSelectGUI(Socket pSocket) throws IOException{
		if(pSocket == null) throw new IllegalArgumentException();
		socket = pSocket;
		out = new PrintWriter(socket.getOutputStream(), true);
		setTitle("チャットルーム");
		setBounds(30, 30, 300, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//////////////////////////////////////////////////////////////////////////////////

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.addMouseListener(this);
		list.addListSelectionListener(this);
		scrollPane.setViewportView(list);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		textField.setText("Please type chatroom path");
		textField.addActionListener(this);
		textField.setActionCommand("enter");
		panel.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);

		button = new JButton("作成");
		button.addActionListener(this);
		button.setActionCommand("button");
		panel.add(button, BorderLayout.EAST);

		setVisible(true);

	}

	public void sendCommand(String command){
		out.println(command);
		System.out.println("送信" + command);
	}

	//コマンドを受け取った時に呼び出される。
	public void doCommand(String command) {
		String str[] = command.split(",");
		try {
			if(str[0].equals( "return")){
				if(str[1].equals( "connect")){
					if(str[3].equals("success")){
						openGUI(str[2]);
						System.out.println("success to enter the chatroom : " + str[2]);
					}else{
						JOptionPane.showMessageDialog(null, "failed to enter the chatroom : " + str[2]);
					}
				}else if(str[1].equals( "create")){
					if(str[3].equals("success")){
						;
						;
					}else{
						JOptionPane.showMessageDialog(null, "failed to create the chatroom : " + str[2]);
					}
				}
			}else if(str[0].equals( "say")){
				String roomPath = str[1];
				String speaker = str[2];
				String message = str[3];
				ChatRoomGUI chatRoom = chatRoomMap.get(roomPath);
				chatRoom.addMessage(speaker, message);
			}
		} catch (Exception e) {
			System.out.println("try to excute invalid command : " + command);
			//e.printStackTrace();
		}
	}

	private void openGUI(String roomPath) throws IOException{
		if(!listModel.contains(roomPath)) throw new IllegalArgumentException();
		ChatRoomSelectGUI gui = this;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ChatRoomGUI chatRoom = new ChatRoomGUI(roomPath, gui);
				chatRoomMap.put(roomPath, chatRoom);
			}
		});
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (list.getSelectedIndices().length == 0){
			button.setText("作成");
		}else if(list.getSelectedIndices().length == 1){
			textField.setText(list.getSelectedValue());
		}else{
			list.setSelectedIndex(e.getLastIndex());
			button.setText("変更");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("enter")){

		}else if(e.getActionCommand().equals("button")){
			if(button.getText().equals("作成")){
				listModel.addElement(textField.getText());
				button.setText("変更");
			}else if(button.getText().equals("変更")){
				if (list.getSelectedIndices().length != 1) return;
				listModel.set(list.getSelectedIndices()[0], textField.getText());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == 3){
			list.clearSelection();
			lastClickedIndex = -1;
			return;
		}
		int index = list.locationToIndex(e.getPoint());
        if(list.getSelectedIndex() == index){
        	if(lastClickedIndex == index){
        		try {
        			openGUI(listModel.getElementAt(index));
        		} catch (IOException e1) {
        			e1.printStackTrace();
        		}
        	}else{
        		lastClickedIndex = index;
        	}
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}



}









