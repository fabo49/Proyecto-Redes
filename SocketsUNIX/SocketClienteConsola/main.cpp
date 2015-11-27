#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>

#include "socket.h"	// Include your socket interface
#include "chrono.h"	// Include the chrono interface

#define SIZE 512

int main() {

   Socket s(SOCK_STREAM);
   Chrono ci, cf;
   char buffer[ SIZE ];
   int id, st;
   long sent, psize;
   double rate;

   psize = 128 * 1024 * 1024;	// Print info each 128 MB

   id = open("file to transfer", O_RDONLY );
   if ( -1 == id ) {
      printf( "File not found %s\n", "file to transfer" );
      return 1;
   }

// Change with valid server IP address, like 10.1.1.10
   s.Connect( "Server IP Address", 9876 );	// Same port as server
   s.Write( "file to transfer" );	// Send first the filename to server
   s.Read( (char *) buffer, SIZE );	// Read file name confirmation from server
   printf( "%s\n", buffer );		// Print the confirmation string, can be removed

   ci.getTime();	// Start the time measurement

   sent = 0;
   while ( st = read( id, buffer, SIZE ) ) {	// Send the file
      st = s.Write( buffer, SIZE );
      sent += st;
      if ( (sent % psize) == 0 ) {	// Print info each 128 Mb, can be removed
         cf.getTime();
         cf -= ci;
         printf( "Time taken to transfer %ld bytes is: %ld sec., %ld ns\n", sent, cf.getSecs(), cf.getnSecs() );
         rate = cf.getSecs() + (cf.getnSecs()/1000000000);
         rate = (double) (sent / 1024 /1024) / rate;
         printf( "Transfer rate: %f MBps\n", rate );
      }
   }

   s.Shutdown( SHUT_WR );		// Mark the socket EOF in server
   s.Read( (char *) buffer, SIZE );	// Read the answer from server

   cf.getTime();	// Get the time now
   cf -= ci;		// Calculate the difference

   printf( "Time taken to transfer %ld bytes is: %ld sec., %ld ns\n", sent, cf.getSecs(), cf.getnSecs() );

   printf( "%s\n", buffer );		// Print the string

   rate = cf.getSecs() + (cf.getNSecs()/1000000000);
   rate = (double) (sent / 1024 /1024) / rate;
   printf( "Total transfer rate: %f MBps\n", rate );

   close(id);
}
