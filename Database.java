import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Database {
	
	public static Connection c = null;
	public static Statement stmt = null;
	
	private int lastID = 0;
	private String currentRoomName = " ";
	private String userNameStatus;
	
	
		public static void createTable() {
			try {
				stmt = c.createStatement();
				String sql = "CREATE TABLE status " + 
				"(USERNAME TEXT NOT NULL," +
				" STATUS TEXT NOT NULL);";
				stmt.executeUpdate(sql);
				stmt.close();
				c.commit();
				c.close();
				System.out.println("Table has been created.");
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
		}
		
		public void newUserStatus(String userName) {
			userNameStatus = userName;
			
			try {
				stmt = c.createStatement();
				String sql = "INSERT INTO STATUS(username,status) VALUES('" + userNameStatus + "','temp'); ";
				stmt.executeUpdate(sql);
				//stmt.close();
				c.commit();
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			userStatusOnline(userNameStatus);
		}
		
		public void userStatusOffline(String userName) {
			userNameStatus = userName;
			
			try {
				stmt = c.createStatement();
				String sql = "UPDATE status set status = '" + "offline" + "' where username = '" + userNameStatus + "';";
				stmt.executeUpdate(sql);
				c.commit();
			
				//stmt.close();
				//c.close();
				
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
			homeScreen();
			
		}
		
		public void userStatusOnline(String userName) {
			userNameStatus = userName;
			
			try {
				stmt = c.createStatement();
				String sql = "UPDATE status set status = '" + "online" + "' where username = '" + userNameStatus + "';";
				stmt.executeUpdate(sql);
				c.commit();
			
				//stmt.close();
				//c.close();
				
				mainMenu();
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
			
		}
		
		
		
	
		public void login() {
			Scanner scan = new Scanner(System.in);
			System.out.println("Please enter a userName: ");
			String userName = scan.next();
			System.out.println("Please enter a passWord");
			String passWord = scan.next();
			
			boolean login = false;
			
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("select * from users;");
				while (rs.next()) {
					String result = rs.getString("username");
					String result2 = rs.getString("password");
					if (result.equals(userName) && result2.equals(passWord)) {
						login = true;
						break;
					}
				}
			if (login == true) {
				userNameStatus = userName;
				userStatusOnline(userName);				
			}
			else {
				System.out.println("Login unsuccesful, please retry or register for an account");
				System.out.println("Press to 1 retry, 2 to make a new account and 3 to quit");
				int response = scan.nextInt();
				if (response == 1) {
					login();
					}
				else if (response == 2){
					newUser();
					}
				else {
					System.out.println("Goodbye");
					//login = false;
				}
				
			}
			rs.close();
			stmt.close();
			c.close();
				
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			scan.close();
	
		}

	
		public void newUser() {
			Scanner scan = new Scanner(System.in);
			String userName;
			String passWord;
			
			System.out.println("Please enter a userName: ");
			userName = scan.next();
			boolean userAccount = checkUserName(userName);
			
			if (userAccount == false) {
				System.out.println("Username already taken please try again: ");
				newUser();
			}
			
			System.out.println("Please enter a passWord");
			passWord = scan.next();
			boolean userPassword = checkPassword(passWord);
			
			if (userPassword == false) {
				System.out.println("Password already taken please try again: ");
				newUser();
			}		
			
			try {
				c.setAutoCommit(false);
				stmt = c.createStatement();
				String sql = "INSERT INTO users("
						+ "username,password)" +
						"VALUES('" + userName + "','" + passWord + "');";
				stmt.executeLargeUpdate(sql);
				stmt.close();
				c.commit();
				//c.close();
				System.out.println("Succesfully created account!");
				newUserStatus(userName);
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			scan.close();
			}
		}
		
		public void createRoom(String roomName) {
			try {
				stmt = c.createStatement();
				String sql = "CREATE TABLE " + roomName + 
				"(ID SERIAL PRIMARY KEY," + 
				" CREATEDDATE TEXT NOT NULL," +	
				" USERNAME TEXT NOT NULL," +
				" MESSAGE CHAR(50));";
				stmt.executeUpdate(sql);
				stmt.close();
				//c.close();
				//c.commit();
				System.out.println("Succesfully created room!");
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
		}
		
		
		public void getLatestMessages(String roomName) {
			int startingID = lastID;
			
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("select * from " + roomName + " where id > " + lastID + ";");
				
				while (rs.next()) {
					String user = rs.getString("username");
					String message = rs.getString("message");
					int id = rs.getInt("id");
					lastID = id;
					
					if (startingID > 0) {
						System.out.println(user + ": " + message);
						System.out.println();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
		}
		
		class GetLatestMessages extends TimerTask {
			
			public void run() {
				getLatestMessages(currentRoomName);
			}
		}
		
		
		
		public boolean checkUserName(String userName) {
			
			boolean output = true;
			 
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("select * from users;");
				while (rs.next()) {
					String result = rs.getString("username");
					if (result.equals(userName)) {
						output = false;
						return output;
					}
					
				}
				c.commit();
				//rs.close();
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
			return output;
		}
		
		
		public boolean checkPassword(String passWord) {
					
			boolean output = true;
					 
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("select * from users;");
				while (rs.next()) {
				String result = rs.getString("password");
					if (result.equals(passWord)) {
						output = false;
						return output;
					}
							
				 }
				stmt.close();
				c.commit();
				rs.close();
				}catch(Exception e) {
						e.printStackTrace();
						System.err.println(e.getClass().getName()+": "+e.getMessage());
						System.exit(0);
					}
					
					return output;
				}		
		
		
		public void joinRoom(String roomName, String userName) {
			
			
			Scanner scan = new Scanner(System.in);
			System.out.println("Welcome the chatroom");
			System.out.println("--------------------");
			System.out.println("Press /help for help");
			
			currentRoomName = roomName;
			Timer timer = new Timer();
			timer.schedule(new Database.GetLatestMessages(), 0, 3000);
			
			while (true)
			{
				String input = scan.nextLine();
				
			
					
				try {
					stmt = c.createStatement();
					String sql = "INSERT INTO " + roomName
							+ "(USERNAME,MESSAGE,createddate)"
							+ "VALUES('" + userName + "','" + input + "',current_timestamp);";
					stmt.executeUpdate(sql);
					stmt = c.createStatement();
					getLatestMessages(roomName);
					stmt.close();
					c.commit();
				}catch(Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					System.exit(0);
				}
				
				
				if (input.equals("/help")) {
					System.out.println("Type /help for help");
					System.out.println("Type /list to see all active users in the chat room");
					System.out.println("Type /leave to leave the chatroom");
					System.out.println("Type /history to see all previous messages from this chat room");				
				}
				if (input.equals("/leave")) {
					break;
				}
				if (input.equals("/history")) {
					try {
						stmt = c.createStatement();
						ResultSet rs = stmt.executeQuery("select * from " + roomName + ";");
	
						while(rs.next()) {
							String user = rs.getString("username");
							String message = rs.getString("message");

							System.out.println("User: " + user);
							System.out.println("Message: " + message);
							System.out.println();
						}
						stmt.close();
						//c.commit();
						rs.close();
		
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println(e.getClass().getName()+": "+e.getMessage());
						System.exit(0);
					}
				}
				if (input.equals("/list")) {
					try {
						stmt = c.createStatement();
						ResultSet rs = stmt.executeQuery("select * from " + "status" + " where status = 'online';");
						
						System.out.println("Active users");
						System.out.println("------------");
	
						while(rs.next()) {
							String user = rs.getString("username");
	
							System.out.println("User: " + user);
							
						}
						//System.out.println("Done....");
						stmt.close();
						c.commit();
						rs.close();
		
					}catch(Exception e) {
						e.printStackTrace();
						System.err.println(e.getClass().getName()+": "+e.getMessage());
						System.exit(0);
					}
				}
				if (input.startsWith("/") && !input.equals("/list") && !input.equals("/history") && !input.equals("/leave") && !input.equals("/help")) {
					System.out.println("Sorry this command is not valid please type /help to see a list of all commands");
				}
					
			}
				
			mainMenu();
			scan.close();
			timer.cancel();
		}

	
		
		public void option1() {
			Scanner scan = new Scanner(System.in);
			System.out.println("Enter room name you would like to join: ");
			String roomName = scan.next();
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = "  + "'" + roomName + "');");
				while (rs.next()) {
					String exists = rs.getString("exists");
					//System.out.println(exists);
					if (exists.equalsIgnoreCase("f")) {
						System.out.println("Sorry this room does not exist press 1 to try again and press 2 to create a room");
						int userInput = scan.nextInt();
						if (userInput == 1) {
							option1();
						}
						else {
							option2();
						}
					}
				}
				//c.commit();
				stmt.close();
				rs.close();
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			System.out.println("Please enter userName: ");
			String userName = scan.next();
			joinRoom(roomName, userName);
			scan.close();
	
		}
		
		
		public void option2() {
			Scanner scan = new Scanner(System.in);
			System.out.println("Enter chatroom name: ");
			String roomName = scan.next();
			boolean isRoomValid = isNameValid(roomName);
			
			
			if (isRoomValid == false) {
				System.out.println("Invalid roomname please enter only lowercase letters and numbers");
				option2();
			}
			else {
				boolean isRoomActive = checkTable(roomName);
				if (isRoomActive == true) {
					System.out.println("Room already created press 1 to join the room and 2 to create another room");
					int response = scan.nextInt();
					
					if (response == 1) {
						System.out.println("Please enter userName: ");
						String userName = scan.next();
						joinRoom(roomName, userName);
					}
					
					else {
						option2();
					}
				}
				
				createRoom(roomName);
				System.out.println("Would you like to join the room? (1 for yes and 2 for no)");
				int response = scan.nextInt();
				if (response == 1) {
					System.out.println("Please enter userName: ");
					String userName = scan.next();
					joinRoom(roomName,userName);
				}
				else {
					mainMenu();
				}
			}
			scan.close();
		}
		
		
		public boolean isNameValid(String roomName) {
			String regex = "^(?=.*[A-Z])(?=.*[-+_!@#$%^&*., ?]).+$";
			String regex1 = "^(?=.*[a-z])(?=.*[-+_!@#$%^&*., ?]).+$";
			String regex2 = "^(?=.*[A-Z])(?=.*\\\\d).+$";
			String regex4 = "^(?=.*[A-Z]).+$";
			
			boolean isValid = true;
			
	        Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(roomName);
	        
	        if (m.matches()) {
	        	isValid = false;
	        	return isValid;
	        }
	        
	        Pattern a = Pattern.compile(regex1);
	        Matcher b = a.matcher(roomName);
	        
	        if (b.matches()) {
	        	isValid = false;
	        	return isValid;
	        }
	        
	        Pattern e = Pattern.compile(regex2);
	        Matcher f = e.matcher(roomName);
	        
	        if (f.matches()) {
	        	isValid = false;
	        	return isValid;
	        }
	       
	        Pattern v = Pattern.compile(regex4);
	        Matcher z = v.matcher(roomName);
	        
	        if (z.matches()) {
	        	isValid = false;
	        	return isValid;
	        }
	        
	        return isValid;
		}
		
		public static boolean checkTable(String roomName) {
			boolean tableExists = false;
			try {
				stmt = c.createStatement();
				String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = '" + roomName + "');";
						  
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String result = rs.getString("exists");
					
					if (result.equalsIgnoreCase("t")) {
						tableExists = true;
						return tableExists;
					}
				}
				//c.commit();
				
				stmt.close();
				rs.close();
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
			return tableExists;
		}
		
		
		public void updateInfo(String userName, String passWord) {
			Scanner scan = new Scanner(System.in);
			int userResponse;
			
			System.out.println("Would you like to change your username or password (Press 1 for username and 2 for password)");
			userResponse = scan.nextInt();
			
			if (userResponse == 1) {
			
				System.out.println("Please enter new userName");
				String newName = scan.next();
				
				boolean output = checkUserName(newName);
				
				if (output == false) {
					System.out.println("Username already taken please enter another name: ");
					updateInfo(userName,passWord);
				}
				
				try {
					stmt = c.createStatement();
					String sql = "UPDATE users set username = '" + newName + "' where username = '" + userName + "';";
					stmt.executeUpdate(sql);
					String sql2 = "UPDATE status set username = '" + newName + "' where username = '" + userName + "';";
					stmt.execute(sql2);
					c.commit();
					System.out.println("Succesfully updated userName");
				
					//stmt.close();
					//c.close();
					
					mainMenu();
				}catch(Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					System.exit(0);
				}
				scan.close();
			}
			else {
				
				System.out.println("Please enter new password");
				String newPass = scan.next();
				
				boolean output = checkPassword(newPass);
				
				
				if (output == false) {
					System.out.println("Pasword already taken please enter another: ");
					updateInfo(userName,passWord);
				}
				
				
				try {
					stmt = c.createStatement();
					String sql = "UPDATE users set password = '" + newPass + "' where password = '" + passWord + "';";
					stmt.executeUpdate(sql);
					c.commit();
					System.out.println("Succesfully updated passWord");
					
					stmt.close();
					c.close();
					
					mainMenu();
				}catch(Exception e) {
					e.printStackTrace();
					System.err.println(e.getClass().getName()+": "+e.getMessage());
					System.exit(0);
				}
			}
		}
		
		
		public void homeScreen() {
			//createTable();
			Scanner scan = new Scanner(System.in);
			
			System.out.println("Welcome to the CSE205 chat app!");
			System.out.println("-------------------------------------");
			System.out.println("What would you like to do?");
			System.out.println("(Type 1 to register, 2 for login and 3 to quit)");
			
			int result = scan.nextInt();
			
			if (result == 1) {
				newUser();
				scan.close();
			}
			
			else if (result == 2) {
				login();
				
			}
			
			else {
				System.out.println("Goodbye");
			}
		}
		
		public void mainMenu() {
			
			Scanner scan = new Scanner(System.in);
			
			System.out.println("Please select from the following options");
			System.out.println("Press 1 to join a room, press 2 to create a room, press 3 to update account information, press 4 to log out");
			int nextResult = scan.nextInt();
			if (nextResult == 1) {
				option1();
			}
			else if (nextResult == 2) {
				option2();
			}
			else if (nextResult == 3) {
				System.out.println("Please re-enter userName: ");
				String userName = scan.next();
				System.out.println("Please re-enter password: ");
				String passWord = scan.next();
				updateInfo(userName, passWord);
			}
			else {
				
				userStatusOffline(userNameStatus);
				
				}
			scan.close();
		}
				
	
		
		public static void main(String[] args) {
			try {
				Class.forName("org.postgresql.Driver");
				c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatdata",
						"postgres", "TomBrady12!");
				c.setAutoCommit(false);
				System.out.println("Connected to the database.");
			}catch(Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			
			//createTable();
			Database db = new Database();
		
			
			db.homeScreen();
		}
}
