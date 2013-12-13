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
}
