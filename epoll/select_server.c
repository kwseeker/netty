#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<unistd.h>
#include<stdio.h>
#include<stdlib.h>
#include<strings.h>
#include<sys/wait.h>
#include<string.h>
#include<errno.h>

// select IO多路复用模式 socket 服务器
// 测试：
// 	gcc select_server.c -o select_server
// 	./select_server
//  telnet 127.0.0.1 6666	//多开几个telnet客户端
//	ctrl + ]
// 	send nop

#define DEFAULT_PORT 6666

int main(int argc, char** argv)
{
    int serverfd;
    struct sockaddr_in my_addr;

    unsigned int lisnum = 10;
	//1 指定通信协议类型，返回socket文件描述符
	//IPv4协议，字节流套接字，采用前两者默认通信协议
    if((serverfd = socket(AF_INET, SOCK_STREAM, 0)) == -1)
    {
        perror("socket");
        return -1;
    }
    printf("socket ok， serverfd = %d\n", serverfd);
	//2 将本地协议地址赋予socket套接字
    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(DEFAULT_PORT);
    my_addr.sin_addr.s_addr = INADDR_ANY;
    bzero(&(my_addr.sin_zero), 0);
    if(bind(serverfd, (struct sockaddr*)&my_addr, sizeof(struct sockaddr)) == -1)
    {
        perror("bind");
        return -2;
    }
    printf("bind ok \n");
	//3 转换成被动套接字（服务端套接字），指示内核应接受指向该套接字的连接请求
    if(listen(serverfd, lisnum) == -1)
    {
        perror("listen");
        return -3;
    }
    printf("listen ok \n");

	//监听可读事件的文件描述符集合
    fd_set listen_fdset;
	//
    int maxsock;
	//等待所指定描述符中任一一个就绪的超时时间，这个时间内没有一个文件描述符号就绪就超时，select返回0, 每次select后timeval也会被清空
	//设为空，会永远等待；
	//设为某个值，会等待一段固定时间
	//设为0,不会等待（轮询）
    struct timeval tv;
    int client_sockfd[5];
    bzero((void*) client_sockfd, sizeof(client_sockfd));
    int conn_amount = 0;
    maxsock = serverfd;
    char buffer[1024];
    int ret = 0;
    while(1)
    {
		//select每次返回都会将文件描述符集合中没有触发事件文件描述符去掉，所以这里每次执行select前要重新设置下要监听的集合
		//监听服务端socket文件描述符和已建立连接的客户端socket文件描述符
        FD_ZERO(&listen_fdset);
        FD_SET(serverfd, &listen_fdset);
        for(int i = 0; i<5; i++)
        {
            if(client_sockfd[i]!=0)
            {
                FD_SET(client_sockfd[i], &listen_fdset);
            }
        }
		tv.tv_sec = 30;
		tv.tv_usec = 0;
		//开始监听事件
        ret = select(maxsock+1, &listen_fdset, NULL, NULL, &tv);
        if(ret<0)
        {
            perror("select error!\n");
            break;
        }
        else if(ret == 0)
        {
            printf("timeout!\n");
            continue;
        }
        for(int i = 0; i<conn_amount; i++)
        {
			//客户端fd可读
            if(FD_ISSET(client_sockfd[i], &listen_fdset))
            {
                printf("start recv from client[%d]:\n", i);
				bzero(buffer, 1024);
                ret = recv(client_sockfd[i], buffer, 1024, 0);
                if(ret<0)
                {
                    printf("client[%d] close\n", i);
                    close(client_sockfd[i]);
                    FD_CLR(client_sockfd[i], &listen_fdset);
                    client_sockfd[i] = 0;
                }
                else
                    printf("recv from client[%d]:%s\n", i, buffer);
            }
        }
		//服务端fd可读
        if(FD_ISSET(serverfd, &listen_fdset))
        {
			//新的客户端连接
            struct sockaddr_in client_addr;  
            size_t size = sizeof(struct sockaddr_in);
			//获取客户端已完成连接的文件描述符
            int sock_client = accept(serverfd, (struct sockaddr*)(&client_addr), (unsigned int*)(&size));
            if(sock_client<0)
            {
                perror("accept error!\n");
                continue;
            }
			printf("new sock_client connected, fd =%d\n", sock_client);
            if(conn_amount<5)
            {
                client_sockfd[conn_amount++] = sock_client;
                bzero(buffer, 1024);
                strcpy(buffer, "this is a server! welcome!\n");
                send(sock_client, buffer, 1024, 0);    //把内容传给新来的客户端
                printf("new connection client[%d] %s:%d\n", conn_amount, inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
                if(maxsock < sock_client)
                    maxsock = sock_client;
                else
                {
                    printf("max connections!!!quit!!\n");
                    break;
                }
            }
        }
    }

    for(int i = 0; i<5; i++)
    {
        if(client_sockfd[i]!=0)
            close(client_sockfd[i]);
    }
    close(serverfd);
    return 0;
}