package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ServeThread extends Thread{

	private Socket socket;
	private Client client;
	private PrintWriter out;

	public ServeThread(Socket socket) throws IOException{
		if(socket == null) throw new IllegalArgumentException();
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	@Override
	public void run() {}

	public void sendCommand(String command){
		System.out.println("送信 : " + command);
		out.println(command);
	}

	public InetAddress getIP(){
		if(socket == null) {
			System.out.println("The socket is null");
			return null;
		}
		return socket.getInetAddress();
	}

	public void setClient(Client client){
		this.client = client;
	}

	@Override
	public void finalize() throws Throwable{
		socket.close();
		out.close();
		super.finalize();
	}

}
