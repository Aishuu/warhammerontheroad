package fr.eurecom.warhammerontheroad;

public interface ChatListener {
	public void messageReceived(String name, String message);
	public void userDisconnected(String name);
	public void userConnected(String name);
}
