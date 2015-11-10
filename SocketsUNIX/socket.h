/**
  * Universidad de Costa Rica
  * Escuela de Ciencias de la Computacion e Informatica
  * Laboratorio de Redes de Computadoras
  * Laboratorio 4: Sockets, cliente y servidor
  * @author Fabian Rodriguez Obando
  * B25695
  * II Semestre 2015
  */

#ifndef SOCKET_H
#define SOCKET_H

#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>

class Socket
{
public:

    /**
     * @brief constructor default para la clase, se asigna con PF_INET y SOCK_STREAM.
     */
    Socket();

    /**
     * @brief constructor para que el usuario especifique el dominio y el tipo del socket.
     */
    Socket(char, bool = false);

    /**
     * @brief constructor para utilizar en Accept
     * @param la referencia a un socket con la info del cliente que nos ha contestado.
     */
    Socket(int);

    /**
      * @brief destructor de la clase, encapsula el system call "close"
      */
    ~Socket();

    /**
     * @brief Encapsula el system call "connect" de UNIX
     * @param Host es una direccion IP
     * @param Port es el puerto por el que se va a hacer la conexion
     * @return -1 si hubo un error
     */
    int Connect(char* Host, int Port);

    /**
     * @brief Encapsula el system call "read" de UNIX
     * @param text es el buffer de donde se lee
     * @param len es el tamanio del buffer
     * @return La cantidad de bytes que fueron leidos, -1 si hubo algun error
     */
    int Read(char* text, int len);

    /**
     * @brief Encapsula el system call "write" de UNIX
     * @param text es el buffer donde se va a escribir
     * @param len es el tamanio del buffer
     * @return El numero de bytes que se escribieron, -1 si hubo error
     */
    int Write(char* text, int len);

    /**
     * @brief Encapsula el system call "shutdown" de UNIX
     * @param how == SHUT_WR -> se deshabilitan transmisiones futuras
     * @param how == SHUT_RD -> se deshabilitan recepciones futuras
     * @param how == SHUT_RDWR -> se deshabilitan transmisiones y recepciones futuras
     * @return 0 si no hay error, -1 si hubo algun error
     */
    int Shutdown(int how);

    /**
     * @brief Encapsula el system call "listen" de UNIX
     * @param Queue es el largo maximo de la cola de conexiones pendientes
     * @return -1 si hubo un error
     */
    int Listen(int Queue);

    /**
     * @brief Encapsula el system call "bind" de UNIX
     * @param Port es el puerto con el cual se quiere asociar
     * @return -1 si hubo un error
     */
    int Bind(int Port);

    /**
     * @brief Encapsula el system call "accept" de UNIX, extrae la primera peticion del a cola de conecciones pendientes.
     * @return -1 si hubo un error, si no hay error, retorna el descriptor del socket aceptado.
     */
    Socket* Accept();

    /**
     * @brief Encapsula el system call "close" de UNIX.
     * @return -1 si no se pudo cerrar el Socket.
     */
    int Close();

private:
    int descriptor; // identificador del sucket
    int IP_family;  // para asignarle si es IP v4 o IP v6
};

#endif // SOCKET_H
