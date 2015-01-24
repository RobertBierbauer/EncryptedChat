package Server;
import java.util.LinkedList;


public class Chatroom {
	private LinkedList<User> users;
	private String name;
	private String password;
	private String language;
	
	public Chatroom(String name, String password, String language) {
		super();
		this.name = name;
		this.password = password;
		this.language = language;
		
		users = new LinkedList<User>();
	}
	
	//adds users
	public void addUser(User user){
		users.add(user);
	}

	//removes users from the chatroom
	public void removeUser(User user){
		users.remove(user);
	}
	
	//returns all users within the chatroom
	public LinkedList<User> getUsers() {
		return users;
	}

	public void setUsers(LinkedList<User> users) {
		this.users = users;
	}

	//returns the name of the chatroom
	public String getName() {
		return name;
	}

	//sets the name of the chatroom
	public void setName(String name) {
		this.name = name;
	}

	//gets the password of the chatroom
	public String getPassword() {
		return password;
	}

	//sets the password of the chatroom
	public void setPassword(String password) {
		this.password = password;
	}

	//gets the language of the chatroom
	public String getLanguage() {
		return language;
	}

	//sets the language of the chatroom
	public void setLanguage(String language) {
		this.language = language;
	}
}
