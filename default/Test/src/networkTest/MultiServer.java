package networkTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class MultiServer {
	
	public static final int PORT = 10003;
	public static LinkedList<ServerThread> serverThreads = new LinkedList<ServerThread>();
	
	public static void main(String args[]){
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(PORT);
			while(true){
				Socket socket = serverSocket.accept();
				ServerThread st = new ServerThread(socket);
				serverThreads.add(st);
				st.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

class ServerThread extends Thread{
	
	public Socket socket;
	public BufferedReader in;
	public PrintWriter out;
	
	public ServerThread(Socket socket){
		this.socket = socket;
		if(socket == null){
			System.out.println("socket is null");
			return;
		}
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		String line;
		try {
			while((line = in.readLine()) != null){
				System.out.println("受信:" + line);
				out.println(line);
				System.out.println("送信:" + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}









