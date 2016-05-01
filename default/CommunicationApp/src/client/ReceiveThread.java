package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiveThread extends Thread{

	private Socket socket = null;
	private BufferedReader in;
	private ChatRoomSelectGUI gui = null;

	public ReceiveThread(Socket pSocket, ChatRoomSelectGUI gui) throws IOException{
		if(pSocket == null || gui == null) throw new IllegalArgumentException();
		this.socket = pSocket;
		this.gui = gui;
		this.in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
		if(in == null) throw new NullPointerException();
	}

	@Override
	public void run(){
		if(in == null) System.out.println("222222222222222222222222222");
		while (true) {
			String line = null;
			try {
				line = this.in.readLine();
			} catch (IOException e) {
				System.out.println("通信が切断されました。");
				return;
			}
			if (line != null) {
				System.out.println("受信 : " + line);
				gui.doCommand(line);
			}
		}
	}

}





