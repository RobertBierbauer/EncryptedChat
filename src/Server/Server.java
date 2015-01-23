package Server;

import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;



public class Server {
	HashMap<String, String> users;
	LinkedList<User> currentUsers;
	LinkedList<Chatroom> chatrooms;
	DatabaseConnection dbc;

	public Server (int port, DatabaseConnection dbc) throws IOException {
		ServerSocket server = new ServerSocket (port);
		this.dbc = dbc;
		users = new HashMap<String, String>();
		setUsers(dbc.getUsers());
		currentUsers = new LinkedList<User>();
		chatrooms = new LinkedList<Chatroom>();
		while (true) {
			Socket client = server.accept();
			System.out.println ("Accepted from " + client.getInetAddress ());

			ServerCommunicationHandler c = new ServerCommunicationHandler (this, client);
			c.start ();
		}
	}

	public void setUsers(HashMap<String, String> users){
		this.users = users;
	}
	
	public boolean addUser(String username, String password){
		if(users.containsKey(username)){
			return false;
		}
		else{
			users.put(username, password);
			dbc.insertUser(username, password);
			return true;
		}
	}

	public boolean isUser(String username, String password){
		if(users.containsKey(username)){
			if(users.get(username).equals(password)){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public Chatroom addChatroom(String chatroomName, String chatroomPassword, String chatroomLanguage){
		if(isChatroom(chatroomName) != null){
			return null;
		}
		Chatroom chatroom = new Chatroom(chatroomName, chatroomPassword, chatroomLanguage);
		chatrooms.add(chatroom);
		return chatroom;
	}

	public Chatroom isChatroom(String chatroomName){
		for(Chatroom room : chatrooms){
			if(room.getName().equals(chatroomName)){
				return room;
			}
		}
		return null;
	}	

	public User userConnected(Socket socket, String username){
		User user = new User(socket, username);
		currentUsers.add(user);
		return user;
	}

	public void userDisconnected(User user){
		currentUsers.remove(user);
	}

	public void joinChatroom(User user, Chatroom chatroom){
		chatroom.addUser(user);
	}

	public void leftChatroom(User user, Chatroom chatroom){
		chatroom.removeUser(user);
		if(chatroom.getUsers().size() == 0){
			chatrooms.remove(chatroom);
		}
	}

	public LinkedList<User> getCurrentUsers() {
		return currentUsers;
	}

	public User getUserByName(String username){
		for(User u : currentUsers){
			if(u.getName().equals(username)){
				return u;
			}
		}
		return null;
	}

	public LinkedList<Chatroom> getChatrooms() {
		return chatrooms;
	}

	public static void main (String args[]) throws IOException {
		DatabaseConnection dbc = new DatabaseConnection();
		dbc.openDatabase();
		dbc.createTable();

		Server server = new Server (8080, dbc);
	}
}