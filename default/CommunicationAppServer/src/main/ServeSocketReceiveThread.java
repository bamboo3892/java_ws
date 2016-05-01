package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeSocketReceiveThread extends Thread{

	ServerSocket serverSocket;

	public ServeSocketReceiveThread()throws IOException{
		serverSocket = new ServerSocket(Start.SERVE_PORT);
	}

	@Override
	public void run(){
		int error = 0;
		while(true){
			try{
				Socket socket = serverSocket.accept();
				System.out.println("server socketにアクセスされました");
				ServeThread serveThread = new ServeThread(socket);
				if(Start.linkClientWithServeThread(serveThread)){
					serveThread.start();
				}else{
					System.out.println("アクセス拒否しました。");
				}
				error = 0;
			}catch(IOException e){
				if(error == 0) e.printStackTrace();
				error++;
				if(error >= 5) {
					System.out.println("stopping ServeSocketReceiveThread");
					break;
				}
			}
		}
	}

}
