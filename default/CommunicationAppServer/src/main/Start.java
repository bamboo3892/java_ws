package main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Start {

	public static final int RECEIVE_PORT = 10003;
	public static final int SERVE_PORT = RECEIVE_PORT + 1;
	public static ServeSocketReceiveThread rsrt;
	public static ClientReceiveThread crt;
	public static HashMap<String, Client> clients = new HashMap<String, Client>();
	public static HashMap<String, ChatRoom> chatRooms = new HashMap<String, ChatRoom>();

	public static void main(String args[]) {
		try {
			rsrt = new ServeSocketReceiveThread();
			rsrt.start();
			System.out.println("start ReceiveSocketReceiveThread");

			crt = new ClientReceiveThread();
			crt.start();
			System.out.println("start ClientReceiveThread");
		} catch (IOException e) {
			System.out.println("failed to open server\n");
			e.printStackTrace();
		}

		addChatRoom(new ChatRoom("aaa"));
	}

	/**
	 * only invoked by ClientReceiveThread
	 */
	public static void addClientFromSocket(Socket socket){
		if(clients.containsKey(socket.getInetAddress().getHostAddress())) {
			System.out.println("have already access to the client");
			return;
		}
		Client client;
		try {
			client = new Client(socket);
		} catch (IOException e) {
			System.out.println("invalid socket");
			return;
		}
		clients.put(client.getIPString(), client);
		System.out.println("success to register the client");
	}

	public synchronized static boolean linkClientWithServeThread(ServeThread pServeThread){
		String IP = pServeThread.getIP().getHostAddress();
		if(!clients.containsKey(IP)){
			System.out.println("そのクライアントは存在しません");
			return false;
		}
		Client client = clients.get(IP);
		if(client.hasServeThread()){
			System.out.println("すでにServeThreadを持っています");
			return false;
		}
		client.setServeThread(pServeThread);
		pServeThread.setClient(client);
		System.out.println("successfully link Client with ServeThread");
		return true;
	}

	public synchronized static boolean linkClientWithChatRoom(Client client, String path){
		if(!chatRooms.containsKey(path)){
			System.out.println("そのパスのチャットルームは存在しません。");
			return false;
		}
		ChatRoom chatRoom = chatRooms.get(path);
		if(chatRoom == null) return false;
		if(chatRoom.addClient(client)){
			client.addChatRoom(path, chatRoom);
			return true;
		}else{
			System.out.println("チャットルームからブロックされました。");
			return false;
		}
	}

	public synchronized static boolean addChatRoom(ChatRoom chatRoom){
		String path = chatRoom.getPath();
		if(chatRooms.containsKey(path)){
			System.out.println("すでにそのパスは登録されています");
			return false;
		}
		chatRooms.put(path, chatRoom);
		return true;
	}

	public static void deleteClient(String path){
		Client client = clients.get(path);
		if(client == null) {
			System.out.println("存在しないクライアントです。");
			return;
		}
		try {
			client.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		clients.remove(path);
		System.out.println("delete the client : " + path);
	}

	public static void closeServer(){

	}

}







