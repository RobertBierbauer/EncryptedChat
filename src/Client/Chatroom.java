package Client;
import java.util.LinkedList;


public class Chatroom {
	private String name;
	private String password;
	private String language;
	private int members;
	
	public Chatroom(String name, String password, String language, int members) {
		super();
		this.name = name;
		this.password = password.equals("null") ? null : password;
		this.language = language.equals("null") ? null : language;
		this.members = members;
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

	public int getMembers() {
		return members;
	}

	public void setMembers(int members) {
		this.members = members;
	}
	
	public void incrementMembers(){
		this.members++;
	}		
}
