#ifndef UTIL_H_
#define UTIL_H_

ssize_t sendline(int fd, const void *str);
ssize_t readline(int fd,  void *str, size_t maxlen);

#endif      // util.h
