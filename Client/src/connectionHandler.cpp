#include <boost/lexical_cast.hpp>
#include "../include/connectionHandler.h"

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
    }
    return true;
}

short bytesToShort(char* bytesArr,int pos)
{
    short result = (short)((bytesArr[pos] & 0xff) << 8);
    result += (short)(bytesArr[pos+1] & 0xff);
    return result;
}

void shortToBytes(short num, char *bytesArr,int begin) {
    bytesArr[begin] = ((num >> 8) & 0xFF);
    bytesArr[begin+1] = (num & 0xFF);
}

bool ConnectionHandler::twoStringsToBytes(short &opcode, std::string &input) {
    std::string part1 = "  " + input.substr(0, input.find(' ')) + ' ';
    std::string part2 = input.substr(input.find(' ') + 1) + ' ';
    input = part1 + part2 ;
    char *arr = new char[input.length()];
    std::strcpy(arr, input.c_str());
    arr[part1.length()-1]='\0';
    arr[input.length()-1]='\0';
    shortToBytes(opcode, arr,0);
    return sendBytes(arr, input.length());
}

bool ConnectionHandler::oneStringToBytes(short &opcode, std::string &input) {
    input = "  "+ input +" ";
    char *arr = new char[input.length()];
    std::strcpy(arr, input.c_str());
    arr[input.length()-1]='\0';
    shortToBytes(opcode, arr,0);
    return sendBytes(arr, input.length());
}

bool ConnectionHandler::numStringToBytes(short &opcode, std::string &input) {
    char *arr = new char[4];
    short courseNum = boost::lexical_cast<short>(input);
    shortToBytes(opcode, arr, 0);
    shortToBytes(courseNum, arr, 2);
    return sendBytes(arr, 4);
}
//This function sends and array of bytes to the server byte by byte.
bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

//For each valid input the function encodes its opcode(2 bytes) and the parameters (if neccesary).
bool ConnectionHandler::send(std::string &input){
    short opcode = 0;
    if(input.find("COMMANDS") != std::string::npos){
        std::cout << "1: ADMINREG <name> <pass>\n"
                     "2: STUDENTREG <name> <pass>\n"
                     "3: LOGIN <name> <pass>\n"
                     "4: LOGOUT\n"
                     "5: COURSEREG <number of course>\n"
                     "6: KDAMCHECK <number of course>\n"
                     "9: ISREGISTERED <number of course>\n"
                     "10: UNREGISTER <number of course>\n"
                     "11: MYCOURSES\n"
                     "Admin Only :\n"
                     "7: COURSESTAT <number of course>\n"
                     "8: STUDENTSTAT <number of course>\n"
                     << std::endl;
        return true;
    }
    if (input.find("ADMINREG") != std::string::npos) {
        opcode = 1;
        std::string toBytes = input.substr(9); // ADMINREG REMOVED
        return twoStringsToBytes(opcode,toBytes);
    }
    if (input.find("STUDENTREG") != std::string::npos) {
        opcode = 2;
        std::string toBytes = input.substr(11); // STUDENTREG REMOVED
        return twoStringsToBytes(opcode,toBytes);

    }
    if (input.find("LOGIN") != std::string::npos) {
        opcode = 3;
        std::string toBytes = input.substr(6); // LOGIN REMOVED
        return twoStringsToBytes(opcode,toBytes);
    }
    if (input.find("LOGOUT") != std::string::npos) {
        opcode = 4;
        char *arr = new char[2];
        shortToBytes(opcode, arr, 0);
        return sendBytes(arr, 2);
    }
    if (input.find("COURSEREG") != std::string::npos) {
        opcode=5;
        std::string toBytes = input.substr(10); // COURSEREG REMOVED
        return numStringToBytes(opcode,toBytes);
    }
    if (input.find("KDAMCHECK") != std::string::npos) {
        opcode=6;
        std::string toBytes = input.substr(10); // KDAMCHECK REMOVED
        return numStringToBytes(opcode,toBytes);
    }
    if (input.find("COURSESTAT") != std::string::npos) {
        opcode=7;
        std::string toBytes = input.substr(11); // COURSESTAT REMOVED
        return numStringToBytes(opcode,toBytes);
    }
    if (input.find("STUDENTSTAT") != std::string::npos) {
        opcode=8;
        std::string toBytes = input.substr(12); // STUDENTSTAT REMOVED
        return oneStringToBytes(opcode,toBytes);
    }
    if (input.find("ISREGISTERED") != std::string::npos) {
        opcode=9;
        std::string toBytes = input.substr(13); // ISREGISTERED REMOVED
        return numStringToBytes(opcode,toBytes);
    }
    if (input.find("UNREGISTER") != std::string::npos) {
        opcode=10;
        std::string toBytes = input.substr(11); // UNREGISTER REMOVED
        return numStringToBytes(opcode,toBytes);
    }
    if (input.find("MYCOURSES") != std::string::npos) {
        opcode=11;
        char *arr = new char[2];
        shortToBytes(opcode, arr, 0);
        return sendBytes(arr, 2);
    }
       std::cout << "Unknown Command" << std::endl;
    return false;
}

//Decodes the message from the server byte by byte
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
        size_t tmp = 0;
        boost::system::error_code error;
        try {
            while (!error && bytesToRead > tmp ) {
                tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
            }
            if(error)
                throw boost::system::system_error(error);
        } catch (std::exception& e) {
            std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
            return false;
        }
        return true;
    }
//Using the getBytes decoder , this function printing the response from the server to the client's screen.
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    char* opMessage=new char[4];
    getBytes(opMessage,4);
    short isAck=bytesToShort(opMessage,0);
    short messageOpcode=bytesToShort(opMessage,2);

    if((isAck == 13)){
        std::cout << "ERROR " << messageOpcode << std::endl;
        return true;
    }

    if(messageOpcode == 10){
        std::cout << "ACK " << messageOpcode << std::endl;
        return true;
    }
    try {
        do {
            if (!getBytes(&ch, 1)) {
                return false;
            }
            if (ch != '\0')
                frame.append(1, ch);
        } while ('\0' != ch);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }

    std::cout << "ACK " << messageOpcode << " " << frame << std::endl;
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}


