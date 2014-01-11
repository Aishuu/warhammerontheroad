package fr.eurecom.warhammerontheroad.application;

import java.util.ArrayList;

import fr.eurecom.warhammerontheroad.model.Game;

public interface GameServiceListener {
	public void onStateChanged(Game game);
	public void userDisconnected(String name);
	public void userConnected(String name);
	public void listAvailableGames(ArrayList<String> avail);
	public void beginFight();
}
