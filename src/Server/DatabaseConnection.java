package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DatabaseConnection {

	Connection c = null;
	Statement stmt = null;
	
	public DatabaseConnection(){}

	//open the database
	public void openDatabase(){
		try {
			c = DriverManager.getConnection("jdbc:sqlite:Chat.db");
			stmt = c.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//close the database
	public void closeDatabase(){
		try {
			stmt.close();
			c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	//creates the table for the users if it doesn't exist already
	public void createTable(){
		String makeTable = "CREATE TABLE IF NOT EXISTS user (ID INTEGER PRIMARY key AUTOINCREMENT NOT NULL, " +
				"NAME text NOT NULL, " +
				"PASSWORD text NOT NULL)";
		try {
			stmt.executeUpdate(makeTable);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//returns all users from the database
	public HashMap<String, String> getUsers(){
		HashMap<String, String> users = new HashMap<String, String>();
		ResultSet rs;
		try {
			rs = stmt.executeQuery( "SELECT * FROM user;" );
			while ( rs.next() ) {
				String  name = rs.getString("NAME");
				String password  = rs.getString("PASSWORD");
				users.put(name, password);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	//add a user to the database
	public void insertUser(String name, String password){
		String createUser = "INSERT INTO user (NAME,PASSWORD) " +
                "VALUES ('" + name + "', '" + password + "');";
		try {
			stmt.executeUpdate(createUser);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
