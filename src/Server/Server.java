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

	//set all users into a hashmap
	public void setUsers(HashMap<String, String> users){
		this.users = users;
	}
	
	//adds a user to the hashmap if it does not exist already
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

	//checks if a username and password is correct
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

	//adds a chatroom if it does not exist already
	public Chatroom addChatroom(String chatroomName, String chatroomPassword, String chatroomLanguage){
		if(isChatroom(chatroomName) != null){
			return null;
		}
		Chatroom chatroom = new Chatroom(chatroomName, chatroomPassword, chatroomLanguage);
		chatrooms.add(chatroom);
		return chatroom;
	}

	//checks if a chatroom exists
	public Chatroom isChatroom(String chatroomName){
		for(Chatroom room : chatrooms){
			if(room.getName().equals(chatroomName)){
				return room;
			}
		}
		return null;
	}	

	//add a user to a list if he connects to the server
	public User userConnected(Socket socket, String username){
		User user = new User(socket, username);
		currentUsers.add(user);
		return user;
	}

	//removes a user from a list if he disconnected
	public void userDisconnected(User user){
		currentUsers.remove(user);
	}

	//adds a user to a chatroom if he joins
	public void joinChatroom(User user, Chatroom chatroom){
		chatroom.addUser(user);
	}

	//removes a user from the chatroom if he leaves
	public void leftChatroom(User user, Chatroom chatroom){
		chatroom.removeUser(user);
		if(chatroom.getUsers().size() == 0){
			chatrooms.remove(chatroom);
		}
	}

	//returns the list of all users currently connected
	public LinkedList<User> getCurrentUsers() {
		return currentUsers;
	}

	//returns a user by its name
	public User getUserByName(String username){
		for(User u : currentUsers){
			if(u.getName().equals(username)){
				return u;
			}
		}
		return null;
	}

	//returns a list of chatrooms
	public LinkedList<Chatroom> getChatrooms() {
		return chatrooms;
	}

	public static void main (String args[]) throws IOException {
		//creates or opens the database
		DatabaseConnection dbc = new DatabaseConnection();
		dbc.openDatabase();
		dbc.createTable();

		//starts the server
		Server server = new Server (8080, dbc);
	}
}