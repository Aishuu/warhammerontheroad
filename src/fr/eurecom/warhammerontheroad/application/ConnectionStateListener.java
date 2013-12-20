package fr.eurecom.warhammerontheroad.application;

public interface ConnectionStateListener {
	public void onConnectionLost();
	public void onConnectionRetrieved();
}
