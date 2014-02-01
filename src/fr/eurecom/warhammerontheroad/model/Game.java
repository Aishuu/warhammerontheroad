package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.application.GameServiceListener;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

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

	public final static String CMD_FIGHT				= "FGT";
	public final static String CMD_BEGIN_FIGHT			= "BFT";
	public final static String CMD_START_GAME			= "SGM";
	public final static String CMD_CREATE_HERO			= "HER";
	public final static String CMD_INITIATIVE			= "INI";
	public final static String CMD_TURN					= "TRN";

	private final static String TAG						= "Game";

	private int state;
	private int id_game;
	private final Collection<GameServiceListener> gameServiceListeners = new ArrayList<GameServiceListener>();
	private ArrayList<Hero> heros;
	private Player me;
	private boolean isGM;
	private boolean isInFight;
	private int turnOf;
	private int cmp_init;
	private Map map;
	private WotrService mService;

	public Game(WotrService mService) {
		this.mService = mService;
		this.state = STATE_NO_GAME;
		this.id_game = 0;
		this.isGM = false;
		this.me = null;
		this.heros = new ArrayList<Hero>();
		this.map = null;
	}

	public Player getMe() {
		return this.me;
	}

	public Player getPlayer(String name) {
		for(Hero h : this.heros)
			if(h instanceof Player) {
				Player p = (Player) h;
				if(p.getName().equals(name))
					return p;
			}
		return null;
	}

	public void setMe(Player player) {
		this.me = player;
		this.heros.add(player);
	}

	public void addHero(Hero hero) {
		this.heros.add(hero);
	}

	public void removeHero(Hero hero) {
		this.heros.remove(hero);
	}

	public void removePlayer(String name) {
		Player p = this.getPlayer(name);
		if(p != null)
			removeHero(p);
	}

	public Hero getHero(int id) {
		if(id <= 0)
			return null;
		for(Hero h : this.heros)
			if(h.getId() == id)
				return h;
		return null;
	}

	public Hero getHero(String s) {
		Player p = getPlayer(s);
		if(p != null)
			return p;
		try {
			int id = Integer.parseInt(s);
			return getHero(id);
		} catch(NumberFormatException e) {
			return null;
		}
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
				startFight();
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
				this.startFight();
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

	private void startFight() {
		this.isInFight = true;
		fireBeginFight();
	}

	public void parseGMCommand(String name, String msg) {
		// TODO: parse the commands. For ack, set timer, show notif to gm
	}

	public void parseCommand(String name, String msg) {
		if(name.equals(WotrService.GM_NAME)) {
			String[] msg_split = msg.split(NetworkParser.SEPARATOR, 2);
			if(msg_split.length < 2)
				return;
			name = msg_split[0];
			msg = msg_split[1];

		}

		if(msg.equals(CMD_START_GAME)) {
			this.gameStarted();
		}

		String parts[] = msg.split(NetworkParser.SEPARATOR, 2);
		if(parts.length < 2)
			return;

		if(parts[0].equals(CMD_FIGHT) && this.isInFight) {
			Hero h = this.getHero(name);
			if(h != null)
				h.parseCommand(this, parts[1]);
		}

		else if(parts[0].equals(CMD_TURN) && this.isInFight) {
			try {
				int turn = Integer.parseInt(parts[1]);
				this.turnOf = turn;
				if(this.isGM) {
					for(Hero h_tmp: this.heros)
						if(!(h_tmp instanceof Player) && h_tmp.getTurnInFight() == turn) {
							this.turnInFight(h_tmp);
							break;
						}
				}
				else
					if(this.me.getTurnInFight() == turn)
						turnInFight(this.me);
			}
			catch(NumberFormatException e) {
				Log.e(TAG, "Turn should be a number... "+msg);
			}
		}

		else if(parts[0].equals(CMD_INITIATIVE) && this.isInFight) {
			Hero h = this.getHero(name);
			try{
				Log.d(TAG, "debug : received init : "+msg);
				int init = Integer.parseInt(parts[1]);
				if(h != null) {
					if(this.isGM) {
						for(Hero h_tmp: this.heros)
							if(!(h_tmp instanceof Player))
								h_tmp.computeTurnInFight(h, init);
					}
					else
						this.me.computeTurnInFight(h, init);
					this.cmp_init ++;
					if(this.cmp_init >= this.heros.size()-1) {
						if(this.isGM) {
							for(Hero h_tmp: this.heros)
								if(!(h_tmp instanceof Player) && h_tmp.getTurnInFight() == 0) {
									this.turnInFight(h_tmp);
									break;
								}
						}
						else
							if(this.me.getTurnInFight() == 0)
								turnInFight(this.me);
					}
				}
			}
			catch(NumberFormatException e) {
				Log.e(TAG, "Initiative must be a number... "+msg);
			}
		}

		else if(parts[0].equals(CMD_BEGIN_FIGHT) && !this.isInFight) {
			if(parts[1].split(NetworkParser.SEPARATOR, -1).length < 3)
				return;
			try {
				this.map = new Map(this.mService, parts[1]);
				waitForTurn();
				this.cmp_init = 0;
				this.turnOf = 0;
				int init = this.me.beginBattle();
				this.mService.getNetworkParser().sendInitiative(init);
			} catch(NumberFormatException e) {
				Log.e(TAG, "Not a number !");
			}
		}

		else if(parts[0].equals(CMD_CREATE_HERO)) {
			try {
				int id = Integer.parseInt(name);
				Hero h = this.getHero(id);
				if(h == null)
					h = this.createHeroWithId(this.mService.getContext(), id);
				h.constructFromString(this.mService, parts[1]);
				h.show();
			} catch(NumberFormatException e) {
				Player p = this.getPlayer(name);
				if(p == null)
					p = new Player(this.mService.getContext(), name);
				p.constructFromString(this.mService, parts[1]);
				this.addHero(p);
			}
		}
	}

	public void sendNotifStartFight(Map m) {
		if(!this.isGM)
			return;
		waitForAction();
		this.mService.getNetworkParser().beginFight(m);
		for(Hero h: this.heros)
			if(!(h instanceof Player))
				this.mService.getNetworkParser().sendInitiative(h.beginBattle(), h);
	}

	private void turnInFight(Hero h) {
		if(!this.isGM)
			this.myTurnNow();
		Log.d(TAG, "Yeah ! my turn ("+h.representInString()+") !");
		// TODO: do stuff here
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Can't wait...");
		}
		this.turnNext();
	}

	private void turnNext() {
		this.turnOf = (this.turnOf+1)%this.heros.size();
		this.mService.getNetworkParser().nextTurn(this.turnOf, this.isGM);
		
		if(!this.isGM)
			this.waitForTurn();
	}

	private void change_state(int state) {
		this.state = state;
		fireStateChanged();
	}

	public Map getMap() {
		return this.map;
	}

	public void setMap(Map m) {
		this.map = m;
	}

	public int getState() {
		return this.state;
	}

	public int getIdGame() {
		return this.id_game;
	}

	public boolean mustBind() {
		Log.d(TAG, "State : "+this.state);
		return (this.state != STATE_NO_GAME && this.state != STATE_GAME_CREATE_WAIT);
	}

	public Hero createHeroWithId(Context context, int n) {
		Hero h = new Hero(context);
		h.setId(n);
		this.addHero(h);
		return h;
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

	private void fireBeginFight() {
		for(GameServiceListener l: gameServiceListeners)
			l.beginFight();
	}

	public void addGameServiceListener(GameServiceListener listener) {
		gameServiceListeners.add(listener);
	}

	public void removeGameServiceListener(GameServiceListener listener) {
		gameServiceListeners.remove(listener);
	}

}
