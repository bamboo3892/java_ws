package client;

import java.io.IOException;
import java.net.Socket;

public class Start {

	public static ServerSelectGUI gui1;
	public static ChatRoomSelectGUI gui2;
	public static ReceiveThread receive;
	public static Socket serveSocket;
	public static Socket receiveSocket;

	public static void main(String args[]){
		serverSelectStep();
	}

	public static void serverSelectStep(){
		Start.gui1 = new ServerSelectGUI();
	}

	public static void serverSelectedStep(Socket socket) throws IOException{
		if(socket == null) throw new IllegalArgumentException();
		Start.serveSocket = socket;
		Start.gui1.disableGUI();
		Start.gui2 = new ChatRoomSelectGUI(serveSocket);
		receiveSocket = new Socket(socket.getInetAddress(), socket.getPort() + 1);
		receive = new ReceiveThread(receiveSocket, gui2);
		receive.start();
	}

}




