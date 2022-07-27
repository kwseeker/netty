#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

//TODO: 客户端也改造成epoll模式
int main(int argc,char *argv[])
{
    int sockfd,numbytes;
    char buf[BUFSIZ];
    // char * buf = "A test message from client!";
    struct sockaddr_in their_addr;

    while((sockfd = socket(AF_INET,SOCK_STREAM,0)) == -1);
    their_addr.sin_family = AF_INET;
    their_addr.sin_port = htons(8000);
    their_addr.sin_addr.s_addr=inet_addr("127.0.0.1");
    bzero(&(their_addr.sin_zero), 8);
    
    while(connect(sockfd,(struct sockaddr*)&their_addr,sizeof(struct sockaddr)) == -1);
    int flags = fcntl(sockfd, F_GETFL);
    flags |= O_NONBLOCK;
    while(fcntl(sockfd, F_SETFL, flags) < 0);

    printf("Connection to EPollServer established ...\n");

    while(1)
    {
        printf("Enter >>> ");
        scanf("%s",buf);
        numbytes = send(sockfd, buf, strlen(buf), MSG_DONTWAIT);
        numbytes=recv(sockfd,buf,BUFSIZ, MSG_DONTWAIT);  
        buf[numbytes]='\0'; 
        printf("Received: %s\n",buf);  
    }

    close(sockfd);
    return 0;
}
