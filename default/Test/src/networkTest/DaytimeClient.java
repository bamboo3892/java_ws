package networkTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class DaytimeClient {
	
	public static final int ECHO_PORT = 13;

	public static void main(String args[]){
		Socket socket = null;
		//System.out.println(args[0]);
		try{
			socket = new Socket(args[0], ECHO_PORT);
			System.out.println("接続しました "+socket.getRemoteSocketAddress());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = in.readLine();
			while((line = in.readLine()) != null){
				if(line != null) {
					System.out.println(line);
				}
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(socket != null) socket.close();
			}catch(IOException e){}
			System.out.println("切断されました" + socket.getRemoteSocketAddress());
		}
	}

}
