package fr.eurecom.warhammerontheroad.network;

/**
 * Wrapper for constants used to communicate with the server
 * 
 * @author aishuu
 *
 */
public final class Cmds {
	private Cmds(){}

	/**
	 * Returned when the command failed
	 */
	public static final String CMD_ERR = 			"ERR";
	/**
	 * Returned when a command was successfull
	 */
	public static final String CMD_ACK = 			"ACK";
	/**
	 * Sent by the server when a player has been disconnected
	 */
	public static final String CMD_DISCONNECTED = 	"DSC";
	/**
	 * Sent by the server when a player has joined the game
	 */
	public static final String CMD_CONNECTED = 		"CNT";
	/**
	 * Sent to simply broadcast a message
	 */
	public static final String CMD_MESSAGE = 		"MSG";
	/**
	 * Sent to bind to a game
	 */
	public static final String CMD_BIND = 			"BND";
	/**
	 * Sent to create a game
	 */
	public static final String CMD_CREATE_GAME = 	"CRG";
	/**
	 * Sent to list the existing games
	 */
	public static final String CMD_LIST = 			"LST";
	/**
	 * Sent to get an available id for a game (doesn't actually create it)
	 */
	public static final String CMD_CREATE =			"CRT";
	/**
	 * Sent by the client to ask for a port to transfer a file or by the server to tell the client a file is available
	 */
	public static final String CMD_FILE =		 	"FLE";
	/**
	 * Sent by the server to give a port as an answer to a CMD_FILE command
	 */
	public static final String CMD_PORT =		 	"PRT";
	/**
	 * Register as the Game Master
	 */
	public static final String CMD_REGISTER_GM =	"BGM";
	/**
	 * Send a message to the Game Master
	 */
	public static final String CMD_SEND_GM =		"SGM";
	/**
	 * Send a message to a player
	 */
	public static final String CMD_SEND_TO_PLAYER =	"STP";
	/**
	 * Broadcast an action
	 */
	public static final String CMD_ACTION =			"ACT";
}
