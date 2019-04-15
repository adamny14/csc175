 /* tcp client skeleton */

#include<stdio.h>
#include<stdlib.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<unistd.h>
#include<netdb.h>
#include<fcntl.h>


/*
   argv[0] is always the name of the program itself (implicit)
   argv[1] will be ip address of server
   argv[2] will be port number of server
*/

/*
   The read command of c takes an argument indicating the MAXIMUM
   number of bytes to read, and returns the actual number of bytes read,
   so to keep reading until we've read exactly a cetain number of bytes,
   we should use the following:
*/

void readfully(int cfd, void* addr, int size) 
{
  int r = 0; // number of bytes read so far
  while (r<size)
    {
      r += read(cfd, (unsigned char*)(addr + r), size-r);
    }
}


int main(int argc, char **argv)
{
  int cfd; // communication file descriptor (handle on socket)
  // declare structure to hold socket address info:
  struct sockaddr_in serveraddr;
  int retval; // many functions return values to indicate success/failure
  int x;  // some data
  unsigned char buffer[128];  // binary data buffer (sample)
  
  serveraddr.sin_family = AF_INET;  // always for IPv4
  serveraddr.sin_addr.s_addr = inet_addr(argv[1]);
  serveraddr.sin_port = htons(atoi(argv[2]));

  cfd = socket(AF_INET,SOCK_STREAM,0); // register tcp socket with OS

  // make connection to server.
  retval = connect(cfd,(struct sockaddr*)&serveraddr,sizeof(serveraddr));
  // note the type cast to more generic type (polymorphism)
  if (retval != 0) { perror("socket failure!"); exit(1); }
  
  // at this point, tcp connection is in ESTABLISHED state, and
  // communication with peer can begin:  (yeah!)

  readfully(cfd,&x,sizeof(int)); // read 4 bytes
  x = ntohl(x);             // convert to host byte ordering
  readfully(cfd,buffer,x);       // read x bytes into buffer
                            // note: buffer already pointer, no &
  write(cfd,buffer+120,8); // write last 8 bytes of buffer to socket

  printf("complete\n");
  
  close(cfd); // close socket
  exit(0);
}

/*
   Compile on Linux with "gcc clientskl.c"

   Solaris and other Unix systems (possibly cygwin also) may require
   "gcc -lnsl -lsocket clientskl.c"
*/
