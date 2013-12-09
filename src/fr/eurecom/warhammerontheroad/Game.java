package fr.eurecom.warhammerontheroad;

import java.util.ArrayList;
import java.util.Collection;

public class Game {
	public final static int STATE_NO_GAME			= 0;
	public final static int STATE_GAME_CREATE_WAIT 	= 1;
	public final static int STATE_GAME_CREATED		= 2;
	public final static int STATE_GAME_LAUNCHED		= 3;

	private int state;
	private int id_game;
    private final Collection<GameServiceListener> gameServiceListeners = new ArrayList<GameServiceListener>();
	
	public Game() {
		this.state = STATE_NO_GAME;
		this.id_game = 0;
	}
	
	public void setIdGame(int id_game) {
		this.id_game = id_game;
		change_state(STATE_GAME_CREATED);
	}
	
	public void change_state(int state) {
		this.state = state;
		fireStateChanged();
	}
	
	public int getState() {
		return this.state;
	}
	
	public int getIdGame() {
		return this.id_game;
	}
	
	public void listAvailableGames(ArrayList<String> avail) {
    	for(GameServiceListener l: gameServiceListeners)
    		l.listAvailableGames(avail);
	}

	public void userDisconnected(String name) {
    	for(GameServiceListener l: gameServiceListeners)
    		l.userDisconnected(name);
	}

	public void userConnected(String name) {
    	for(GameServiceListener l: gameServiceListeners)
    		l.userConnected(name);
	}
	
	private void fireStateChanged() {
    	for(GameServiceListener l: gameServiceListeners)
    		l.onStateChanged(this);
	}

    public void addGameServiceListener(GameServiceListener listener) {
        gameServiceListeners.add(listener);
    }
    
    public void removeGameServiceListener(GameServiceListener listener) {
        gameServiceListeners.remove(listener);
    }
	
}
