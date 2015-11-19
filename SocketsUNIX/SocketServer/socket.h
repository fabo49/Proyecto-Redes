#ifndef SOCKET_H
#define SOCKET_H

#include <sys/socket.h>
#include <stdio.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>

class Socket
{
public:
    Socket();
    Socket(int desc);
    Socket(char, bool = false);
    ~Socket();
    int Close();
    int Connect(char*, int);
    int Read(char*, int);
    int Write(char* , int);
    int Listen(int);
    int Port(int);
    int Bind(int);
    int Shutdown(int);
    Socket* Accept();

private:
    int descriptor;
    int IP_family;
};

#endif // SOCKET_H
