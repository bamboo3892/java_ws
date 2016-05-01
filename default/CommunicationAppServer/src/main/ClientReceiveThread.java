package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientReceiveThread extends Thread{

	private ServerSocket serverSocket;

	public ClientReceiveThread() throws IOException{
		serverSocket = new ServerSocket(Start.RECEIVE_PORT);
	}

	@Override
	public void run(){
		int error = 0;
		while(true){
			try{
				Socket socket = serverSocket.accept();
				System.out.println("クライアントからのアクセス:" + socket.getInetAddress().getHostAddress());
				Start.addClientFromSocket(socket);
				error = 0;
			}catch(IOException e){
				if(error == 0) e.printStackTrace();
				error++;
				if(error >= 5) {
					System.out.println("ClientReceiveThreadが停止");
					break;
				}
			}
		}
	}

}
