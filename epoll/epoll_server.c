#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <fcntl.h>
#include <pthread.h>

struct epoll_attr {
    int epfd;
    struct epoll_event *events; //触发的事件列表
    int max_events;
};

//循环等待监听事件
void channel_handle(void *ptr) {
    printf("Thread started ...\n");
    struct epoll_attr *e_attr = (struct epoll_attr*) ptr; 
    while (1) {
        int event_occur_count = epoll_wait(e_attr->epfd, e_attr->events, e_attr->max_events, -1);
        printf("epfd2 find %d events occur ...\n", event_occur_count);
        for(int i=0; i<event_occur_count; i++) {
            //监听事件，从事件中获取socket fd，进行 recv send
            if(e_attr->events[i].events & EPOLLIN) {  //有新消息可读
                //从事件中获取socket fd
                int in_socket_fd = e_attr->events[i].data.fd;
                printf("epfd2 handle one event: fd=%d, events=%d\n", in_socket_fd, e_attr->events[i].events);
                //比较好的做法是，这里不处理读写，而是创建任务提交到队列中，然后由线程进行处理
                //TODO：优化
                int numbytes;
                char buff[BUFSIZ];
                while((numbytes = recv(in_socket_fd, buff, BUFSIZ, MSG_DONTWAIT)) > 0) {
                    buff[numbytes] = '\0';
                    printf("EPollServer recv ===> %s\n",buff);
                    //echo
                    //TODO：比较合理的是基于可写事件进行send
                    if(send(in_socket_fd,buff,numbytes, MSG_DONTWAIT)<0) {  
                        perror("Error occur when write ...");  
                        return;  
                    }
                }
            } else {
                printf("epfd2 ignore one event: fd=%d, events=%d\n", e_attr->events[i].data.fd, e_attr->events[i].events);
            }
        }
    }
}

//TODO：任务队列与线程读写连入的socket
//TODO：EPoll基于事件驱动，那么什么时候触发写入（send）

int main(int argc, char *argv[])
{
    int PORT = 8000;
    int MAXEVENTS = 10;
    int BUF_SIZE = 1024;

    // 1 服务端Socket配置
    struct sockaddr_in server_addr;
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;
    bzero(&(server_addr.sin_zero), 8);
    int struct_len = sizeof(struct sockaddr_in);

    // 2 创建套接字
    int ser_socket_fd = socket(AF_INET, SOCK_STREAM, 0);
    if(ser_socket_fd == -1) {
        perror("server create socket");
        exit(EXIT_FAILURE);
    }
    // 3 bind
    int ret = bind(ser_socket_fd, (struct sockaddr *) &server_addr, struct_len);
    if(ret == -1) {
        perror("socket bind");
        exit(EXIT_FAILURE);
    }
    // 4 设置非阻塞模式
    int flag = fcntl (ser_socket_fd, F_GETFL, 0);
    flag |= O_NONBLOCK;
    fcntl (ser_socket_fd, F_SETFL, flag);
    // 5 listen
    ret = listen(ser_socket_fd, 10);
    if(ret == -1) {
        perror("socket listen");
        exit(EXIT_FAILURE);
    }
    
    printf("Waiting for connect ...\n");

    // 6 创建epoll，将套节字 ser_socket_fd 注册进去
    int epfd = epoll_create1(0);                //epoll实例(负责新连接接入)
    if(epfd == -1) {
        perror("epoll_create1");
        exit(EXIT_FAILURE);
    }
    printf("epoll fd epfd created, epfd = %d\n", epfd);
    struct epoll_event event;
    event.data.fd = ser_socket_fd;          //监听套接字
    event.events = EPOLLIN | EPOLLET;       //选择边沿触发方式，监听可读事件
    ret = epoll_ctl(epfd, EPOLL_CTL_ADD, ser_socket_fd, &event);
    if(ret < 0) {
        perror("epoll_ctl fail");
        return -1;
    }
    printf("server socket fd registered to epfd to monitor ...\n");

    int epfd2 = epoll_create1(0);               //epoll实例（负责send recv）
    if(epfd2 == -1) {
        perror("epoll_create1");
        exit(EXIT_FAILURE);
    }
    printf("epoll fd epfd2 created, epfd = %d\n", epfd2);

    struct epoll_event *events = calloc(MAXEVENTS, sizeof(struct epoll_event));   //存储触发的事件集合
    struct epoll_event *events2 = calloc(MAXEVENTS, sizeof(struct epoll_event));

    //创建2个新线程，共用epfd2，监听多个连接消息(线程和连接是一对多关系)
    // pthread_t thread1, thread2;
    // int ret_thrd1, ret_thrd2;
    // struct epoll_attr e_attr;
    // e_attr.epfd = epfd2;
    // e_attr.events = events2;
    // e_attr.max_events = MAXEVENTS;
    // ret_thrd1 = pthread_create(&thread1, NULL, (void *)&channel_handle, (void *) &e_attr);
    // ret_thrd2 = pthread_create(&thread2, NULL, (void *)&channel_handle, (void *) &e_attr);
    pthread_t thread;
    struct epoll_attr e_attr;
    e_attr.epfd = epfd2;
    e_attr.events = events2;
    e_attr.max_events = MAXEVENTS;
    int ret_thread = pthread_create(&thread, NULL, (void *) &channel_handle, (void *) &e_attr);

    while (1) {
        int event_occur_count = epoll_wait(epfd, events, MAXEVENTS, -1);
        printf("epfd find %d events occur ...\n", event_occur_count);
        for(int i=0; i<event_occur_count; i++) {
            if(events[i].events & EPOLLIN && events[i].data.fd == ser_socket_fd) {  //新的Socket连接进来
                printf("Handle connection event ...\n");
                while (1) { //一个事件可能包含有多个连接
                    struct sockaddr client_addr;
                    socklen_t ca_len = sizeof client_addr;
                    int in_socket_fd;
                    //char hbuf[BUF_SIZE], sbuf[BUF_SIZE];
                    // 7 accept
                    in_socket_fd = accept (ser_socket_fd, &client_addr, &ca_len);
                    if(in_socket_fd == -1) {
                        if(errno == EAGAIN || errno == EWOULDBLOCK) {
                            printf("New connection handle finish ...\n");
                            break;
                        }
                        perror("socket accept");
                    }
                    
                    //将in_socket_fd注册入另一个epoll
                    struct epoll_event event2;
                    event2.data.fd = in_socket_fd;          
                    event2.events = EPOLLIN | EPOLLET;       //选择边沿触发方式，监听可读事件
                    int ret = epoll_ctl(epfd2, EPOLL_CTL_ADD, in_socket_fd, &event2);
                    if(ret == -1) {
                        perror("epoll_ctl");
                    }
                }
            } else {    //其他事件
                //暂不处理
                printf("epfd ignore one event ...\n");
            }
        }
    }

    //释放资源
    pthread_join(thread, NULL);
    close(epfd);
    close(epfd2);
    close(ser_socket_fd);
    free(events);
    free(events2);
    
    return 0;
}