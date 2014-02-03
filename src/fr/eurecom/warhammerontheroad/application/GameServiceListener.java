package fr.eurecom.warhammerontheroad.application;

import fr.eurecom.warhammerontheroad.model.Game;

public interface GameServiceListener {
	public void onStateChanged(Game game, int exState);
	//public void listAvailableGames(ArrayList<String> avail);
	public void prepareFight();
}
