#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>

#include "util.h"
#include "server.h"

// read a line from fd (without the '\n') of at most maxlen characters (without the terminating null character) 
ssize_t readline(int fd,  void *str, size_t maxlen) {
    int n, rc;
    char c = 0;
    char * buffer = str;

    for(n=0; n<maxlen; n++) {
        rc = read(fd, &c, 1);

        if (rc == 1) {
            if(c == '\n' || c == 0)
                break;
            *buffer = c;
            buffer ++;
        }

        else if(rc == 0)
            break;

        else {
            if(errno == EINTR)
                continue;
            return -1;
        }

    }

    *buffer = 0;

    if(*(buffer-1) == '\r')
        *(buffer-1) = 0;

    return n;
}

// write a string to a fd of max MAX_SEND characters and returns the number of charcters written
ssize_t sendline(int fd, const void *str) {

    int nleft, nwritten, max;
    const char * buffer = str;
    max = nleft = MAX_SEND < strlen(str) ? MAX_SEND : strlen(str);
    int addend = (buffer[max-1] == '\n');
    if(addend)
        nleft--;

    while(nleft > 0) {
        if((nwritten = write(fd, buffer, nleft)) <=0) {
            if(errno == EINTR)
                nwritten = 0;
            else
                return -1;
        }
        nleft -= nwritten;
        buffer += nwritten;
    }

    if(addend)
        write(fd, "\n", 1);

    return max;
}

void senderror(int fd, const char * subtype, const char * description) {
    char * msg;
    if(subtype) {
        msg = malloc(sizeof(char)*(10+strlen(description)));
        sprintf(msg, "%s#%s#%s\n", CMD_ERR, subtype, description);
    }
    else {
        msg = malloc(sizeof(char)*(6+strlen(description)));
        sprintf(msg, "%s#%s\n", CMD_ERR, description);
    }
    sendline(fd, msg);
    free(msg);
}

void sendack(int fd, const char * subtype, const char * description) {
    char * msg;
    if(subtype) {
        if(description) {
            msg = malloc(sizeof(char)*(10+strlen(description)));
            sprintf(msg, "%s#%s#%s\n", CMD_ACK, subtype, description);
        }
        else {
            msg = malloc(sizeof(char)*10);
            sprintf(msg, "%s#%s\n", CMD_ACK, subtype);
        }
    }
    else {
        if(description) {
            msg = malloc(sizeof(char)*(6+strlen(description)));
            sprintf(msg, "%s#%s\n", CMD_ACK, description);
        }
        else {
            msg = malloc(sizeof(char)*6);
            sprintf(msg, "%s#\n", CMD_ACK);
        }
    }
    sendline(fd, msg);
    free(msg);
}
