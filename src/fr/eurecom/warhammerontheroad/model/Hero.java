package fr.eurecom.warhammerontheroad.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.application.CombatView;
import fr.eurecom.warhammerontheroad.network.Describable;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

public class Hero extends Case implements Describable {
	private final static String TAG			= "Hero";
	private static int cmp_id				= 0;

	protected Context context;
	protected Stats stats;
	private int B;
	protected Race race;
	protected ArrayList<Skills> skills;
	private ArrayList<Talents> talents;
	private int[] randomTalentIndex;

	protected boolean hasVisee;
	protected WeaponSet weapons;
	private ArmorSet armor;
	protected boolean isengaged;
	private boolean loaded;
	private boolean hasBlocked;
	private boolean hasAttacked;
	private int id;
	private int turn_in_fight;
	private int initiative_for_fight;

	private boolean display;
	private int display_x, display_y;
	protected Drawable resource;

	public Hero(Context context) {
		this.context = context;
		weapons = new WeaponSet();
		armor = new ArmorSet();
		this.race = null;
		randomTalentIndex = null;
	}

	public Hero(Context context, Race race){
		this.id = ++cmp_id;
		this.context = context;
		weapons = new WeaponSet();
		armor = new ArmorSet();
		this.race = null;
		randomTalentIndex = null;
		setRace(race);
	}

	protected void waitABit() {
		try {
			Thread.sleep(Game.SLEEP_TIME);
		} catch (InterruptedException e) {
		}

	}

	public void AddAdvancedSkills(int i, String speciality){
		int j = 0;
		Skills tmp = null;
		boolean exist = false;
		InputStream is = context.getResources().openRawResource(R.raw.advancedskills);
		if (is != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";

			try {
				while ( (receiveString = bufferedReader.readLine()) != null) {
					if (j == i)
					{
						tmp = new Skills(100+j, receiveString, speciality);
						for (Skills s : skills){
							if (tmp.compare(s))
							{
								s.upgrade();
								exist = true;
								break;
							}	
						}
						if (exist == false)
						{
							skills.add(tmp);
						}
					}
					j++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void AddItem(int index)
	{
		int i = 0;
		String[] parameters;
		InputStream is = context.getResources().openRawResource(R.raw.items);
		if (is != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";

			try {
				while ( (receiveString = bufferedReader.readLine()) != null) {
					if (index == i)
					{
						parameters = receiveString.split(" ");
						switch(parameters.length){
						case 3 :
							armor.addArmor(new Armor(parameters[0], 
									Integer.parseInt(parameters[1]), 
									Integer.parseInt(parameters[2])));
							return;

						case 4 :
							weapons.addMelee(new MeleeWeapon(parameters[0],
									Integer.parseInt(parameters[1]),
									Integer.parseInt(parameters[2]),
									Integer.parseInt(parameters[3])));
							return;

						case 6 :
							weapons.addRange(new RangedWeapon(parameters[0],
									Integer.parseInt(parameters[1]),
									Integer.parseInt(parameters[2]),
									Integer.parseInt(parameters[3]),
									Integer.parseInt(parameters[4]),
									Integer.parseInt(parameters[5])));
							return;
						default:
							Log.e(TAG, "AddItem : "+parameters.length+" parameters (line "+index+")");
							return;
						}
					}else{
						i++;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void AddTalents(int i){
		int j = 0;
		Talents tmp = null;
		boolean exist = false;
		InputStream is = context.getResources().openRawResource(R.raw.talents);
		if (is != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";

			try {
				while ( (receiveString = bufferedReader.readLine()) != null) {
					if (j == i)
					{
						tmp = new Talents(j, receiveString);
						for (Talents t : talents){
							if (tmp.compare(t))
							{
								exist = true;
								break;
							}	
						}
						if (exist == false)
						{
							talents.add(tmp);
						}
					}
					j++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void attaqueRapide(Game game, Hero hero, Dice dice) {
		if(!game.usePA(2))
			return;
		Log.d(TAG, this.representInString()+" performs a fast attack on "+hero.representInString());
		game.printStandard(this.x, this.y, CombatAction.ATTAQUE_RAPIDE.getLabel());
		this.waitABit();
		for (int i = 0; i<stats.getStats(8); i++) {
			_attaqueStandard(game, hero, dice, false);
		}
	}

	public void _attaqueStandard(Game game, Hero hero, Dice dice, boolean describe) {
		String nameAttacker, nameDefender;
		if(this instanceof Player)
			nameAttacker = ((Player) this).getName();
		else
			nameAttacker = this.getRace().toString();
		if(hero instanceof Player)
			nameDefender = ((Player) hero).getName();
		else
			nameDefender = hero.getRace().toString();
		if(describe) {
			game.printStandard(this.x, this.y, CombatAction.STD_ATTACK.getLabel());
			Log.d(TAG, nameAttacker+" performs a standard attack on "+nameDefender+" !");
		}

		int result = dice.hundredDice();
		int invresult = result == 100 ? 0 : (result-(result/10)*10)*10 + result/10;
		int localisation;
		int modif = hasVisee ? 10 : 0;
		int damages, tmpDamage;
		if (invresult < 16)
			localisation = 0;
		else if (invresult < 56)
			localisation = 1;
		else if (invresult < 81)
			localisation = 2;
		else
			localisation = 3;
		if (skillTest(false, 0, result, modif))
		{
			damages = dice.tenDice();
			if (damages == 10)
			{

				if (skillTest(false, 0, result, modif))
				{
					if(describe)
						this.waitABit();
					game.printStatus(this.x, this.y, "Ulrich fury !");
					Log.d(TAG, "Ulrich fury !");
					do{
						tmpDamage = dice.tenDice();
						if (tmpDamage == 10)
							damages += 10;
					}while (tmpDamage == 10);
					damages += tmpDamage;
				}
			}
			if (weapons.getWeapon() instanceof RangedWeapon){
				if(describe)
					this.waitABit();
				hero.recevoirDamage(weapons.getWeapon().getDegats() + damages, localisation, dice, game, false);
				loaded = false;
			}
			else {
				if(describe)
					this.waitABit();
				hero.recevoirDamage(weapons.getWeapon().getDegats() + stats.getStats(10) + damages, localisation, dice, game, true);
				if(nextToEnemy(game))
					isengaged = true;
				else
					isengaged = false;
			}
		}
		else {
			if(describe)
				this.waitABit();
			game.printStatus(this.x, this.y, "Miss");
			Log.d(TAG, nameAttacker+" failed to hit "+nameDefender);
		}
		hasVisee = false;

	}

	public void attaqueStandard(Game game, Hero hero, Dice dice) {
		if(!game.usePA(1))
			return;
		this._attaqueStandard(game, hero, dice, true);
	}

	public void charge(Game game, Hero hero, Case dest, Dice dice) {
		if(!game.usePA(2))
			return;
		if (isengaged)
		{
			ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
			for(Case c: enemy)
				if(c instanceof Player)
					((Hero)(c))._attaqueStandard(game, this, dice, true);
		}
		Log.d(TAG, this.representInString()+" charges "+hero.representInString());
		game.printStandard(this.x, this.y, CombatAction.CHARGE.getLabel());

		this._move(game, dest, 2f);

		int result = dice.hundredDice();
		int invresult = result == 100 ? 0 : (result-result/10)*10 + result/10;
		int localisation;
		int damages, tmpDamage;
		if (invresult < 16)
			localisation = 0;
		else if (invresult < 56)
			localisation = 1;
		else if (invresult < 81)
			localisation = 2;
		else
			localisation = 3;
		if (skillTest(false, 0, result, 20))
		{
			damages = dice.tenDice();
			if (damages == 10)
			{
				this.waitABit();
				game.printStatus(this.x, this.y, "Ulrich fury !");
				Log.d(TAG, "Ulrich fury !");
				if (skillTest(false, 0, result, 20))
				{
					do{
						tmpDamage = dice.tenDice();
						if (tmpDamage == 10)
							damages += 10;
					}while (tmpDamage == 10);
					damages += tmpDamage;
				}
			}
			this.waitABit();
			hero.recevoirDamage(weapons.getWeapon().getDegats() + stats.getStats(10) + damages, localisation, dice, game, true);
		}
		else {
			this.waitABit();
			game.printStatus(this.x, this.y, "Miss");
			Log.d(TAG, this.representInString()+" misses "+hero.representInString());
		}
		hasVisee = false;
		isengaged = nextToEnemy(game);
	}

	protected void chooseImage() {
		switch(race){
		case GOBLIN:
			this.resource = this.context.getResources().getDrawable(R.drawable.goblin);
			break;
		case GUARD:
			this.resource = this.context.getResources().getDrawable(R.drawable.guard);
			break;
		case BANDIT:
			this.resource = this.context.getResources().getDrawable(R.drawable.bandit);
			break;
		case ORC:
			this.resource = this.context.getResources().getDrawable(R.drawable.orc);
			break;
		case SKELETON:
			this.resource = this.context.getResources().getDrawable(R.drawable.skeleton);
		default:
			//TODO: default image
			break;
		}
	}

	public void computeTurnInFight(Hero hero, int init) {
		Log.d(TAG, "Hero "+representInString()+" (init : "+this.initiative_for_fight+") received init "+init+" from "+hero.representInString());
		Log.d(TAG, "Turn of "+representInString()+" now is "+this.turn_in_fight);
		if(init > this.initiative_for_fight || (init == this.initiative_for_fight && hero.representInString().compareTo(this.representInString())<0))
			this.turn_in_fight++;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		String[] parts = s.split("HHH");
		String test = "[";
		for(int i=0;i<parts.length;i++)
			test += parts[i] + ",";
		test += "]";
		Log.d("ro",test);
		try {
			setRandomTalentIndex(parts[2]);
			this.setRace(Race.fromIndex(Integer.parseInt(parts[0])));
			if(this.stats != null){
				stats.constructFromString(service, parts[1]);
				resetB();
			}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number ! " + parts[0]);
		}
	}

	public void CreateBasicsSkills(){
		int i = 0;
		InputStream is = context.getResources().openRawResource(R.raw.basicskills);
		if (is != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String receiveString = "";

			try {
				while ( (receiveString = bufferedReader.readLine()) != null) {
					skills.add(new Skills(i, receiveString));
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void death(Game game)
	{
		String nameDefender;
		if(this instanceof Player)
			nameDefender = ((Player) this).getName();
		else
			nameDefender = this.getRace().toString();
		Log.d(TAG, nameDefender+" is dead !");
		long time_start = SystemClock.elapsedRealtime();
		long t;
		do {
			t = SystemClock.elapsedRealtime();
			float ratio = ((float)(t-time_start))/CombatView.DELAY_DEATH_ANIM;
			if(ratio < 0.2)
				this.display = false;
			else if(ratio < 0.4)
				this.display = true;
			else if(ratio < 0.6)
				this.display = false;
			else if(ratio < 0.8)
				this.display = true;
			else
				this.display = false;
		} while(t-time_start < CombatView.DELAY_DEATH_ANIM);
		this.display = false;
		game.getMap().removeCase(this);
	}

	public void degainer(Game game) {
		if(!game.usePA(1))
			return;
		game.printStandard(this.x, this.y, CombatAction.DEGAINER.getLabel());
		weapons.changeStyle();
		this.waitABit();
	}

	public void desengager(Game game, Case dest) {
		if(!game.usePA(2))
			return;
		hasVisee = false;
		this.isengaged = false;
		game.printStandard(this.x, this.y, CombatAction.DESENGAGER.getLabel());
		this.waitABit();
		this._move(game, dest, 1f);
	}

	@Override
	public String describeAsString() {
		String result = Integer.toString(this.race.getIndex()) + "HHH" + stats.describeAsString() + "HHH" + randomTalentIndexToString();
		return result;
	}

	public void doDraw(Canvas c, int cell_size) {
		if(this.display && this.x >= 0 && this.y >= 0) {
			int xtmp, ytmp;
			if(this.display_x < 0 || this.display_y < 0) {
				xtmp = this.x*cell_size;
				ytmp = this.y*cell_size;
			} else {
				xtmp = this.display_x;
				ytmp = this.display_y;
			}
			this.resource.setBounds(xtmp, ytmp, xtmp+cell_size, ytmp+cell_size);
			this.resource.draw(c);
		}
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Hero))
			return false;
		Hero h = (Hero) o;
		return (this.id == h.getId());
	}


	public ArrayList<String[]> getArmor(){
		ArrayList<String[]> result = armor.toArrayString();
		return result;
	}

	public String[] getArmorRecap(){
		return armor.recapArmor();
	}

	public int getId() {
		return this.id;
	}

	public int getInitiativeForFight() {
		return this.initiative_for_fight;
	}

	public Race getRace() {
		return race;
	}

	public ArrayList<String[]> getskills(){
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(Skills s:skills)
			result.add(s.toArrayString(stats));
		return result;
	}

	public int getTurnInFight() {
		return this.turn_in_fight;
	}

	public ArrayList<String[]> getWeapon(){
		ArrayList<String[]> result = weapons.toArrayString();
		return result;
	}

	public void init() {
		this.hasVisee = false;
		this.chooseImage();
		this.init_stats();
		skills = new ArrayList<Skills>();
		talents = new ArrayList<Talents>();
		CreateBasicsSkills();
		switch(race){
		case HUMAN:
			int index1, index2;
			skills.get(3).upgrade();
			AddAdvancedSkills(4, "l'empire");
			AddAdvancedSkills(16, "reikspiel");
			if (randomTalentIndex == null || randomTalentIndex.length < 2)
			{
				this.randomTalentIndex = new int[2];
				index1 = Talents.getStartingTalent(0);
				do{
					index2 = Talents.getStartingTalent(0);
				}while(index1 == index2);
				randomTalentIndex[0] = index1;
				randomTalentIndex[1] = index2;
			}else{
				index1 = randomTalentIndex[0];
				index2 = randomTalentIndex[1];
			}
			AddTalents(index1);
			AddTalents(index2);
			break;

		case ELF:
			AddAdvancedSkills(4, "elfes");
			AddAdvancedSkills(16, "eltharin");
			AddAdvancedSkills(16, "reikspiel");
			AddTalents(2);
			AddTalents(33);
			AddTalents(48);
			AddTalents(37);
			AddTalents(64);
			AddTalents(81);
			break;

		case DWARF:
			AddAdvancedSkills(4, "nains");
			AddAdvancedSkills(16, "khazalid");
			AddAdvancedSkills(16, "reikspiel");
			AddAdvancedSkills(36, "");
			AddAdvancedSkills(38, "");
			AddAdvancedSkills(40, "");
			AddTalents(30);
			AddTalents(58);
			AddTalents(62);
			AddTalents(66);
			AddTalents(80);
			AddTalents(81);
			break;

		case HOBBIT:
			int index3 = Talents.getStartingTalent(3);
			skills.get(3).upgrade();
			AddAdvancedSkills(3, "genealogie/heraldique");
			AddAdvancedSkills(4, "halflings");
			AddAdvancedSkills(16, "halflings");
			AddAdvancedSkills(16, "reikspiel");
			AddAdvancedSkills(29, "");
			AddAdvancedSkills(35, "");
			AddTalents(48);
			AddTalents(59);
			AddTalents(81);
			if (randomTalentIndex == null || randomTalentIndex.length == 0) {
				randomTalentIndex = new int[1];
				randomTalentIndex[0] = index3;
			}
			else
				index3 = randomTalentIndex[0];
			AddTalents(index3);
			break;
		case BANDIT:
			skills.get(3).upgrade();
			skills.get(4).upgrade();
			skills.get(6).upgrade();
			skills.get(7).upgrade();
			skills.get(9).upgrade();
			skills.get(16).upgrade();
			skills.get(18).upgrade();
			AddAdvancedSkills(2, "");
			AddAdvancedSkills(4, "Empire");
			AddAdvancedSkills(9, "");
			AddAdvancedSkills(16, "reikspiel");
			AddTalents(3);
			AddTalents(7);
			AddTalents(22);
			AddTalents(56);
			AddItem(5);
			AddItem(8);
			AddItem(6);
			AddItem(0);
			AddItem(1);
			AddItem(2);
			break;

		case GOBLIN:
			skills.get(6).upgrade();
			skills.get(7).upgrade();
			skills.get(8).upgrade();
			skills.get(9).upgrade();
			skills.get(16).upgrade();
			skills.get(19).upgrade();
			AddAdvancedSkills(16, "Goblinoid");
			AddTalents(81);
			AddItem(6);
			AddItem(0);
			AddItem(10);
			break;

		case GUARD:
			skills.get(3);
			skills.get(3);
			skills.get(11);
			skills.get(12);
			skills.get(16);
			AddAdvancedSkills(3, "droit");
			AddAdvancedSkills(4, "Empire");
			AddAdvancedSkills(9, "");
			AddAdvancedSkills(16, "reikspiel");
			AddAdvancedSkills(48, "");
			AddTalents(17);
			AddTalents(19);
			AddTalents(21);
			AddTalents(37);
			AddTalents(58);
			AddTalents(64);
			AddItem(6);
			AddItem(8);
			AddItem(0);
			break;

		case SKELETON:
			AddTalents(24);
			AddTalents(51);
			AddItem(6);
			AddItem(5);
			AddItem(0);
			break;

		case ORC:
			skills.get(8).upgrade();
			skills.get(9).upgrade();
			skills.get(12).upgrade();
			skills.get(16).upgrade();

			skills.get(19).upgrade();
			AddAdvancedSkills(16, "goblinoid");
			AddAdvancedSkills(52, "");
			AddTalents(13);
			AddTalents(19);
			AddTalents(50);
			AddTalents(81);
			AddItem(9);
			AddItem(8);
			AddItem(6);
			AddItem(5);
			AddItem(11);
		}
	}

	protected void init_stats() {
		stats = new PrimaryStats(race);
		this.resetB();
	}

	public boolean isAlive() {
		return this.B != 0;
	}

	protected void _move(Game game, Case dest, float speed) {
		int x_start = this.x;
		int y_start = this.y;
		game.getMap().setCase(this, dest);
		long start_time = SystemClock.elapsedRealtime();
		long t;
		int cell_size = game.getMap().getCellSize();
		long dist = (long) (Math.sqrt((this.x-x_start)*(this.x-x_start)+(this.y-y_start)*(this.y-y_start))*cell_size);
		long delay_anim = (long)((CombatView.DELAY_CROSS_X*dist)/(game.getMap().getMaxX()*cell_size*speed));
		do {
			t = SystemClock.elapsedRealtime();
			float ratio = ((float) (t-start_time))/delay_anim;
			this.display_x = (int)(x_start*cell_size + (this.x - x_start)*cell_size*ratio);
			this.display_y = (int)(y_start*cell_size + (this.y - y_start)*cell_size*ratio);
		} while(t-start_time < delay_anim);
		this.display_x = -1;
		this.display_y = -1;
	}

	public void move(Game game, Case dest, Dice dice) {
		if(!game.usePA(1))
			return;
		if (isengaged)
		{
			ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
			for(Case c: enemy)
				if(c instanceof Player)
					((Hero)(c))._attaqueStandard(game, this, dice, true);
		}
		hasVisee = false;
		isengaged = false;
		this._move(game, dest, 1f);
	}

	public boolean nextToEnemy(Game game)
	{
		ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
		for(Case c: enemy)
			if(c instanceof Player)
				return true;
		return false;
	}

	public void nextTurn()
	{
		hasBlocked = false;
	}

	public void parseCommand(Game game, String msg) {
		if(msg.length() == 0)
			return;
		Hero h;
		Dice d;
		Case dest;
		String[] parts = msg.split(NetworkParser.SEPARATOR, -1);
		try {
			CombatAction action = CombatAction.fromIndex(Integer.parseInt(parts[0]));

			if(action == null) {
				Log.e(TAG, "Received : "+msg);
				Log.e(TAG, "Illegal action code !");
				return;
			}
			switch(action) {
			case STD_ATTACK:
				if(parts.length < 3)
					return;
				h = game.getHero(parts[1]);
				if(h == null)
					return;
				d = new SimulatedDice(msg.split(NetworkParser.SEPARATOR, 3)[2]);
				this.attaqueStandard(game, h, d);
				break;
			case ATTAQUE_RAPIDE:
				if(parts.length < 3)
					return;
				h = game.getHero(parts[1]);
				if(h == null)
					return;
				d = new SimulatedDice(msg.split(NetworkParser.SEPARATOR, 3)[2]);
				this.attaqueRapide(game, h, d);
				break;
			case CHARGE:
				if(parts.length < 5)
					return;
				h = game.getHero(parts[1]);
				if(h == null)
					return;
				dest = game.getMap().getCase(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
				if(!(dest instanceof Vide))
					return;
				d = new SimulatedDice(msg.split(NetworkParser.SEPARATOR, 5)[4]);
				this.charge(game, h, dest, d);
				break;
			case DEGAINER:
				this.degainer(game);
				break;
			case MOVE:
				if(parts.length < 4)
					return;
				dest = game.getMap().getCase(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
				if(!(dest instanceof Vide))
					return;
				d = new SimulatedDice(msg.split(NetworkParser.SEPARATOR, 4)[3]);
				this.move(game, dest, d);
				break;
			case DESENGAGER:
				if(parts.length < 3)
					return;
				dest = game.getMap().getCase(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
				if(!(dest instanceof Vide))
					return;
				this.desengager(game, dest);
				break;
			case RECHARGER:
				this.recharger(game);
				break;
			case VISER:
				this.viser(game);
				break;
			}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Received : "+msg);
			Log.e(TAG, "Not a number !");
		}
	}

	public void parseGMCommand(String msg) {
		// TODO: parse the commands. For ack, set timer, show notif to gm
	}

	public boolean peutDegainer(Game game) {
		return weapons.canChange() && game.canUsePA(1);
	}

	public boolean peutRecharger(Game game) {
		return weapons.getWeapon() instanceof RangedWeapon && !loaded && game.canUsePA(((RangedWeapon)(weapons.getWeapon())).getReload());
	}

	public boolean peutViser(Game game) {
		return !this.hasVisee && game.canUsePA(1);
	}

	public int prepareBattle()
	{
		isengaged = false;
		loaded = true;
		hasBlocked = false;
		this.initiative_for_fight = stats.getStats(4) + new Dice().tenDice();
		this.turn_in_fight = 0;
		return this.initiative_for_fight;
	}

	public String randomTalentIndexToString()
	{
		String result;
		if(randomTalentIndex != null)
		{
			int n = randomTalentIndex.length;
			result = Integer.toString(n);
			if (n != 0)
			{
				for (int i=0; i<n; i++)
				{
					result += "RRR" + randomTalentIndex[i];
				}
			}
		}else{
			result = "0";
		}
		return result;
	}

	public void recevoirDamage(int damages, int localisation, Dice dice, Game g, boolean closeRange) {
		String nameDefender;
		if(this instanceof Player)
			nameDefender = ((Player) this).getName();
		else
			nameDefender = this.getRace().toString();


		if (weapons.getLeftHand() != null && !hasBlocked)
		{
			hasBlocked = true;
			if(skillTest(false, 0, dice.hundredDice(), 10)) {
				g.printStatus(this.x, this.y, "Blocked");
				this.waitABit();
				return;
			}
		}
		int tmpDamage = damages - stats.getStats(11) - armor.getProtection(localisation);
		if (tmpDamage < 0)
			tmpDamage = 0;
		tmpDamage = B - tmpDamage < 0 ? B : tmpDamage;
		g.printDamage(this.x, this.y, damages);
		this.waitABit();
		Log.d(TAG, nameDefender+" suffers "+damages+" damages !");
		B -= tmpDamage;
		if (B == 0)
			death(g);
		if (closeRange)
			isengaged = true;
	}

	public void recharger(Game game) {
		if(!(weapons.getWeapon() instanceof RangedWeapon))
			return;
		if(!game.usePA(((RangedWeapon)(weapons.getWeapon())).getReload()))
			return;
		game.printStandard(this.x, this.y, CombatAction.RECHARGER.getLabel());
		loaded = true;
		this.waitABit();
	}

	@Override
	public String representInString() {
		return Integer.toString(this.id);
	}

	public void resetB()
	{
		this.display = true;
		if(stats != null)
			B = stats.getStats(9);
	}

	public void setId(int id) {
		this.id = id;
		if(id > cmp_id)
			cmp_id = id;
	}

	@Override
	public void setPos(int x, int y) {
		this.display_x = -1;
		this.display_y = -1;
		super.setPos(x, y);
	}

	public void setRace(Race race) {
		if(race==this.race)
			return;
		this.race = race;
		init();
	}

	public void setRandomTalentIndex(String s)
	{
		String[] parameters = s.split("RRR");
		if(Integer.parseInt(parameters[0]) == 0)
			return;
		else{
			randomTalentIndex = new int[Integer.parseInt(parameters[0])];
			for (int i=0; i<Integer.parseInt(parameters[0]); i++)
			{
				randomTalentIndex[i] = Integer.parseInt(parameters[i+1]);
			}
		}
	}

	public void show(){
		Log.d("race",race.toString());

		Log.d("stats",stats.describeAsString());

		for(Skills s : skills){
			s.show();
		}
		for(Talents t: talents){
			t.show();
		}

		weapons.show();
		armor.show();
	}

	public boolean skillTest(boolean skill, int index, int dice, int diffModificator)// if skill is true, the index is the one of the skill in the array. Else it's the index of the stat we want to test.
	{
		int modificator;
		int statIndex;
		int tmpstat;
		if(skill)
		{
			int tmp;
			tmp = skills.get(index).testModificator();
			statIndex = tmp/10;
			modificator = tmp-10*statIndex;
		}else{
			statIndex = index;
			modificator = 1;
		}
		tmpstat = stats.getStats(statIndex);
		switch (modificator){
		case 0 :
			tmpstat /=2;
			break;
		case 2 :
			tmpstat += 10;
			break;
		case 3:
			tmpstat += 20;
			break;
		}
		if ((dice)>tmpstat+diffModificator)
			return false;
		return true;
	}

	public void viser(Game game) {
		if(!game.usePA(1))
			return;
		game.printStandard(this.x, this.y, CombatAction.VISER.getLabel());
		this.hasVisee = true;
		this.waitABit();
	}

	public ArrayList<Case> whereAttaqueRapide(Game game) {
		if(game.canUsePA(2) && weapons.getWeapon() instanceof MeleeWeapon)
		{
			ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, 1);
			if(all == null)
				return null;
			ArrayList<Case> result = new ArrayList<Case>();
			for(Case c : all)
				if(c instanceof Hero && ((this instanceof Player && !(c instanceof Player)) || (!(this instanceof Player) && c instanceof Player)))
					result.add(c);
			if(result.size() == 0)
				return null;
			return result;
		}
		return null;
	}
	public ArrayList<Case> whereAttaqueStandard(Game game) {
		if(!game.canUsePA(1))
			return null;
		if (hasAttacked)
			return null;
		int rangeMin = 1;
		int rangeMax = 1;
		if (weapons.getWeapon() instanceof RangedWeapon)
		{
			if (isengaged)
				return null;
			rangeMin = 2;
			rangeMax = ((RangedWeapon) (weapons.getWeapon())).getRange();	
			if (!loaded)
				return null;
		}
		ArrayList<Case> all = game.getMap().getInRangeCases(this, rangeMin, rangeMax);
		if(all == null)
			return null;
		ArrayList<Case> result = new ArrayList<Case>();
		for(Case c : all)
			if(c instanceof Hero && ((this instanceof Player && !(c instanceof Player)) || (!(this instanceof Player) && c instanceof Player)))
				result.add(c);
		if(result.size() == 0)
			return null;
		return result;
	}

	public ArrayList<Case> whereCharge(Game game) {
		if (game.canUsePA(2) && weapons.getWeapon() instanceof MeleeWeapon)
		{
			ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, 2*stats.getStats(12)+1);
			if(all == null)
				return null;
			ArrayList<Case> interResult = new ArrayList<Case>();
			ArrayList<Case> result = new ArrayList<Case>();
			for(Case c : all)
			{
				if(c instanceof Hero && ((this instanceof Player && !(c instanceof Player)) || (!(this instanceof Player) && c instanceof Player)))
				{
					interResult.add(c);
					result.add(c);
				}
			}
			if(interResult.size() == 0)
				return null;
			for(Case c:all)
				if(c instanceof Vide)
					for(Case c1 : interResult)
						if(game.getMap().getInRangeCases(c1, 1, 1).contains(c))
							result.add(c);
			return result;
		}
		return null;
	}

	public ArrayList<Case> whereDesengager(Game game) {
		if (game.canUsePA(2) && isengaged){
			ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, stats.getStats(12));
			if(all == null)
				return null;
			ArrayList<Case> result = new ArrayList<Case>();
			for(Case c : all)
				if(c instanceof Vide)
					result.add(c);
			if(result.size() == 0)
				return null;
			return result;
		}
		return null;
	}

	public ArrayList<Case> whereMovement(Game game) {
		if(!game.canUsePA(1))
			return null;
		ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, stats.getStats(12));
		if(all == null)
			return null;
		ArrayList<Case> result = new ArrayList<Case>();
		for(Case c : all)
			if(c instanceof Vide)
				result.add(c);
		if(result.size() == 0)
			return null;
		return result;
	}

	public Stats getStats() {
		return stats;
	}

	public int getB() {
		return B;
	}

	public Drawable getResource() {
		return resource;
	}

}
