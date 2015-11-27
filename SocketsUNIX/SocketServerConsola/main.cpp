#include <QCoreApplication>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include "socket.h"	// Include your socket interface

#define SIZE 1048576


int ChildProcess( Socket * );	// Prototype for child process


int main( int argc, char ** argv ) {

   int childpid;
   Socket s1('s', false),	// Create a socket with SOCK_STREAM
     *s2;

   s1.Bind( 9876 );		// Port to access this mirror server
   s1.Listen( 5 );		// Set backlog queue to 5 conections

   for( ; ; ) {
      s2 = s1.Accept();	 	// Wait for a conection
      childpid = fork();	// Create a child to serve the request
      if ( childpid < 0 )
         perror("server: fork error");
      else if (0 == childpid) {	// child code
         s1.Close();		// Close original socket in child
         ChildProcess( s2 );
      }
      s2->Close();		// Close socket in parent
   }
}

int ChildProcess( Socket * s ) {
   char buffer[SIZE], name[128];
   int id, st;

   s->Read( name, 128 );	// Read the file name from client
   s->Write( "OK",2 );		// Write the file name confirmation to client

   strcat( name, ".recv" );
   id = open( name, O_RDWR | O_CREAT, S_IRUSR | S_IWUSR );
   if ( -1 == id ) {
      printf( "Can not create file %s\n", name );
      exit( 1 );
   }

   while ( (st = s->Read( buffer, SIZE )) > 0 ) {	// Read returns EOF when we shutdown
      write( id, buffer, st );				// the writing socket in client
   }

   s->Write( "ACK",3 );	// Send an ACK to client, it will stop time counting upon receive
   close ( id );
   exit( 0 );

}
