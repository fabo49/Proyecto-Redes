#include "socket.h"


//constructor por default
Socket::Socket()
{
    descriptor = socket(AF_INET, SOCK_STREAM, 0);
    IP_family = AF_INET;
}

Socket::Socket(int desc)
//constructor que recibe descriptor y construye un Socket con dicho descriptor
{
  descriptor = desc;
}

Socket::Socket(char type, bool version)
//version: 0 -> IPv4, 1 -> IPv6
//type: tipo de socket
{
    int versionForSuckIt;
    int typeForSuckIt;
    if(!version){
        versionForSuckIt = AF_INET;
        IP_family = AF_INET;
    }
    else
    {
        versionForSuckIt = AF_INET6;
        IP_family = AF_INET6;
    }
    if(type == 'd')
        typeForSuckIt = SOCK_DGRAM;
    else
        typeForSuckIt = SOCK_STREAM;

    descriptor = socket(versionForSuckIt, typeForSuckIt, 0);
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

int Socket::Write(char* text, int len)
{
    return write(descriptor, text, len);
}

int Socket::Listen(int queue)
{
  int ret = listen(descriptor, queue);
  return ret;
}

int Socket::Bind(int port)
{
  int enable = 1;
  if (setsockopt(descriptor, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int)) < 0)
      printf("setsockopt(SO_REUSEADDR) failed");
  sockaddr_in server_addr;
  server_addr.sin_family = AF_INET;
  server_addr.sin_addr.s_addr = INADDR_ANY;
  server_addr.sin_port = htons(port);
  int res = bind(descriptor, (struct sockaddr *) & server_addr, sizeof(server_addr));
  if(res==-1)
      perror("Bind error: ");
  return res;
}

int Socket::Shutdown(int how)
{   //recibe "SHUT_RD", "SHUT_WR" o "SHUT_RDWR"
    return shutdown(descriptor, how);
}

Socket * Socket::Accept()
{
    int enable = 1;
    if (setsockopt(descriptor, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(int)) < 0)
        printf("setsockopt(SO_REUSEADDR) failed");
  socklen_t length = sizeof(in);
  perror("Accept");
  int descEntrante = accept(descriptor, &in, &length);
  perror("Accept");
  Socket * socketEntrante = new Socket(descEntrante);
  if(descEntrante != -1)
    return socketEntrante;
  else
    return NULL;
}

char* Socket::GetIPAddress()
{
    struct ifreq ifr;

    /* I want to get an IPv4 IP address */
    ifr.ifr_addr.sa_family = AF_INET;

    /* I want IP address attached to "eth0" */
    strncpy(ifr.ifr_name, "wlan0", IFNAMSIZ-1);

    ioctl(descriptor, SIOCGIFADDR, &ifr);

    return inet_ntoa(((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr);
}
