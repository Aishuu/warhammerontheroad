package fr.eurecom.warhammerontheroad.application;

/**
 * Interface used to register a class to get notifications about relevant events for the chat.
 * 
 * @author aishuu
 *
 */
public interface ChatListener {
	/**
	 * A message has been received
	 * 
	 * @param name name of the sender
	 * @param message content of the message
	 */
	public void messageReceived(String name, String message);
	
	/**
	 * A private message has been received
	 * 
	 * @param name name of the sender
	 * @param message content of the message
	 */
	public void privateMessageReceived(String name, String message);
	
	/**
	 * A user has been disconnected
	 * 
	 * @param name name of the user
	 */
	public void userDisconnected(String name);
	
	/**
	 * A new user has connected
	 * 
	 * @param name name of the user
	 */
	public void userConnected(String name);
	
	/**
	 * The status of the transfer of a file changed
	 * 
	 * @param name the name of the file
	 * @param file_status the status of the transfer
	 * 
	 * @see NetworkParser.FILE_SUCCESSFULLY_TRANSMITTED
	 * @see NetworkParser.FILE_DOES_NOT_EXIST
	 */
	public void fileTransferStatusChanged(String name, int file_status);
}