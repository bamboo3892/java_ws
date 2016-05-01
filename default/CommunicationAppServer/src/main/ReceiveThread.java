package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiveThread extends Thread{

	private Socket socket = null;
	private Client client = null;
	private BufferedReader in = null;

	public ReceiveThread(Socket socket)throws IOException{
		if(socket == null) throw new IllegalArgumentException();
		this.socket = socket;
		in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	@Override
	public void run(){
		while (true) {
			String command = null;
			try {
				command = in.readLine();
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("break the connection : ReceiveThread");
				Start.deleteClient(client.getIPString());
				return;
			}
			if (command != null) {
				System.out.println("受信 : " + command);
				client.doCommand(command);
			}
		}
	}

	public void setClient(Client client){
		this.client = client;
	}

	@Override
	public void finalize() throws Throwable{
		socket.close();
		in.close();
		super.finalize();
	}

}
