CC=gcc
CFLAGS=-Wall
LDFLAGS=
EXEC=server
SRC= $(wildcard *.c)
OBJ= $(SRC:.c=.o)

all: $(OBJ)
	$(CC) -o $(EXEC) $^ $(LDFLAGS)

%.o: %.c
		$(CC) -o $@ -c $< $(CFLAGS)

.PHONY: clean

clean:
	rm -rf *.o
