/**
  * Universidad de Costa Rica
  * Escuela de Ciencias de la Computacion e Informatica
  * Laboratorio de Redes de Computadoras
  * @author Fabian Rodriguez Obando B25695
  * II Semestre 2015
  */

#include "socket.h"


//constructor por default
Socket::Socket()
{
    descriptor = socket(PF_INET, SOCK_STREAM, 0);
    IP_family = PF_INET;
}

Socket::Socket(char type, bool version)
//version: 0 -> IPv4, 1 -> IPv6
//type: tipo de socket
{
    int versionForSuckIt;
    int typeForSuckIt;
    if(!version){
        versionForSuckIt = PF_INET;
        IP_family = PF_INET;
    }else{
        versionForSuckIt = PF_INET6;
        IP_family = PF_INET6;
    }

    if(type == 'd'){
        typeForSuckIt = SOCK_DGRAM;
    }else{
        typeForSuckIt = SOCK_STREAM;
    }

    descriptor = socket(versionForSuckIt, typeForSuckIt, 0);
}

Socket::Socket(int ref)
{
    descriptor = ref;
}
//destructor
Socket::~Socket()
{
    close(descriptor);
}

int Socket::Close()
{
    return close(descriptor);
}

int Socket::Connect(char *Host, int Port)
{
    sockaddr_in host_addr;
    host_addr.sin_family = IP_family;
    inet_aton(Host, &host_addr.sin_addr);
    host_addr.sin_port = htons(Port);

    return connect(descriptor, (sockaddr*) &host_addr, sizeof(host_addr)  );
}

int Socket::Read(char *text, int len)
{
    return read(descriptor, text, len);
}

int Socket::Write(char *text, int len)
{
    return write(descriptor, text, len);
}

int Socket::Shutdown(int how)
{
    return shutdown(descriptor, how);
}

int Socket::Listen(int Queue)
{
    return listen(descriptor, Queue);
}

int Socket::Bind(int Port)
{
    sockaddr_in server_addr;
    server_addr.sin_family = IP_family;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(Port);
    return bind(descriptor, (const sockaddr*)&server_addr, sizeof(server_addr));
}

Socket* Socket::Accept()
{
    sockaddr* address;
    socklen_t len = sizeof(address);
    int newDescriptor = accept(descriptor, address, &len);
    Socket* s = new Socket(newDescriptor);
    if(newDescriptor != -1){
        return s;
    }else{
        return NULL;
    }
}
