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
char *iptoa(unsigned char buf[4])
{
  char *s = (char*)malloc(16); // max number of chars needed
  sprintf(s,"%d.%d.%d.%d",buf[0],buf[1],buf[2],buf[3]);
  return s;  
}
// use as in:
//int ip = htonl(-2); // 255.255.255.254 converted to network byte ordering
//printf("%s\n", iptoa( (unsigned char*)&ip ) ); // prints 255.255.255.254

// Even better, this one writes to a char* allocated elsewhere,
// provides better protection against memory leaks:

// converts 4-byte int to char* ip address
void iptostring(int *bip, char *sip)
{
  unsigned char* buf = (unsigned char*)bip; // cast int* to array of bytes
  sprintf(s,"%d.%d.%d.%d",buf[0],buf[1],buf[2],buf[3]);
}
//usage:
// int ip = htonl(-3); // 255.255.255.253 as a 4-byte int
// char ipstring[16]; // "255.255.255.253\0" requires 16 chars.
// iptostring(&ip,ipstring); // result written to local mem, no gc needed.


void readfully(int cfd, igned char * buf, int size) 
{
  int total = 0;
  int br;
  while (r<size)
    {
      br = read(fd, buff+total, (len,total));
      total += br;
    }
}


int main(int argc, char **argv)
{
  int cfd; // communication file descriptor (handle on socket)
  // declare structure to hold socket address info:
  int result;
  int x, i;
  char *internip = argv[1];
  char *internport = argv[2];
  char *externport = argv[3];
  char *protocal = argv[4];
  char *serverip = argv[5];

  in_addr_t myip = inet_addr(argv[1]);
  unsigned short myport = htons((unsigned short)atoi(internport));
  unsigned short outerport = htons((unsigned short)atoi(externport));
  unsigned char proto = (unsigned char)atoi(protocal);

  in_addr_t eip;
  unsigned short eport;

  struct sockaddr_in serveraddr;
  unsigned char buffer[128];  // binary data buffer (sample)
  
  serveraddr.sin_family = AF_INET;  // always for IPv4
  serveraddr.sin_addr.s_addr = inet_addr(serverip);
  serveraddr.sin_port = myport;

  cfd = socket(AF_INET,SOCK_STREAM,0); // register tcp socket with OS

  // make connection to server.
  result = connect(cfd,(struct sockaddr*)&serveraddr,sizeof(serveraddr));
  // note the type cast to more generic type (polymorphism)
  if (result != 0) { perror("socket failure!"); exit(1); }
  
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
