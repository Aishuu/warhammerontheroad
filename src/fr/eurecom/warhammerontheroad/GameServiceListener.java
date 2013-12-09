package fr.eurecom.warhammerontheroad;

import java.util.ArrayList;

public interface GameServiceListener {
	public void onStateChanged(Game game);
	public void userDisconnected(String name);
	public void userConnected(String name);
	public void listAvailableGames(ArrayList<String> avail);
}
