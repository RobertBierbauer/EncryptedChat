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
	
	public void addUser(User user){
		users.add(user);
	}

	public void removeUser(User user){
		users.remove(user);
	}
	
	public LinkedList<User> getUsers() {
		return users;
	}

	public void setUsers(LinkedList<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
