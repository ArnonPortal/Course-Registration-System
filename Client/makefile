CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: Client
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/client.o $(LDFLAGS)

Client: bin/connectionHandler.o bin/client.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/client.o: src/client.cpp
	g++ $(CFLAGS) -o bin/client.o src/client.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
