# Course-Registration-System
A project in system programming - Server(Java) &amp; Client(C++) communication system using concurrency and networking .

# Description:

The system simulates a client, which is a Student or an Admin , who wants to register or access information about courses.
Once the server is launching, given a file of courses , it loads the courses to his memory and ready to serve unlimited number of clients,
using the Reactor Desing Pattern, to optimize the efficiency.
The networking using TCP/IP protocol.

The server decodes unique commands from the client , as follows(See definitions below) : 

0: COMMANDS

1: ADMINREG (username) (password)

2: STUDENTREG (username) (password)

3: LOGIN (username) (password)

4: LOGOUT

5: COURSEREG (coursenum)

6: KDAMCHECK (coursenum)

7: COURSESTAT (coursenum)

8: STUDENTSTAT (coursenum)

9: ISREGISTERED (coursenum)

10: UNREGISTER (coursenum)

11: MYCOURSES

For each command the server sends to the client an acknowledge(ACK) or an error(ERROR) message which indicates if the wanted process succeeded or not. 

In addition , there 3 diffrent types of users - each of them determine which commands are allowed to use :

1)Unidentified - Logged out user.

2)Student - Logged in student.

3)Admin - Logged in admin.

The server will return an ERROR message if the user requesting an unauthorized command.



# Commands description:

0- COMMANDS - All users

This command provides the list of the supported commands.

1- ADMINREG - Unidentified only

An ADMINREG message is used to register an admin in the system.Returns an ERROR if the name is already registered.

2- STUDENTREG - Unidentified only

A STUDENTREG message is used to register a student in the system.Returns an ERROR if the name is already registered.

3- LOGIN - Unidentified only

A LOGIN message is used to login a user into the system. Returns an ERROR if the user is already logged in, or one of the parameters are invalid (e.g. incorrect password , incorrect username).

4- LOGOUT - Admin/Student only

A LOGOUT command is used to logout the user from the system.Rturns an ERROR if the user is already logged out.

5- COURSEREG - Student only

A COURSEREG command is used to register the logged in student to the <num of course> course in the system.Returns an ERROR on the following cases:
	
1.The stduent is not registered to the prior courses needed(Kdam Courses).
	
2.The course is full
	
3.The num of course does not exist in the system.
	
4.The student is not logged in
	

6- KDAMCHECK - Student only

KDAMCHECK command is used for checking the prior courses needed for a specific course. Returns an ERROR if the course doesnt exist in the system.


7- COURSESTAT - Admin only

An Admin user command which displays the status of a specific course: 
	
1.The name of the course.
	
2.Number of seats available.
	
3.A list of registered students.
	
	
Returns an ERROR if the course doesnt exist in the system , the user is not an admin or is not logged in.

	
8- STUDENTSTAT - Admin only
	

An Admin user command which displays the status of a specific student:
	
1.Student username.
	
2.List of registered courses ordered as in the courses file.
	
Returns an ERROR if the course doesnt exist in the system or the user is not logged in.
	
	

9- ISREGISTERED - Student only

This command returns REGISTERED/NOT REGISTERED if the student is registered/not registered to the input course. If the course doesnt exist or the student isnt logged in- returns and ERROR.

10- UNREGISTER - Student only

Uneregister the user from the course . Returns an ERROR if the student is already unregistered from the course or the stduent is not logged in.

11- MYCOURSES - Student only

This command displays a list of the registered courses of the student.Returns an empty list if the student is not registered to none of the courses.


# Requirements & How to Launch:
	
For Linux users: Server - Apache Maven 3.6.0 or higher. Client - gcc 7.5.0 or higher.

If not IDE is used, open the terminal and insert :

**For Server:**
	
cd {installation home}/Server
	
mvn clean
	
mvn compile
	
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGRSServer.ReactorMain" -Dexec.args="(port)" (where port is a 4 digit number, which represents the port on your computer that the server will run on)
	
**For client :** 
	
cd {installation home}/Client

make clean
	
make
	
bin/BGRSclient <server ip> <server port>
	
