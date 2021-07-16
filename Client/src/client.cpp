#include <thread>
#include "../include/connectionHandler.h"

using namespace std;

class ClientDecoder {
private:
    ConnectionHandler &connectionHandler;
    bool *terminate;
public:
    ClientDecoder(ConnectionHandler &connection, bool* _terminate) : connectionHandler(connection), terminate(_terminate) {}

    void run() {
        while (!*terminate) {
            std::string answer;
            *terminate = !(connectionHandler.getLine(answer));
        }
    }
};


int main(int argc, char *argv[]) {
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    bool terminate = false;

    ClientDecoder decoder(connectionHandler, &terminate);
    std::thread th1(&ClientDecoder::run, &decoder);

    while (!terminate) {
        const short bufsize = 1024;
        char buf[bufsize]; //Initializing the client input buffer
        std::cin.getline(buf, bufsize);
        std::string input(buf);
        connectionHandler.send(input);//This function encoding the input and sends it to the server.
        this_thread::sleep_for(std::chrono::milliseconds(100));
    }
    return 0;
}
