#include <sys/socket.h>
#include <sys/types.h>
#include <sys/time.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/select.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>

#include "server.h"
#include "util.h"
#include "error.h"

// global variables
player   players[MAX_PLAYER];               // set of connected players
game     games[MAX_GAME];                   // set of created games
fproc    fileprocesses[MAX_FILE_TRANSFER];  // set of child processes
int nb_client = 0;                          // number of clients connected
int nb_game = 0;                            // number of games created
int nb_file_processes = 0;                  // number of child processes transferring files

// Init the server socket
int init(int argc, char ** argv) {
    /*  Get port number from the command line, and
        set to default port if no arguments were supplied  
        */
    short int port;
    if(1 == argc)
        port = PORT;
    else if(2 == argc)
        port = strtol(argv[1], NULL, 0);
    else {
        fprintf(stderr, "Usage: \" %s [port] \"\n\t port : port to use for the server.\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    /* Init PRNG */
    srand(time(NULL));

    /*  Create the listening socket  */
    int sock_serv = socket(AF_INET, SOCK_STREAM, 0);
    if (sock_serv == -1) {
        perror("socket");
        exit(EXIT_FAILURE);
    }
    // enable to reuse addr right after we stopped the server
    int optval = 1;
    setsockopt(sock_serv, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));

    printf("\rServer now listening on socket %d\n", sock_serv);

    /*  Set all bytes in socket address structure to
        zero, and fill in the relevant data members   */
    struct    sockaddr_in servaddr;  // socket address struct 
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);

    /*  Bind our socket addresss to the 
        listening socket, and call listen()  */
    if (bind(sock_serv, (struct sockaddr *) &servaddr, sizeof(servaddr)) == -1) {
        fprintf(stderr, "Error when trying to bind !\n");
        exit(EXIT_FAILURE);
    }
    if (listen(sock_serv, BACKLOG) == -1) {
        perror("listen");
        exit(EXIT_FAILURE);
    }
    return sock_serv;
}

// Wait for activity from any socket or from stdin
void waitForActivity(fd_set *rfds, int sock_serv) {
    //clear the socket set
    FD_ZERO(rfds);

    //Add master socket to the set
    FD_SET(sock_serv, rfds);
    int fd_max;	
    fd_max = sock_serv;

    // Monitor stdin
    FD_SET(fileno(stdin), rfds);

    // add client sockets to set
    int i;
    for(i = 0 ; i < nb_client ; i++)
        if(players[i] != NULL) {
            FD_SET(players[i]->fd, rfds);
            if(players[i]->fd > fd_max)			
                fd_max = players[i]->fd;
        }

    // add child processes' sockets to set
    for(i=0; i<nb_file_processes; i++) {
        FD_SET(fileprocesses[i]->in, rfds);
        if(fileprocesses[i]->in > fd_max)
            fd_max = fileprocesses[i]->in;
    }

    // Wait for activity
    if(select(fd_max + 1, rfds, NULL, NULL, NULL) < 0)
        printf("\rselect error\n");
}

// parse command from stdin
int parseStdin(const char * cmd) {
    if(strcmp(cmd,QUIT_MESSAGE) == 0)
        return -1;
    return 0;
}

// accept a new incoming socket
void acceptNewSocket(int sock_serv) {
    struct 	  sockaddr_in csin;
    socklen_t recsize = sizeof(csin);

    // accept new socket
    int new_socket = accept(sock_serv, (struct sockaddr *) &csin, &recsize);

    if(nb_client < MAX_PLAYER) {

        if(new_socket < 0) {
            perror("accept");
            exit(EXIT_FAILURE);
        }

        players[nb_client]=malloc(sizeof(struct _player));
        if(players[nb_client] < 0) {
            perror("malloc");
            exit(EXIT_FAILURE);
        }

        players[nb_client]->fd = new_socket;
        players[nb_client]->game = NULL;
        nb_client ++;
        printf("\rNew connection, socket n° : %d (total : %d)\n", new_socket, nb_client);
    }
    else {
        senderror(new_socket, NULL, ERR_TOO_MANY_PLAYERS);
        close(new_socket);
        printf("\rA client has been rejected !\n");
    }
}

void removeChildProcess(int i);

void removeClient(player p) {
    int j,k;

    printf("\rSocket closed : %d (total : %d)\n", p->fd, nb_client-1);
    close(p->fd);

    game g;
    // if the player was in a game
    if((g = p->game)) {
        // if the player was the GM, remove it
        if(p == g->game_master)
            g->game_master = NULL;

        // Notification to send to the others
        char * msg = malloc((strlen(p->name)+6)*sizeof(char));
        sprintf(msg, "%s#%s\n", CMD_DISCONNECTED, p->name);

        for(j=0; j<g->nb_players; j++)
            if(g->players[j] == p) {
                // update the table of players in the game
                for(k=j; k<g->nb_players-1;k++)
                    g->players[k] = g->players[k+1];
                g->nb_players--;
                j--;
            }
            else
                // send notification
                sendline(g->players[j]->fd, msg);
        free(msg);

        // no more players in the game
        if(g->nb_players == 0) {
            printf("\rNo more players in game %d : game deleted\n", g->id);
            for(j=0; j<nb_game; j++)
                if(g == games[j]) {
                    // update the table of games
                    for(k=j; k<nb_game-1; k++)
                        games[k] = games[k+1];
                    break;
                }
            free(g);
        }
    }

    for(j=0; j<nb_client; j++)
        if(players[j] == p) {
            // update the table of clients
            for(k=j; k<nb_client-1; k++)
                players[k] = players[k+1];
            break;
        }

    // remove child processes triggered by this player
    for(j=0; j<nb_file_processes; j++)
        if(fileprocesses[j]->sender == p)
            removeChildProcess(j);

    free(p);

    nb_client --;
}

// broadcast a message
void broadcastMessage(player p, char * buffer) {
    int j;

    if(!p->game) {
        senderror(p->fd, buffer, ERR_NOT_IN_GAME);
        return;
    }

    char * msg = malloc((strlen(buffer+4)+strlen(p->name)+7)*sizeof(char));
    sprintf(msg, "%s#%s#%s\n", buffer, p->name, buffer+4);
    for(j=0; j < p->game->nb_players; j++)
        if ( p->game->players[j] != p )
            sendline(p->game->players[j]->fd, msg);
    free(msg);
}

// bind to a game
void bindToGame(player p, char * buffer) {
    int j;
    buffer[9] = 0;

    // already in a game
    if(p->game) {
        senderror(p->fd, NULL, ERR_ALREADY_IN_GAME);
        return;
    }

    // id of the game
    int id = atoi(buffer+4);

    // search for the game
    game g = NULL;
    for(j=0; j<nb_game; j++)
        if(games[j]->id == id) {
            g = games[j];
            break;
        }

    // if it doesn't exist, 
    if(!g) {
        // Too many games
        if(nb_game >= MAX_GAME) {
            printf("\rThere are so many games boy...\n");
            senderror(p->fd, CMD_BIND, ERR_TOO_MANY_GAMES);
            return;
        }

        // Create a new game
        printf("\rNew game created by %s\n", buffer+10);
        games[nb_game] = g = malloc(sizeof(struct _game));
        g->nb_players = 0;
        g->id = id;
        g->game_master = p;
        nb_game++;
    }

    // update the name of the player
    strncpy(p->name, buffer+10, MAX_NAME_SIZE);

    // if game is already full
    if(g->nb_players >= MAX_PLAYER_GAME) {
        printf("\rToo many players in this game !\n");
        senderror(p->fd, CMD_BIND, ERR_TOO_MANY_PLAYERS_IN_GAME);
        return;
    }

    // check if the name is already used
    for(j=0; j<g->nb_players; j++)
        if(strcmp(g->players[j]->name, p->name) == 0) {
            printf("\rName already used !\n");
            senderror(p->fd, CMD_BIND, ERR_NAME_USED);
            return;
        }

    // send notification to players in the game
    char * msg = malloc(sizeof(char)*(6+strlen(p->name)));
    sprintf(msg, "%s#%s\n", CMD_CONNECTED, p->name);
    for(j=0;j<g->nb_players; j++)
        if(g->players[j] != p)
            sendline(g->players[j]->fd, msg);
    free(msg);

    // update player, game and table of games
    p->game = g;
    g->players[g->nb_players] = p;
    g->nb_players++;
}

void listGames(player p) {
    int j;

    if(nb_game == 0) {
        sendack(p->fd, CMD_LIST, NULL);
        return;
    }

    // initialize message
    char * msg = malloc(sizeof(char)*(nb_game*(5+MAX_NAME_SIZE)+nb_game - 1));
    *msg = 0;

    // for each game
    for(j=0;j<nb_game;j++) {
        char tmp[6];
        // print id and game master's name
        sprintf(tmp, "#%05d", games[j]->id);
        strcat(msg, tmp);
        if(games[j]->game_master != NULL)
            strcat(msg, games[j]->game_master->name);
    }

    sendack(p->fd, CMD_LIST, msg);
    free(msg);
}

// send an available game id
void getAvailableId(player p) {
    int j,k;

    int r=rand()%99999;
    int id;
    for(k=0;k<99999;k++) {
        int exist = 0;
        id = (r+k)%99999+1;
        for(j=0;j<nb_game; j++)
            if(games[j]->id == id) {
                exist = 1;
                break;
            }
        if(!exist)
            break;
    }

    // No available id found
    if(k == 99999) {
        sendline(p->fd, "ERR#WTF ?? No more ids available ?!\n");
        return;
    }

    char msg[6];
    sprintf(msg, "%05d\n", id);
    sendack(p->fd, CMD_CREATE, msg);
}

// transfer a file
// for the son, never return
void transfer_file(player p, char * buffer) {
    int out[2], in[2];
    int childpid;
    int size;
    char * filepath;
    buffer[10] = 0;

    // if in a game
    if(!p->game) {
        senderror(p->fd, CMD_FILE, ERR_NOT_IN_GAME);
        return;
    }

    // if there is no game master or if the player is not game master
    if(p->game->game_master != p) {
        senderror(p->fd, CMD_FILE, ERR_ONLY_GM);
        return;
    }

    // check for file size
    if((size = atoi(buffer+4)) > MAX_FILE_SIZE) {
        senderror(p->fd, CMD_FILE, ERR_FILE_TOO_BIG);
        return;
    }

    // path of the file is specified
    if(strlen(buffer+11) == 0) {
        senderror(p->fd, NULL, ERR_NOT_RECOGNIZED);
        return;
    }

    filepath = malloc(sizeof(char)*(strlen(buffer+11)+1));
    strcpy(filepath, buffer+11);
    printf("\rPlayer %s is trying to broadcast the file : %s (%dB)\n", p->name, filepath, size);

    // The file can be transmitted
    if(nb_file_processes >= MAX_FILE_TRANSFER) {
        printf("\rToo many ongoing file transfers !\n");
        senderror(p->fd, CMD_FILE, ERR_TOO_MANY_FILE_TRANSFERS);
        return;
    }

    // create a pipe to communicate between the two processes
    pipe(in);
    pipe(out);

    // fork
    if((childpid = fork()) == -1) {
        printf("\rServer failed to fork !\n");
        senderror(p->fd, CMD_FILE, ERR_INTERNAL);
        return;
    }

    // for the child
    if(childpid == 0) {
        // close the input part of one pipe and the output part of the other one
        close(out[0]);
        close(in[1]);

        // create the listening socket
        int filelistener = socket(AF_INET, SOCK_STREAM, 0);
        if(filelistener == -1) {
            senderror(out[1], NULL, "Could not open socket");
            exit(EXIT_FAILURE);
        }
        // enable to reuse addr right after we stopped the server
        int optval = 1;
        setsockopt(filelistener, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));

        struct    sockaddr_in servaddr;  // socket address struct 
        memset(&servaddr, 0, sizeof(servaddr));
        servaddr.sin_family = AF_INET;
        servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
        servaddr.sin_port = htons(0);

        // bind socket
        if(bind(filelistener, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1) {
            senderror(out[1], NULL, "Error when trying to bind !");
            exit(EXIT_FAILURE);
        }

        // get port
        socklen_t length = sizeof(servaddr);
        getsockname(filelistener, (struct sockaddr *)&servaddr, &length);
        int port = ntohs(servaddr.sin_port);

        // Listen on the port
        if(listen(filelistener, MAX_PLAYER_GAME+2) == -1) {
            senderror(out[1], NULL, "Error when trying to listen !");
            exit(EXIT_FAILURE);
        }

        // send port to parent
        char * msg = malloc(sizeof(char)*(12+strlen(filepath)));
        sprintf(msg, "%s#%05d#%s\n", CMD_PORT, port, filepath);
        sendline(out[1], msg);
        free(msg);

        // wait for the connection of the sender
        fd_set rfds;
        FD_ZERO(&rfds);
        FD_SET(filelistener, &rfds);
        int fd_max;
        fd_max = filelistener;
        struct timeval to = {TIMEOUT_FILE, 0};
        if(select(fd_max+1, &rfds, NULL, NULL, &to) < 0) {
            senderror(out[1], NULL, "Error when trying to select !");
            exit(EXIT_FAILURE);
        }

        int sender_socket;
        if(FD_ISSET(filelistener, &rfds)) {
            struct 	  sockaddr_in csin;
            socklen_t recsize = sizeof(csin);
            sender_socket = accept(filelistener, (struct sockaddr *) &csin, &recsize);
        }
        else {
            senderror(out[1], NULL, "Timeout before sender tries to connect !");
            exit(EXIT_FAILURE);
        }

        // get the file from the sender
        char filename[12+MAX_NAME_SIZE];
        sprintf(filename, "/tmp/%05d_%s", p->game->id, p->name);
        int serverfile = open(filename, O_WRONLY | O_CREAT | O_TRUNC, S_IRUSR | S_IWUSR);
        int sent = 0;
        while(sent < size) {

            // prepare the set
            fd_set rfds;
            FD_ZERO(&rfds);
            FD_SET(sender_socket, &rfds);
            fd_max = sender_socket;
            to.tv_sec = TIMEOUT_FILE;
            to.tv_usec = 0;

            if(select(fd_max+1, &rfds, NULL, NULL, &to) < 0) {
                senderror(out[1], NULL, "Error when trying to select !");
                close(serverfile);
                unlink(filename);
                exit(EXIT_FAILURE);
            }

            if(FD_ISSET(sender_socket, &rfds)) {
                int rc = read(sender_socket, buffer, MAXBUF);
                if(write(serverfile, buffer, rc) < 0) {
                    senderror(out[1], NULL, "Error when writing to tmp file !");
                    close(serverfile);
                    unlink(filename);
                    exit(EXIT_FAILURE);
                }
                sent += rc;
            }
            else {
                senderror(out[1], NULL, "Timeout when sender was sending data");
                close(serverfile);
                unlink(filename);
                exit(EXIT_FAILURE);
            }
        }
        close(serverfile);
        close(sender_socket);

        // send notification to the parent that we are ready to broadcast
        msg = malloc(sizeof(char)*(19+strlen(filepath)));
        sprintf(msg, "%s#%05d#%06d#%s\n", CMD_READY, port, size, filepath);
        sendline(out[1], msg);
        readline(in[0], buffer, MAXBUF);
        if(buffer[3] != '#') {
            senderror(out[1], NULL, "Who are you ?");
            unlink(filename);
            exit(EXIT_FAILURE);
        }
        buffer[3] = 0;
        if(strcmp(buffer, CMD_ACK) != 0) {
            senderror(out[1], NULL, "Who are you ?");
            unlink(filename);
            exit(EXIT_FAILURE);
        }
        int nb_players = atoi(buffer+4);

        // send the file to the other players
        int connected_clients = 0, served_clients = 0;
        int * sock_client = malloc(sizeof(int)*nb_players);
        int * fd_client = malloc(sizeof(int)*nb_players);
        int * byte_written = malloc(sizeof(int)*nb_players);

        while(served_clients < nb_players-1) {
            fd_set rfds;
            fd_set rfds_write;
            FD_ZERO(&rfds);
            FD_ZERO(&rfds_write);
            FD_SET(filelistener, &rfds);
            fd_max = filelistener;
            int j;
            for(j=0; j<connected_clients; j++) {
                FD_SET(sock_client[j], &rfds_write);
                if(sock_client[j] > fd_max)
                    fd_max = sock_client[j];
            }
            to.tv_sec = TIMEOUT_FILE;
            to.tv_usec = 0;
            int return_value;

            if((return_value = select(fd_max+1, &rfds, &rfds_write, NULL, &to)) < 0) {
                senderror(out[1], NULL, "Error when trying to select !");
                unlink(filename);
                exit(EXIT_FAILURE);
            }
            if(return_value == 0) {
                senderror(out[1], NULL, "Timeout clients");
                unlink(filename);
                exit(EXIT_FAILURE);
            }

            if(FD_ISSET(filelistener, &rfds)) {
                struct 	  sockaddr_in csin;
                socklen_t recsize = sizeof(csin);
                int s = accept(filelistener, (struct sockaddr *) &csin, &recsize);
                if(connected_clients < nb_players) {
                    sock_client[connected_clients] = s;
                    fd_client[connected_clients] = open(filename, O_RDONLY);
                    byte_written[connected_clients] = 0;
                    connected_clients ++;
                }
                else
                    close(s);
            }

            for(j=0; j<connected_clients; j++)
                if(FD_ISSET(sock_client[j], &rfds_write)) {
                    int b_read = read(fd_client[j], buffer, size-byte_written[j] < MAXBUF ? size-byte_written[j] : MAXBUF);
                    int b_written = write(sock_client[j], buffer, b_read);
                    if(b_written == 0) {
                        close(sock_client[j]);
                        close(fd_client[j]);
                        int k;
                        for(k=j; k<connected_clients-1; k++) {
                            fd_client[k] = fd_client[k+1];
                            sock_client[k] = sock_client[k+1];
                            byte_written[k] = byte_written[k+1];
                        }
                    }
                    lseek(sock_client[j], b_written-b_read, SEEK_CUR);
                    byte_written[j] += b_written;
                    if(byte_written[j] == size) {
                        close(sock_client[j]);
                        close(fd_client[j]);
                        int k;
                        for(k=j; k<connected_clients-1; k++) {
                            fd_client[k] = fd_client[k+1];
                            sock_client[k] = sock_client[k+1];
                            byte_written[k] = byte_written[k+1];
                        }
                        served_clients++;
                    }

                }
        }
        sendack(out[1], NULL, filepath);
        unlink(filename);
        exit(0);
    }
    // for the parent
    else {
        // close output part of the pipe and input part of the other
        close(out[1]);
        close(in[0]);

        // create a new entry in the child processes table
        fileprocesses[nb_file_processes] = malloc(sizeof(struct _fproc));
        fileprocesses[nb_file_processes]->in = out[0];
        fileprocesses[nb_file_processes]->out = in[1];
        fileprocesses[nb_file_processes]->sender = p;
        fileprocesses[nb_file_processes]->pid = childpid;
        nb_file_processes++;
        free(filepath);

        return;
    }
}

// Register as a game master
void register_game_master(player p) {
    // Not in a game
    if(!p->game) {
        senderror(p->fd, CMD_REGISTER_GM, ERR_NOT_IN_GAME);
        return;
    }

    // can't register as Game Master if there is already one
    if(p->game->game_master) {
        senderror(p->fd, CMD_REGISTER_GM, ERR_ALREADY_A_GM);
        return;
    }
    
    p->game->game_master = p;
}

// Send a message to the game master
void send_to_game_master(player p, char * buffer) {
    // Not in a game
    if(!p->game) {
        senderror(p->fd, CMD_SEND_GM, ERR_NOT_IN_GAME);
        return;
    }

    // No game master in this game
    if(!p->game->game_master) {
        senderror(p->fd, CMD_SEND_GM, ERR_NO_GM);
        return;
    }

    char * msg = malloc((strlen(buffer+4)+strlen(p->name)+7)*sizeof(char));
    sprintf(msg, "%s#%s#%s\n", CMD_SEND_GM, p->name, buffer+4);
    sendline(p->game->game_master->fd, msg);
    free(msg);
}

// Send message to a player
void send_to_player(player p, char * buffer) {
    int j;
    
    // Not in a game
    if(!p->game) {
        senderror(p->fd, CMD_SEND_TO_PLAYER, ERR_NOT_IN_GAME);
        return;
    }

    // get the name of the receiver
    char * pointer = strchr(buffer+4, '#');
    if(strlen(buffer+4) < 3 || !pointer) {
        senderror(p->fd, NULL, ERR_NOT_RECOGNIZED);
        return;
    }
    *pointer = 0;

    for(j=0; j<p->game->nb_players; j++)
        if(strcmp(p->game->players[j]->name, buffer+4) == 0) {
            char * msg = malloc((strlen(pointer+1)+strlen(p->name)+7)*sizeof(char));
            sprintf(msg, "%s#%s#%s\n", CMD_SEND_TO_PLAYER, p->name, pointer+1);
            sendline(p->game->players[j]->fd, msg);
            free(msg);
            return;
        }

    senderror(p->fd, CMD_SEND_TO_PLAYER, ERR_NO_SUCH_PLAYER);
}

// get request from client socket
void parseClientRequest(player p, char * buffer) {

    // socket closed
    if(readline(p->fd, buffer, MAXBUF-1) == 0)
        removeClient(p);

    else if(strlen(buffer) > 3 && buffer[3] == '#') { // new message
#ifdef DEBUG
        printf("\r[DEBUG] : %s\n", buffer);
#endif
        buffer[3] = 0;

        // broadcast message
        if(strcmp(buffer, CMD_MESSAGE) == 0)
            broadcastMessage(p, buffer);

        // broadcast action
        else if(strcmp(buffer, CMD_ACTION) == 0) {
            printf("test\n");
            broadcastMessage(p, buffer);
        }

        // bind to a game
        else if(strlen(buffer+4) > 6 && buffer[9] == '#' && strcmp(buffer, CMD_BIND) == 0)
            bindToGame(p, buffer);

        // list existing games
        else if(strcmp(buffer, CMD_LIST) == 0)
            listGames(p);

        // Get available id to creage a game
        else if(strcmp(buffer, CMD_CREATE) == 0)
            getAvailableId(p);

        // Create new socket to send a file
        else if(strlen(buffer+4) > 7 && buffer[10] == '#' && strcmp(buffer, CMD_FILE) == 0)
            transfer_file(p, buffer);

        // Register as a Game Master
        else if(strcmp(buffer, CMD_REGISTER_GM) == 0)
            register_game_master(p);

        // Send a message to game master
        else if(strcmp(buffer, CMD_SEND_GM) == 0)
            send_to_game_master(p, buffer);

        // Send a message to a specific player
        else if(strcmp(buffer, CMD_SEND_TO_PLAYER) == 0)
            send_to_player(p, buffer);

        else
            senderror(p->fd, NULL, ERR_NOT_RECOGNIZED);
    }
    else {
#ifdef DEBUG
        printf("\r[DEBUG] : %s\n", buffer);
#endif
        senderror(p->fd, NULL, ERR_NOT_RECOGNIZED);
    }
}

// remove the reference to a child process
void removeChildProcess(int i) {
    int j;
    printf("\rFile process initiated by player %s is now closing (%d remaining)\n", fileprocesses[i]->sender->name, nb_file_processes-1);
    close(fileprocesses[i]->in);
    close(fileprocesses[i]->out);
    free(fileprocesses[i]);
    for(j=i;j<nb_file_processes-1; j++)
        fileprocesses[j] = fileprocesses[j+1];
    nb_file_processes--;
}

// Parse the answers of the child processes transferring files
void parseFileProcessesRequest(int i, char * buffer) {
    // if the fd closed
    if(readline(fileprocesses[i]->in, buffer, MAXBUF-1) == 0) {
        printf("\rChild process MIA...\n");
        senderror(fileprocesses[i]->sender->fd, CMD_FILE, ERR_INTERNAL);
        removeChildProcess(i);
        return;
    }

    if(buffer[3] != '#') {
        printf("\rChild process is sending strange commands... Better check that, bro.\n");
        senderror(fileprocesses[i]->sender->fd, CMD_FILE, ERR_INTERNAL);
        removeChildProcess(i);
        return;
    }

    buffer[3] = 0;
    if(strcmp(buffer, CMD_ERR) == 0) {
        printf("\rChild process failed with error: %s\n", buffer+4);
        senderror(fileprocesses[i]->sender->fd, CMD_FILE, ERR_INTERNAL);
        removeChildProcess(i);
        return;
    }

    if(strcmp(buffer, CMD_PORT) == 0 && buffer[9] == '#') {
        char * msg = malloc(sizeof(char)*(6+strlen(buffer+4)));
        buffer[9] = 0;
        printf("\rChild process waiting on port %s\n", buffer+4);
        sprintf(msg, "%s#%s#%s\n", CMD_PORT, buffer+4, buffer+10);
        sendline(fileprocesses[i]->sender->fd, msg);
        free(msg);
        return;
    }

    if(strcmp(buffer, CMD_READY) == 0) {
        printf("\rFile successfully uploadeded !\n");
        int j;
        char * msg = malloc(sizeof(char)*(6+strlen(buffer+4)));
        sprintf(msg, "%s#%s\n", CMD_FILE, buffer+4);
        for(j=0; j<fileprocesses[i]->sender->game->nb_players; j++)
            if(fileprocesses[i]->sender->game->players[j] != fileprocesses[i]->sender)
                sendline(fileprocesses[i]->sender->game->players[j]->fd, msg);
        sprintf(msg, "%05d", fileprocesses[i]->sender->game->nb_players);
        sendack(fileprocesses[i]->out, NULL, msg);
        free(msg);
        return;
    }

    if(strcmp(buffer, CMD_ACK) == 0) {
        printf("\rFile successfully broadcasted !\n");
        sendack(fileprocesses[i]->sender->fd, CMD_FILE, buffer+4);
        removeChildProcess(i);
        return;
    }
}

// clean memory
void clean(int sock_serv) {
    int i;

    // close sockets
    close(sock_serv);
    for(i = 0 ; i < nb_client; i ++) {
        close(players[i]->fd);
        printf("Socket n°: %d is now closed.\n", players[i]->fd);
        free(players[i]);
    }

    // free games
    for(i=0; i<nb_game; i++)
        free(games[i]);

    // free frpocs
    for(i=0; i<nb_file_processes; i++) {
        close(fileprocesses[i]->in);
        close(fileprocesses[i]->out);
        free(fileprocesses[i]);
    }
}

// main loop
int main(int argc, char *argv[]) {
    char      buffer[MAXBUF];       // character buffer used to receive messages
    int i;                          // iterator

    printf("\n");
    printf("          *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
    printf("          |                                       |\n");
    printf("          *     Warhammer On The Road             *\n");
    printf("          |                                       |\n");
    printf("          *                         Server        *\n");
    printf("          |                                       |\n");
    printf("          *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
    printf("\n");

    int sock_serv = init(argc, argv);

    /*  Enter an infinite loop to respond
        to client requests and echo input  */
    while(1) {
        printf("\r            ");
        printf("\r> ");
        fflush(stdout);

        fd_set rfds;
        waitForActivity(&rfds, sock_serv);

        // Something happened in stdin
        if(FD_ISSET(fileno(stdin), &rfds)) {
            fgets(buffer, sizeof(buffer), stdin);
            if(parseStdin(buffer) < 0)
                break;
        }

        // If something happened on the listening socket
        if(FD_ISSET(sock_serv, &rfds))
            acceptNewSocket(sock_serv);

        // if something happened on a client socket
        for(i = 0 ; i < nb_client ; i ++)
            if(FD_ISSET(players[i]->fd, &rfds))
                parseClientRequest(players[i], buffer);

        // if a child process tries to communicate
        for(i=0; i<nb_file_processes; i++)
            if(FD_ISSET(fileprocesses[i]->in, &rfds))
                parseFileProcessesRequest(i, buffer);
    }

    printf("\r");

    clean(sock_serv);

    return 0;
}
