#include<stdio.h>
#include<sys/epoll.h>

int main(int argc,char *argv[])
{
    printf("epoll_event size: %ld\n", sizeof(struct epoll_event));
    printf("epoll_event size: %ld\n", sizeof(void *));
}