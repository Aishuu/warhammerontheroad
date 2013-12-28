package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Collection;

import android.graphics.drawable.Drawable;
import android.util.Log;
import fr.eurecom.warhammerontheroad.application.GameServiceListener;

public class Game {
	public final static int STATE_NO_GAME				= 0;
	public final static int STATE_GAME_CREATE_WAIT 		= 1;
	public final static int STATE_GAME_CREATED			= 2;
	public final static int STATE_GAME_PERSO_CREATED 	= 3;
	public final static int STATE_GAME_LAUNCHED			= 4;
	public final static int STATE_GAME_WAIT_TURN		= 5;
	public final static int STATE_GAME_TURN				= 6;
	public final static int STATE_GAME_WAIT_ACTION		= 7;
	public final static int STATE_GAME_CONFIRM_ACTION	= 8;

	private final static String CMD_FIGHT				= "FGT";
	private final static String CMD_BEGIN_FIGHT			= "BFT";

	private final static String TAG						= "Game";

	private int state;
	private int id_game;
	private final Collection<GameServiceListener> gameServiceListeners = new ArrayList<GameServiceListener>();
	private ArrayList<Player> persos;
	private ArrayList<Hero> tmp_hero;
	private Player me;
	private boolean isGM;
	private boolean isInFight;
	private Map map;

	public Game() {
		this.state = STATE_NO_GAME;
		this.id_game = 0;
		this.isGM = false;
		this.me = null;
		this.persos = new ArrayList<Player>();
		this.tmp_hero = new ArrayList<Hero>();
		this.map = null;
	}

	public Player getPlayer() {
		return this.me;
	}

	public Player getPlayer(String name) {
		for(Player p : this.persos)
			if(p.getName() != null && p.getName().equals(name))
				return p;
		return null;
	}
	
	public Player getPlayer(int id) {
		for(Player p : this.persos)
			if(p.getHero().getId() == id)
				return p;
		return null;
	}

	public void setPlayer(Player player) {
		this.me = player;
		this.persos.add(player);
	}
	
	public void addPlayer(Player player) {
		this.persos.add(player);
	}
	
	public void removePlayer(Player player) {
		this.persos.remove(player);
	}
	
	public void removePlayer(String name) {
		Player p = this.getPlayer(name);
		if(p != null)
			removePlayer(p);
	}
	
	public void removeTmpHero(Hero h) {
		this.tmp_hero.remove(h);
	}
	
	public void removeTmpHero(int id) {
		Hero h = getTmpHero(id);
		if(h != null)
			removeTmpHero(h);
	}
	
	public Hero getTmpHero(int id) {
		for(Hero h : this.tmp_hero)
			if(h.getId() == id)
				return h;
		return null;
	}
	
	public Hero getHero(int id) {
		Player p = getPlayer(id);
		if(p != null)
			return p.getHero();
		return getTmpHero(id);
	}

	public void waitForId() {
		if(this.state == STATE_NO_GAME) 
			change_state(STATE_GAME_CREATE_WAIT);
		else
			Log.w(TAG, "Unexpected call to waitForId..");
	}

	public void bound(int id) {
		if(this.state == STATE_NO_GAME || this.state == STATE_GAME_CREATE_WAIT) {
			if(this.state == STATE_GAME_CREATE_WAIT)
				this.isGM = true;
			this.id_game = id;
			change_state(STATE_GAME_CREATED);
		}
		else
			Log.w(TAG, "Unexpected call to bound...");
	}

	public void validateCharaCreation() {
		if(!this.isGM && this.state == STATE_GAME_CREATED)
			change_state(STATE_GAME_PERSO_CREATED);
		else
			Log.w(TAG, "Unexpected call to validateCharaCreation...");
	}

	public void gameStarted() {
		if((this.isGM && this.state == STATE_GAME_CREATED) || (!this.isGM && this.state == STATE_GAME_PERSO_CREATED))
			change_state(STATE_GAME_LAUNCHED);
		else
			Log.w(TAG, "Unexpected call to gameStarted...");
	}

	public void waitForTurn() {
		if(!this.isGM && (this.state == STATE_GAME_TURN || this.state == STATE_GAME_LAUNCHED)) {
			if(this.state == STATE_GAME_LAUNCHED)
				this.isInFight = true;
			change_state(STATE_GAME_WAIT_TURN);
		}
		else
			Log.w(TAG, "Unexpected call to waitForTurn...");
	}

	public void myTurnNow() {
		if(!this.isGM && this.state == STATE_GAME_WAIT_TURN)
			change_state(STATE_GAME_TURN);
		else
			Log.w(TAG, "Unexpected call to myTurnNow...");
	}

	public void waitForAction() {
		if(this.isGM && (this.state == STATE_GAME_LAUNCHED || this.state == STATE_GAME_CONFIRM_ACTION)) {
			if(this.state == STATE_GAME_LAUNCHED)
				this.isInFight = true;
			change_state(STATE_GAME_WAIT_ACTION);
		}
		else
			Log.w(TAG, "Unexpected call to waitForAction...");
	}

	public void waitConfirmation() {
		if(this.isGM && this.state == STATE_GAME_CONFIRM_ACTION)
			change_state(STATE_GAME_WAIT_ACTION);
		else
			Log.w(TAG, "Unexpected call to waitConfirmation...");
	}

	public void endFight() {
		if((!this.isGM && this.state == STATE_GAME_WAIT_TURN) || (this.isGM && this.state == STATE_GAME_CONFIRM_ACTION)) {
			this.isInFight = false;
			change_state(STATE_GAME_LAUNCHED);
		}
		else
			Log.w(TAG, "Unexpected call to endFight...");
	}

	public void parseGMCommand(String name, String msg) {
		// TODO: parse the commands. For ack, set timer, show notif to gm
	}

	public void parseCommand(String name, String msg) {
		String parts[] = msg.split("#", 2);
		if(parts.length < 2)
			return;

		if(parts[0].equals(CMD_FIGHT) && this.isInFight) {
			Player p = this.getPlayer(name);
			if(p != null)
				p.getHero().parseCommand(this, parts[1]);
		}
		
		else if(parts[0].equals(CMD_BEGIN_FIGHT) && !this.isInFight) {
			parts = parts[1].split("#", -1);
			if(parts.length < 3)
				return;
			try {
				final int maxX = Integer.parseInt(parts[0]);
				final int maxY = Integer.parseInt(parts[1]);
				//final String imagePath = parts[2];
				// TODO: create Drawable from imagePath in another thread
				Drawable d = null;
				this.map = new Map(maxX, maxY, d);
				waitForTurn();
			} catch(NumberFormatException e) {
				Log.e(TAG, "Not a number !");
			}
		}
	}

	private void change_state(int state) {
		this.state = state;
		fireStateChanged();
	}
	
	public Map getMap() {
		return this.map;
	}

	public int getState() {
		return this.state;
	}

	public int getIdGame() {
		return this.id_game;
	}

	public boolean mustBind() {
		return (this.state != STATE_NO_GAME && this.state != STATE_GAME_CREATE_WAIT);
	}

	public boolean isGM() {
		return this.isGM;
	}

	public boolean isInFight() {
		return this.isInFight;
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
