# Course-Registration-System
A project in system programming - Server(Java) &amp; Client(C++) communication system using concurrency and networking .

Description:

The system simulates a client, which is a Student or an Admin , who wants to register or access information about courses.
Once the server is launching, given a file of courses , it loads the courses to his memory and ready to serve unlimited number of clients,
using the Reactor Desing Pattern, with great efficiency.

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




Commands description:

0- COMMANDS

This command provides the list of the supported commands.

1- ADMINREG
An ADMINREG message is used to register an admin in the system.Returns an ERROR if the name is already registered.

2- STUDENTREG
A STUDENTREG message is used to register a student in the system.Returns an ERROR if the name is already registered.

3- LOGIN

A LOGIN message is used to login a user into the system. Returns an ERROR if the user is already logged in, or one of the parameters are invalid (e.g. incorrect password , incorrect username).

4- LOGOUT

A LOGOUT command is used to logout the user from the system.Rturns an ERROR if the user is already logged out.

5- COURSEREG

A COURSEREG command is used to register the logged in user to the <num of course> course in the system.Returns an ERROR on the following cases:
	1.The user is not registered to the prior courses needed(Kdam Courses).
	2.The course is full
	3.The num of course does not exist in the system.
	3.The user is not logged in

Parameters:

Opcode: 5

Course Number: the number of the course the student wants to register to.

Command initiation: This command is initiated by entering the following text in the client command line interface: COURSEREG (CourseNum)

6- KDAMCHECK

A student user command. this message checks what are the KDAM courses of the specified course. If student registered to a course successfully, we consider him having this course as KDAM.

Parameters:

Opcode: 6

Course Number: the number of the course the user needs to know its KDAM courses. When the server gets the message it returns the list of the KDAM courses(if there are now KDAM courses it returns an empty string).

Command initiation: This command is initiated by entering the following texts in the client command line interface: KDAMCHECK (CourseNumber)

7- COURSESTAT

An Admin user command. The admin sends this message to the server to get the state of a specific course.

The client prints the state of the course as followed:

Course: (courseNum) courseName

Seats Available: numOfSeatsAvailable / maxNumOfSeats

Students Registered: listOfStudents //ordered alphabetically

Example:

Course: (42) How To Train Your Dragon

Seats Available: 22/25

Students Registered: [ahufferson, hhhaddock, thevast] //if there are no students registered yet, simply prints []

Parameters:

Opcode: 7

Course Number: the number of the course we want the state of.

Command initiation:

This command is initiated by entering the following texts in the client command line interface: COURSESTAT (courseNum)

8- STUDENTSTAT

An admin user command. A STUDENTSTAT message is used to receive a status about a specific student.

The client prints the state of the course as followed:

Student: (studentUsername)

Courses: (listOfCoursesNumbersStudentRegisteredTo)

Example:

Student: hhhaddock

Courses: [42] // if the student hasn’t registered to any course yet, simply prints []

Parameters:

Opcode: 8

Command initiation:

This command is initiated by entering the following texts in the client command line interface: STUDENTSTAT (StudentUsername)

9- ISREGISTERED

A student user command. An ISREGISTERED message is used to know if the student is registered to the specified course. The server sends back “REGISTERED” if the student is already registered to the course, otherwise, it sends back “NOT REGISTERED”.

Parameters:

Opcode: 9

Course Number: The number of the course the student wants to check.

Command initiation:

This command is initiated by entering the following texts in the client command line interface: ISREGISTERED (courseNum)

10- UNREGISTER

A student user command. An UNREGISTER message is used to unregister to a specific course. The server sends back an ACK message if the registration process successfully done, otherwise, it sends back an ERR message.

Parameters:

Opcode: 10

Course Number: The number of the course the student wants to unregister to.

Command initiation: This command is initiated by entering the following texts in the client command line interface: UNREGISTER (courseNum)

11- MYCOURSES

A student user command. A MYCOURSES message is used to know the courses the student has registered to. The server sends back a list of the courses number(in the format:[(coursenum1),(coursenum2)]) that the student has registered to (could be empty []).

Parameters:

Opcode: 11

Command initiation:

This command is initiated by entering the following texts in the client command line interface: MYCOURSES
