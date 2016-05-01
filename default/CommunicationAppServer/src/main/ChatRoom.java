package main;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

	private String path;
	private HashMap<String, Client> clientMap = new HashMap<String, Client>();

	public ChatRoom(String path){
		this.path = path;
	}

	public boolean addClient(Client client){
		if(clientMap.containsKey(client.getIPString())){
			System.out.println("すでに参加しているクライアントです");
		}
		clientMap.put(client.getIPString(), client);
		return true;
	}

	public void spreadMessage(String speaker, String message){
		System.out.println("attempt to spread message : " + speaker + " < " + message);
		for(Map.Entry<String, Client> entry : clientMap.entrySet()) {
			System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		    entry.getValue().sendCommand("say," + this.path + "," + speaker + "," + message);
		}
	}

	public void removeClient(String IP){
		clientMap.remove(IP);
	}

	public String getPath(){
		return path;
	}

}
