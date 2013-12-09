#include <sys/socket.h>
#include <sys/types.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/select.h>
#include <sys/time.h>
#include <errno.h>


#define BACKLOG         1024
#define MAXBUF          1024            // Can't receiver message longer than 1023 characters (including the CMD)
#define PORT            33000
#define MAX_PLAYER      50
#define MAX_GAME        10
#define MAX_NAME_SIZE   15
#define MAX_PLAYER_GAME 5
#define MAX_SEND        ( MAXBUF+1+MAX_NAME_SIZE )  // ensure we can broadcast a message shorter than MAXBUF
#define QUIT_MESSAGE    "quit\n"

ssize_t sendline(int fd, const void *str);
ssize_t readline(int fd,  void *str, size_t maxlen);

typedef struct _player * player;
typedef struct _game * game;

struct _player {
    int fd;
    char name[MAX_NAME_SIZE];
    game game;
};

struct _game {
    player players[MAX_PLAYER_GAME];
    int nb_players;
    int id;
};

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

int main(int argc, char *argv[]) {
    int       sock_serv;             // listening socket  
    player    players[MAX_PLAYER];            // connection socket 
    short int port;                  // port number       
    struct    sockaddr_in servaddr;  // socket address struct 
    struct 	  sockaddr_in csin;
    socklen_t recsize = sizeof(csin);
    char      buffer[MAXBUF];        // character buffer         
    fd_set    rfds;    		         // Reading descriptor
    int fd_max;	
    int nb_client = 0;
    int nb_game = 0;
    int activity;
    game     games[MAX_GAME];
    int i, j, k;

    // clear sockfd table
    for(i = 0; i < MAX_PLAYER; i++)
        players[i] = NULL;
    for(i = 0; i < MAX_GAME; i++)
        games[i] = NULL;


    /*  Get port number from the command line, and
        set to default port if no arguments were supplied  
        */
    if(1 == argc)
        port = PORT;
    else if(2 == argc)
        port = strtol(argv[1], NULL, 0);
    else {
        fprintf(stderr, "Usage: \" %s [port] \"\n\t port : port to use for the server.\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    /*  Create the listening socket  */
    sock_serv = socket(AF_INET, SOCK_STREAM, 0);
    // enable to reuse addr right after we stopped the server
    int optval = 1;
    setsockopt(sock_serv, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof optval);

    printf("La socket %d est maintenant ouverte en mode TCP/IP\n", sock_serv);
    if (sock_serv == -1) {
        perror("socket");
        exit(EXIT_FAILURE);
    }

    /*  Set all bytes in socket address structure to
        zero, and fill in the relevant data members   */
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(port);
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);

    /*  Bind our socket addresss to the 
        listening socket, and call listen()  */
    if (bind(sock_serv, (struct sockaddr *) &servaddr, sizeof(servaddr)) == -1) {
        perror("bind");
        exit(EXIT_FAILURE);
    }
    if (listen(sock_serv, BACKLOG) == -1) {
        perror("listen");
        exit(EXIT_FAILURE);
    }

    printf("\n");

    /*  Enter an infinite loop to respond
        to client requests and echo input  */
    while(1) {
        printf("\r            ");
        printf("\r>");
        fflush(stdout);

        //clear the socket set
        FD_ZERO(&rfds);

        //Add master socket to the set
        FD_SET(sock_serv, &rfds);
        fd_max = sock_serv;

        // Monitor stdin
        FD_SET(fileno(stdin), &rfds);

        for(i = 0 ; i < nb_client ; i++)
            if(players[i] != NULL) {
                FD_SET(players[i]->fd, &rfds);
                if(players[i]->fd > fd_max)			
                    fd_max = players[i]->fd;
            }

        // Wait for activity
        activity = select(fd_max + 1, &rfds, NULL, NULL, NULL);

        if(activity < 0)
            printf("select error\n");

        if(FD_ISSET(fileno(stdin), &rfds)) {
            fgets(buffer, sizeof(buffer), stdin);
            if(strcmp(buffer,QUIT_MESSAGE) == 0)
                break;
        }

        // If something happened on the listening socket
        if(FD_ISSET(sock_serv, &rfds)) {
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
                nb_client ++;
                printf("--- New connection, socket n° : %d (total : %d) ---\n", new_socket, nb_client);
            }
            else {
                // TODO: if there are too many players, fire a player that is not yet registered (the first in the tab)
                sendline(new_socket, "ERR#Too many players !\n");
                close(new_socket);
                printf("--- A client has been rejected ! ---\n");
            }

        }

        for(i = 0 ; i < nb_client ; i ++) {
            int sd = players[i]->fd;

            // if something happened on a client socket
            if(!FD_ISSET(sd, &rfds))
                continue;

            if(readline(sd, buffer, MAXBUF-1) == 0) {
                printf("---       Socket closed : %d (total : %d)       ---\n", sd, nb_client-1);
                close(sd);
                game g;
                if((g = players[i]->game)) {
                    char * msg = malloc((strlen(players[i]->name)+6)*sizeof(char));
                    strcpy(msg, "DSC#");
                    strcat(msg, players[i]->name);
                    strcat(msg, "\n");
                    for(j=0; j<g->nb_players; j++)
                        if(g->players[j] == players[i]) {
                            for(k=j; k<g->nb_players-1;k++)
                                g->players[k] = g->players[k+1];
                            break;
                        }
                        else
                            sendline(g->players[j]->fd, msg);
                    free(msg);
                    g->nb_players--;
                    if(g->nb_players == 0) {
                        printf("---   No more players in game %d : game deleted   ---\n", g->id);
                        for(j=0; j<nb_game; j++)
                            if(g == games[j]) {
                                for(k=j; k<nb_game-1; k++)
                                    games[k] = games[k+1];
                                break;
                            }
                        free(g);
                    }
                }
                free(players[i]);
                nb_client --;
                for(k=j; k<nb_client-1; k++)
                    players[k] = players[k+1];
            }
            else {
                /* Les messages sont sous forme
                 * MSG#le message
                 * ou
                 * BND#12345#name
                 * ou
                 * LST#
                 * ou
                 * CRT#name
                 */
                printf("message : \"%s\"\n", buffer);
                if(buffer[3] == '#') { // nouveau message
                    buffer[3] = 0;
                    game g;
                    if(strcmp(buffer, "MSG") == 0 && (g = players[i]->game)) { // broadcast message
                        char * msg = malloc((strlen(buffer+4)+strlen(players[i]->name)+7)*sizeof(char));
                        strcpy(msg, "MSG#");
                        strcat(msg, players[i]->name);
                        strcat(msg, "#");
                        strcat(msg, buffer+4);
                        strcat(msg, "\n");
                        for(j=0; j<g->nb_players; j++)
                            if ( g->players[j] != players[i] )
                                sendline(g->players[j]->fd, msg);
                        free(msg);
                    }
                    else if(buffer[9] == '#' && strcmp(buffer, "BND") == 0) {
                        buffer[9] = 0;

                        int id = atoi(buffer+4);
                        game g = NULL;
                        for(j=0; j<nb_game; j++)
                            if(games[j]->id == id) {
                                g = games[j];
                                break;
                            }

                        if(!g) {
                            if(nb_game >= MAX_GAME) {
                                printf("--- Can't create a new game ! ---\n");
                                sendline(sd, "ERR#Can't create a new game !");
                            }
                            else {    
                                printf("--- New game created by %s ---\n", buffer+10);
                                games[nb_game] = g = malloc(sizeof(struct _game));
                                g->nb_players = 0;
                                g->id = id;
                                nb_game++;
                            }
                        }

                        strncpy(players[i]->name, buffer+10, MAX_NAME_SIZE);
                        if(g) {
                            if(g->nb_players >= MAX_PLAYER_GAME)
                                sendline(sd, "ERR#Too many players in this game !");
                            else {
                                char * msg = malloc(sizeof(char)*(6+strlen(players[i]->name)));
                                sprintf(msg, "CNT#%s\n", players[i]->name);
                                for(j=0;j<g->nb_players; j++)
                                    sendline(sd, msg);
                                free(msg);
                                players[i]->game = g;
                                g->players[g->nb_players] = players[i];
                                g->nb_players++;
                            }
                        }

                    }
                    else if(strcmp(buffer, "LST") == 0) {
                        char * msg = malloc(sizeof(char)*(4+nb_game*(5+1+MAX_NAME_SIZE)+1));
                        sprintf(msg, "ALT");
                        for(j=0;j<nb_game;j++) {
                            char tmp[6];
                            sprintf(tmp, "#%05d", games[j]->id);
                            strcat(msg, tmp);
                            if(games[j]->nb_players != 0)
                                strcat(msg, games[j]->players[0]->name);
                        }
                        strcat(msg, "\n");
                        sendline(sd, msg);
                        free(msg);
                    }
                    else if(strcmp(buffer, "CRT") == 0) {
                        for(k=1;k<100000;k++) {
                            int exist = 0;
                            for(j=0;j<nb_game; j++)
                                if(games[j]->id == k) {
                                    exist = 1;
                                    break;
                                }
                            if(!exist)
                                break;
                        }
                        char msg[11];
                        sprintf(msg, "ACT#%05d\n", k);
                        sendline(sd, msg);
                    }
                    else
                        sendline(sd, "ERR#Command not recognized...\n");
                }
                else
                    sendline(sd, "ERR#Command not recognized...\n");
            }
        }
    }


    close(sock_serv);
    for(j = 0 ; j < nb_client; j ++) {
        close(players[j]->fd);
        printf("La socket n°: %d est maintenant fermée.\n", players[j]->fd);
        free(players[j]);
    }
    for(j=0; j<nb_game; j++)
        free(games[j]);
    return 0;
}
