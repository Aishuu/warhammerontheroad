package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.application.CharaCreationDetailsActivity;
import fr.eurecom.warhammerontheroad.application.ChatRoomActivity;
import fr.eurecom.warhammerontheroad.application.CombatActivity;
import fr.eurecom.warhammerontheroad.application.CombatView;
import fr.eurecom.warhammerontheroad.application.CreateMapActivity;
import fr.eurecom.warhammerontheroad.application.CreateSupportCharaActivity;
import fr.eurecom.warhammerontheroad.application.GMMenuActivity;
import fr.eurecom.warhammerontheroad.application.GameServiceListener;
import fr.eurecom.warhammerontheroad.application.JoinGameActivity;
import fr.eurecom.warhammerontheroad.application.NewGameIntroActivity;
import fr.eurecom.warhammerontheroad.application.PlayerMenuActivity;
import fr.eurecom.warhammerontheroad.application.SeeCharaDataActivity;
import fr.eurecom.warhammerontheroad.application.SeeEnemyStatsActivity;
import fr.eurecom.warhammerontheroad.application.SeeStatsActivity;
import fr.eurecom.warhammerontheroad.application.StartMenuActivity;
import fr.eurecom.warhammerontheroad.application.StatsObjectActivity;
import fr.eurecom.warhammerontheroad.application.StatsProfilActivity;
import fr.eurecom.warhammerontheroad.application.StatsSkillActivity;
import fr.eurecom.warhammerontheroad.application.WotrActivity;
import fr.eurecom.warhammerontheroad.application.WriteAndReadStoryActivity;
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
	public final static String CMD_PREPARE_FIGHT		= "PFT";
	public final static String CMD_END_FIGHT			= "EFT";
	public final static String CMD_START_GAME			= "SGM";
	public final static String CMD_CREATE_HERO			= "HER";
	public final static String CMD_REMOVE_HERO			= "RMH";
	public final static String CMD_INITIATIVE			= "INI";
	public final static String CMD_TURN					= "TRN";
	
	public final static int SLEEP_TIME					= 200;

	private final static String TAG						= "Game";

	private int state;
	private int id_game;
	private final Collection<GameServiceListener> gameServiceListeners = new ArrayList<GameServiceListener>();
	private ArrayList<Hero> heros;
	private ArrayList<Player> disconnectedPlayers;
	private Player me;
	private boolean isGM;
	private boolean isInFight;
	private int turnOf;
	private int cmp_init;
	private Map map;
	private CombatView.CombatThread combatThread;
	private WotrService mService;
	private int PA;

	public Game(WotrService mService) {
		this.mService = mService;
		this.state = STATE_NO_GAME;
		this.id_game = 0;
		this.isGM = false;
		this.me = null;
		this.heros = new ArrayList<Hero>();
		this.disconnectedPlayers = new ArrayList<Player>();
		this.map = null;
		this.combatThread = null;
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
		this.addHero(player);
	}

	public void addHero(Hero hero) {
		if(hero instanceof Player)
			for(Hero h: this.heros)
				if(h instanceof Player)
					((Player) h).updateColor(((Player) hero).getName());
		this.heros.add(hero);
	}

	public void removeHero(Hero hero) {
		this.heros.remove(hero);
		//TODO broadcast hero suppression
	}

	public void removePlayer(String name) {
		Player p = this.getPlayer(name);
		if(p != null)
			removeHero(p);
	}
	
	public ArrayList<Hero> getHeros() {
		return this.heros;
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
		if(name.equals(WotrService.GM_NAME)) {
			String[] msg_split = msg.split(NetworkParser.SEPARATOR, 2);
			if(msg_split.length < 2)
				return;
			name = msg_split[0];
			msg = msg_split[1];

		}

		if(msg.equals(CMD_START_GAME)) {
			this.gameStarted();
			return;
		}

		else if(msg.equals(CMD_PREPARE_FIGHT) && !this.isInFight) {
			this.cmp_init = 0;
			this.turnOf = 0;
			this.PA = 2;
			this.me.prepareBattle();
			for(Hero h: this.heros)
				h.resetB();
			this.firePrepareFight();
			return;
		}
		
		else if(msg.equals(CMD_END_FIGHT) && this.isInFight) {
			if(this.checkEndFight() < 0)
				return;
			this.displayEndFight((this.checkEndFight() == 1 && isGM) || (this.checkEndFight() == 0 && !isGM));
			return;
		}
		
		else if(msg.equals(CMD_REMOVE_HERO)) {
			Hero h = this.getHero(name);
			if(h == null || h instanceof Player)
				return;
			this.removeHero(h);
			return;
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
				this.PA = 2;
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
				int init = Integer.parseInt(parts[1]);
				if(h != null) {
					if(this.isGM) {
						for(Hero h_tmp: this.heros)
							if(!(h_tmp instanceof Player))
								h_tmp.computeTurnInFight(h, init);
					}
					else
						this.me.computeTurnInFight(h, init);
					this.incrInitCounter();
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
				int init = this.me.getInitiativeForFight();
				this.mService.getNetworkParser().sendInitiative(init);
				this.incrInitCounter();
			} catch(NumberFormatException e) {
				Log.e(TAG, "Not a number !");
			}
		}

		else if(parts[0].equals(CMD_CREATE_HERO)) {
			try {
				int id = Integer.parseInt(name);
				Hero h = this.getHero(id);
				if(h != null)
					removeHero(h);
				h = this.createHeroWithId(this.mService.getContext(), id);
				h.constructFromString(this.mService, parts[1]);
				h.show();
			} catch(NumberFormatException e) {
				Player p = this.getPlayer(name);
				if(p != null)
					this.removeHero(p);
				p = new Player(this.mService.getContext(), name, this.heros);
				p.constructFromString(this.mService, parts[1]);
				this.addHero(p);
			}
		}
	}

	public void sendNotifPrepareFight() {
		if(!this.isGM)
			return;
		this.PA = 2;
		this.mService.getNetworkParser().prepareFight();
		for(Hero h: this.heros) {
			if(!(h instanceof Player))
				h.prepareBattle();
			h.resetB();
		}
	}

	public void sendNotifStartFight() {
		if(!this.isGM)
			return;
		waitForAction();
		this.mService.getNetworkParser().beginFight(this.map);
		for(Hero h: this.heros)
			if(!(h instanceof Player)) {
				for(Hero htmp: this.heros)
					if(!(htmp instanceof Player) && htmp != h)
						htmp.computeTurnInFight(h, h.getInitiativeForFight());
				this.mService.getNetworkParser().sendInitiative(h.getInitiativeForFight(), h);
				this.incrInitCounter();
			}
	}
	
	private synchronized void incrInitCounter() {
		this.cmp_init ++;
		if(this.cmp_init >= this.heros.size()) {
			if(this.isGM) {
				for(Hero h_tmp: this.heros) {
					Log.d(TAG, "Turn of "+h_tmp.representInString()+" is "+h_tmp.getTurnInFight());
					if(!(h_tmp instanceof Player) && h_tmp.getTurnInFight() == 0) {
						this.turnInFight(h_tmp);
						break;
					}
				}
			}
			else {
				Log.d(TAG, "Turn of "+this.me.representInString()+" is "+this.me.getTurnInFight());
				if(this.me.getTurnInFight() == 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					turnInFight(this.me);
				}
			}
		}
	}
	
	private void reDisplayMenu(Hero h) {
		if(this.PA > 0 && this.combatThread != null)
			this.combatThread.displayMenu(h);
		else
			this.endTurn();
	}
	
	public void performAndSendAttaqueStandard(Hero h, Hero cible) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		Dice d = new Dice();
		h.attaqueStandard(this, cible, d, true);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.STD_ATTACK, d, h, cible.representInString());
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.STD_ATTACK, d, cible.representInString());
		this.reDisplayMenu(h);
	}
	
	public void performAndSendAttaqueRapide(Hero h, Hero cible) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		Dice d = new Dice();
		h.attaqueRapide(this, cible, d);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.ATTAQUE_RAPIDE, d, h, cible.representInString());
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.ATTAQUE_RAPIDE, d, cible.representInString());
		this.reDisplayMenu(h);
	}
	
	public void performAndSendCharge(Hero h, Hero cible, Case dest) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		if(!(dest instanceof Vide) || dest.getX()<0 || dest.getY()<0)
			return;
		Dice d = new Dice();
		h.charge(this, cible, dest, d);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.CHARGE, d, h, cible.representInString(), Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.CHARGE, d, cible.representInString(), Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		this.reDisplayMenu(h);
	}
	
	public void performAndSendDegainer(Hero h) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		h.degainer(this);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.DEGAINER, null, h);
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.DEGAINER, null);
		this.reDisplayMenu(h);
	}
	
	public void performAndSendMove(Hero h, Case dest) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		if(!(dest instanceof Vide))
			return;
		Dice d = new Dice();
		h.move(this, dest, d);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.MOVE, d, h, Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.MOVE, d, Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		this.reDisplayMenu(h);
	}
	
	public void performAndSendDesengager(Hero h, Case dest) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		if(!(dest instanceof Vide))
			return;
		h.desengager(this, dest);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.DESENGAGER, null, h, Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.DESENGAGER, null, Integer.toString(dest.getX()), Integer.toString(dest.getY()));
		this.reDisplayMenu(h);
	}
	
	public void performAndSendRecharger(Hero h) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();
		
		h.recharger(this);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.RECHARGER, null, h);
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.RECHARGER, null);
		this.reDisplayMenu(h);
	}
	
	public void performAndSendViser(Hero h) {
		if(this.combatThread != null)
			this.combatThread.hideMenu();

		h.viser(this);
		if(this.isGM)
			this.mService.getNetworkParser().sendDicedAction(CombatAction.VISER, null, h);
		else
			this.mService.getNetworkParser().sendDicedAction(CombatAction.VISER, null);
		this.reDisplayMenu(h);
	}

	private void turnInFight(Hero h) {
		if(!this.isGM)
			this.myTurnNow();

		if(!h.isAlive()) {
			this.turnNext();
			return;
		}
		
		h.nextTurn();
		
		if(this.combatThread != null)
			this.combatThread.displayMenu(h);
		
		/* --- TEST --- */
		/*
		Hero cible;
		if(this.isGM) {
			int i = (int) Math.random()*this.heros.size();
			do {
				cible = this.heros.get(i);
				i = (i+1)%this.heros.size();
			} while (!(cible instanceof Player) || !cible.isAlive());
		} else {
			int i = (int) Math.random()*this.heros.size();
			do {
				cible = this.heros.get(i);
				i = (i+1)%this.heros.size();
			} while (cible instanceof Player || !cible.isAlive());
		}
		
		this.performAndSendAttaqueStandard(h, cible);
		*/
		/* ------------ */
		
	}

	private void turnNext() {
		this.turnOf = (this.turnOf+1)%this.heros.size();
		this.mService.getNetworkParser().nextTurn(this.turnOf, this.isGM);
		this.PA = 2;

		if(!this.isGM)
			this.waitForTurn();
		else
			for(Hero h_tmp: this.heros)
				if(!(h_tmp instanceof Player) && h_tmp.getTurnInFight() == turnOf) {
					this.turnInFight(h_tmp);
					break;
				}
	}
	
	public void endTurn() {
		this.combatThread.hideMenu();
		
		if(this.checkEndFight() == -1)
			this.turnNext();
		else {
			if(!this.isGM)
				this.waitForTurn();
			this.mService.getNetworkParser().sendEndFight(this.isGM);
			this.displayEndFight((this.checkEndFight() == 1 && isGM) || (this.checkEndFight() == 0 && !isGM));
		}
	}
	
	private void displayEndFight(boolean victory) {
		if(this.combatThread != null)
			this.combatThread.displayEnd(victory);
		this.combatThread = null;
		this.endFight();
	}
	
	public int checkEndFight() {
		boolean isPlayerAlive = false, isEnemyAlive = false;
		for(Hero htmp : this.heros)
			if(htmp instanceof Player) {
				if(htmp.isAlive())
					isPlayerAlive = true;
			} else
				if(htmp.isAlive())
					isEnemyAlive = true;
		if(isPlayerAlive && isEnemyAlive)
			return -1;
		if(isPlayerAlive)
			return 0;
		return 1;
	}

	private void change_state(int state) {
		if(this.state == state)
			return;
		int exState = this.state;
		this.state = state;
		fireStateChanged(exState);
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

	public boolean validateActivity(WotrActivity act) {
		switch(this.state) {
		case STATE_NO_GAME:
			return (act instanceof StartMenuActivity || act instanceof JoinGameActivity || act instanceof NewGameIntroActivity);
		case STATE_GAME_CREATE_WAIT:
			return act instanceof NewGameIntroActivity;
		case STATE_GAME_CREATED:
			return (act instanceof NewGameIntroActivity || act instanceof GMMenuActivity || act instanceof PlayerMenuActivity || act instanceof ChatRoomActivity || (act instanceof WriteAndReadStoryActivity && this.isGM) || act instanceof CreateMapActivity ||
					act instanceof CreateSupportCharaActivity || act instanceof SeeEnemyStatsActivity || act instanceof SeeCharaDataActivity || act instanceof CharaCreationDetailsActivity);
		case STATE_GAME_PERSO_CREATED:
			return (act instanceof PlayerMenuActivity || act instanceof ChatRoomActivity || act instanceof CharaCreationDetailsActivity);
		case STATE_GAME_LAUNCHED:
			return (act instanceof GMMenuActivity || act instanceof PlayerMenuActivity || act instanceof ChatRoomActivity || 
					act instanceof WriteAndReadStoryActivity || act instanceof CreateMapActivity || act instanceof CreateSupportCharaActivity || act instanceof SeeEnemyStatsActivity || 
					act instanceof SeeCharaDataActivity || act instanceof SeeStatsActivity|| act instanceof StatsProfilActivity|| 
					act instanceof StatsObjectActivity|| act instanceof StatsSkillActivity);
		case STATE_GAME_WAIT_TURN:
		case STATE_GAME_TURN:
			return (act instanceof CombatActivity || act instanceof SeeStatsActivity || act instanceof ChatRoomActivity|| act instanceof StatsProfilActivity|| 
					act instanceof StatsObjectActivity|| act instanceof StatsSkillActivity);
		case STATE_GAME_WAIT_ACTION:
		case STATE_GAME_CONFIRM_ACTION:
			return (act instanceof GMMenuActivity || act instanceof ChatRoomActivity || act instanceof WriteAndReadStoryActivity || act instanceof SeeCharaDataActivity || act instanceof SeeEnemyStatsActivity || act instanceof CombatActivity);
		default:
			return false;
		}
	}

	public boolean isGM() {
		return this.isGM;
	}

	public boolean isInFight() {
		return this.isInFight;
	}

	/*
	public void listAvailableGames(ArrayList<String> avail) {
		for(GameServiceListener l: gameServiceListeners)
			l.listAvailableGames(avail);
	} */

	public void userDisconnected(String name) {
		if(this.state == Game.STATE_NO_GAME || this.state == Game.STATE_GAME_CREATE_WAIT)
			return;
		if(this.state == Game.STATE_GAME_CREATED || this.state == Game.STATE_GAME_PERSO_CREATED)
			return;
		Player p = this.getPlayer(name);
		if(p == null)
			return;
		this.removeHero(p);
		this.disconnectedPlayers.add(p);
	}

	public void userConnected(String name) {
		if(this.state == Game.STATE_NO_GAME || this.state == Game.STATE_GAME_CREATE_WAIT)
			return;
		if(this.state == Game.STATE_GAME_CREATED || this.state == Game.STATE_GAME_PERSO_CREATED) {
			if(this.isGM) {
				for(Hero h: this.heros)
					if(!(h instanceof Player))
						this.mService.getNetworkParser().createHero(h, name);
				// TODO: send chat
				// TODO: when GM is disconnected ???
			}
			else {
				if(this.state == Game.STATE_GAME_PERSO_CREATED)
					this.mService.getNetworkParser().createPlayer(this.me, name);
			}
		}
		else {
			for(Player p: this.disconnectedPlayers)
				if(p.getName().equals(name)) {
					this.addHero(p);
					this.disconnectedPlayers.remove(p);
					if(this.isGM) {
						for(Hero h: this.heros)
							if(!(h instanceof Player))
								this.mService.getNetworkParser().createHero(h, name);
					}
					else
						this.mService.getNetworkParser().createPlayer(this.me, name);
					break;
				}
		}
	}

	private void fireStateChanged(int exState) {
		for(GameServiceListener l: gameServiceListeners)
			l.onStateChanged(this, exState);
	}

	private void firePrepareFight() {
		for(GameServiceListener l: gameServiceListeners)
			l.prepareFight();
	}

	public void addGameServiceListener(GameServiceListener listener) {
		gameServiceListeners.add(listener);
	}

	public void removeGameServiceListener(GameServiceListener listener) {
		gameServiceListeners.remove(listener);
	}
	
	public void registerCombatThread(CombatView.CombatThread combatThread) {
		Log.d(TAG, "register combatthread");
		this.combatThread = combatThread;
	}
	
	public void unRegisterCombatThread() {
		this.combatThread = null;
	}
	
	public void printDamage(int x, int y, int dmg) {
		if(this.combatThread != null)
			this.combatThread.printDamage(x, y, dmg);
	}

	
	public void printStatus(int x, int y, String status) {
		if(this.combatThread != null)
			this.combatThread.printStatus(x, y, status);
	}
	
	public void printStandard(int x, int y, String text) {
		if(this.combatThread != null)
			this.combatThread.printStandard(x, y, text);
	}
	
	public boolean canUsePA(int nbPA)
	{
		return PA>=nbPA;
	}
	
	public boolean usePA(int value)
	{
		if(value > PA)
			return false;
		PA -= value;
		return true;
	}

}
