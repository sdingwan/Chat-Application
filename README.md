Java Chat Application with PostgreSQL

A secure and interactive chat application built in Java, using PostgreSQL for database management. This application allows users to register, log in, create/join chat rooms, and exchange messages with real-time updates. It also features user status tracking and provides a comprehensive chat experience with data integrity and user authentication.

Table of Contents
Features
Getting Started
Usage
Database Configuration
Contributing
License
Features

User Authentication: Users can register accounts and securely log in.

User Status Tracking: Real-time tracking of user status (online/offline).

Chat Room Management: Users can create and join chat rooms.

Interactive Messaging: Exchange messages with other users in real-time.

Command-Line Interface: Includes helpful commands like /help, /list, /history, and /leave.

Getting Started
Follow these steps to set up and run the application:

Prerequisites:

Ensure you have Java (8 or higher) and PostgreSQL (13 or higher) installed on your system.
Clone the Repository:

sh
Copy code
git clone https://github.com/yourusername/your-repo.git
cd your-repo
Database Configuration:

Open Database.java and configure your PostgreSQL database connection:
java
Copy code
c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatdata", "your-username", "your-password");
Compile and Run:

Compile the code:
Copy code
javac Database.java

Run the application:
Copy code
java Database

Usage
Register: Choose to register a new account.

Login: Log in with an existing account.

Chat Rooms: Create or join chat rooms to start chatting with other users.

Commands: Use commands like /help, /list, /history, and /leave for various functionalities.

Account Info: Update your account information (username or password) from the main menu.

Database Configuration
In Database.java, configure your PostgreSQL database connection with your credentials:


Copy code
c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/chatdata", "your-username", "your-password");

Contributing
We welcome contributions! Feel free to open issues, submit pull requests, or suggest improvements.

