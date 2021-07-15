# Course-Registration-System
A project in system programming - Server(Java) &amp; Client(C++) communication system using concurrency and networking .

Description:
The system simulates a client, which is a Student or an Admin , who wants to register or access information about courses.
Once the server is launching, given a file of courses , it loads the courses to his memory and ready to serve unlimited number of clients,
using the Reactor Desing Pattern, with great efficiency.

The server decodes unique commands from the client , as follows : 
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
