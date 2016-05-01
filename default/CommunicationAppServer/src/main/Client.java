package main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Client {

	private String IP;
	private ServeThread serve;
	private ReceiveThread receive;
	private HashMap<String, ChatRoom> joiningChatRoom = new HashMap<String, ChatRoom>();

	public Client(Socket socket) throws IOException{
		if(socket == null) throw new IllegalArgumentException();
		this.IP = socket.getInetAddress().getHostAddress();
		receive = new ReceiveThread(socket);
		receive.setClient(this);
		receive.start();
	}

	/**
	 * only invoked by ReceiveThread
	 */
	public synchronized boolean doCommand(String command){
		String str[] = command.split(",");
		if(str[0].equals("connect")){//connect :
			if(Start.linkClientWithChatRoom(this, str[1])){
				System.out.println("チャットルームに参加できました");
				serve.sendCommand("return,connect," + str[1] + ",success");
				return true;
			}else{
				serve.sendCommand("return,connect," + str[1] + ",success");
				return false;
			}
		}else if(str[0].equals("say")){//say : send the chatroom the command
			if(joiningChatRoom.containsKey(str[1])) {
				ChatRoom chatRoom = joiningChatRoom.get(str[1]);
				chatRoom.spreadMessage(IP, str[2]);
				return true;
			}else{
				System.out.println("参加していないチャットルームです");
				return false;
			}
		}else if(str[0].equals("disconnect")){//disconnect :
			if(joiningChatRoom.containsKey(str[1])) {
				ChatRoom chatRoom = joiningChatRoom.get(str[1]);
				if(chatRoom == null) return false;
				chatRoom.removeClient(IP);
				return true;
			}else{
				System.out.println("すでに参加していないチャットルームです");
				return false;
			}
		}
		System.out.println("認識できないコマンドです");
		return false;
	}

	public synchronized void sendCommand(String command){
		serve.sendCommand(command);
	}

	public synchronized boolean addChatRoom(String path, ChatRoom chatRoom){
		if(joiningChatRoom.containsKey(path)){
			System.out.println("すでに参加しているチャットルームです");
			return false;
		}
		joiningChatRoom.put(path, chatRoom);
		return true;
	}

	public synchronized String getIPString(){
		return IP;
	}

	public void setServeThread(ServeThread serve){
		this.serve = serve;
	}

	public boolean hasServeThread(){
		return !(serve == null);
	}

	@Override
	public void finalize() throws Throwable{
		serve.finalize();
		receive.finalize();
		super.finalize();
	}

}
