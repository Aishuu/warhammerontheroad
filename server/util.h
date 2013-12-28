#ifndef UTIL_H_
#define UTIL_H_

ssize_t sendline(int fd, const void *str);
ssize_t readline(int fd,  void *str, size_t maxlen);
void senderror(int fd, const char *subtype, const char *description);
void sendack(int fd, const char *subtype, const char *description);

#endif      // util.h
