#ifndef SERVER_H_
#define SERVER_H_

#define MAXBUF              1024                        // Can't receive message longer than 1023 characters (including the CMD)
#define PORT                33000                       // Default port
#define MAX_PLAYER          50                          // Max simultaneously connected players
#define BACKLOG             ( MAX_PLAYER + 5 )          // Number of connections allowed
#define MAX_GAME            10                          // Max simultaneous games
#define MAX_NAME_SIZE       15                          // Max size of the name of a player
#define MAX_PLAYER_GAME     5                           // Max number of player per game
#define MAX_FILE_TRANSFER   5                           // Max simultaneous file transfers
#define MAX_FILE_SIZE       999999                      // Max size in bytes of transfered files
#define MAX_SEND            ( MAXBUF+1+MAX_NAME_SIZE )  // Ensure we can broadcast a message shorter than MAXBUF
#define TIMEOUT_FILE        15                          // Timeout (in seconds) to make the child process close when the sender is not sending anything
#define QUIT_MESSAGE        "quit\n"                    // Command to send to server to stop

// List or recognized commands
#define CMD_ERR             "ERR"                       // Error
#define CMD_ACK             "ACK"                       // Ok
#define CMD_DISCONNECTED    "DSC"                       // A player got disconnected
#define CMD_CONNECTED       "CNT"                       // A player is now connected
#define CMD_MESSAGE         "MSG"                       // Broadcast a message
#define CMD_BIND            "BND"                       // Bind to a game
#define CMD_CREATE_GAME     "CRG"                       // Create a game
#define CMD_LIST            "LST"                       // List existing games
#define CMD_CREATE          "CRT"                       // Ask for an available id
#define CMD_FILE            "FLE"                       // Want to send a file
#define CMD_PORT            "PRT"                       // File ready to be transmitted on this port
#define CMD_READY           "RDY"                       // Child process is ready to broadcast a file
#define CMD_REGISTER_GM     "BGM"                       // register as a game master
#define CMD_SEND_GM         "SGM"                       // Send a message to the game master
#define CMD_SEND_TO_PLAYER  "STP"                       // Send a message to a player
#define CMD_ACTION          "ACT"                       // Broadcast an action

#define DEBUG

typedef struct _player * player;
typedef struct _game * game;
typedef struct _fproc * fproc;

// Hold informations about a player
struct _player {
    int fd;
    char name[MAX_NAME_SIZE];
    game game;
};

// Hold informations about a game
struct _game {
    player players[MAX_PLAYER_GAME];
    player game_master;
    int nb_players;
    int id;
};

// Hold informations about a file transfer process
struct _fproc {
    int in;
    int out;
    player sender;
    int pid;
};

#endif      // server.h
