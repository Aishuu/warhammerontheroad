package fr.eurecom.warhammerontheroad.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.R;
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

	private boolean hasVisee;
	private WeaponSet weapons;
	private ArmorSet armor;
	private boolean isengaged;
	private boolean loaded;
	private boolean hasBlocked;
	private int id;
	private int turn_in_fight;
	private int initiative_for_fight;
	protected int resource;

	public Hero(Context context, Race race){
		this.id = ++cmp_id;
		this.context = context;
		weapons = new WeaponSet();
		armor = new ArmorSet();
		this.race = null;
		randomTalentIndex = null;
		setRace(race);
	}

	public Hero(Context context) {
		this.context = context;
		weapons = new WeaponSet();
		armor = new ArmorSet();
		this.race = null;
		randomTalentIndex = null;
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

	public void setRace(Race race) {
		if(race==this.race)
			return;
		this.race = race;
		init();
	}

	public Race getRace() {
		return race;
	}

	public void setId(int id) {
		this.id = id;
		if(id > cmp_id)
			cmp_id = id;
	}

	protected void init_stats() {
		stats = new PrimaryStats(race);
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
			if (randomTalentIndex.length == 0)
			{
				index1 = Tools.getStartingTalent(0);
				do{
					index2 = Tools.getStartingTalent(0);
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
			int index3 = Tools.getStartingTalent(3);
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
			if (randomTalentIndex.length == 0)
				randomTalentIndex[0] = index3;
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

	public int getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Hero))
			return false;
		Hero h = (Hero) o;
		return (this.id == h.getId());
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

	@Override
	public int getResource() {
		return this.resource;
	}


	public void parseGMCommand(String msg) {
		// TODO: parse the commands. For ack, set timer, show notif to gm
	}

	public void parseCommand(Game game, String msg) {
		if(msg.length() == 0)
			return;
		String[] parts = msg.split(NetworkParser.SEPARATOR, -1);
		try {
			CombatAction action = CombatAction.fromIndex(Integer.parseInt(parts[0]));
			switch(action) {
			case STD_ATTACK:
				if(parts.length < 3)
					return;
				Hero h = game.getHero(parts[1]);
				if(h == null)
					return;
				Dice d = new SimulatedDice(msg.split(NetworkParser.SEPARATOR, 3)[2]);
				this.attaqueStandard(game, h, d);
				break;
			case ATTAQUE_RAPIDE:
				break;
			case CHARGE:
				break;
			case DEGAINER:
				break;
			case MOVE:
				break;
			case RECHARGER:
				break;
			case VISER:
				break;
			default:
				Log.e(TAG, "Received : "+msg);
				Log.e(TAG, "Illegal action code !");
			}
		} catch(NumberFormatException e) {
			Log.e(TAG, "Received : "+msg);
			Log.e(TAG, "Not a number !");
		}
	}

	public boolean peutViser(Game game) {
		return !this.hasVisee;
	}

	public ArrayList<Case> whereMovement(Game game) {
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

	public ArrayList<Case> whereAttaqueStandard(Game game) {
		int rangeMin = 1;
		int rangeMax = 1;
		if (weapons.getWeapon() instanceof MeleeWeapon)
		{
			rangeMin = 2;
			rangeMax = ((RangedWeapon) (weapons.getWeapon())).getRange();	
		}else{
			if (!loaded)
				return null;
		}
		ArrayList<Case> all = game.getMap().getInRangeCases(this, rangeMin, rangeMax);
		if(all == null)
			return null;
		ArrayList<Case> result = new ArrayList<Case>();
		for(Case c : all)
			if(c instanceof Hero)
				result.add(c);
		if(result.size() == 0)
			return null;
		return result;
	}

	public ArrayList<Case> whereCharge(Game game) {
		if (game.getPA() > 1){
			if (weapons.getWeapon() instanceof MeleeWeapon)
			{
				ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, 2*stats.getStats(12)+1);
				if(all == null)
					return null;
				ArrayList<Case> interResult = new ArrayList<Case>();
				ArrayList<Case> result = new ArrayList<Case>();
				for(Case c : all)
				{
					if(c instanceof Hero)
					{
						interResult.add(c);
						result.add(c);
					}
				}
				if(interResult.size() == 0)
					return null;
				for(Case c:all)
					if(c instanceof Vide)
						for(Case c1 : result)
							if(game.getMap().getInRangeCases(c1, 1, 1).contains(c))
								result.add(c);
				return result;
			}else{
				return null;
			}}
		return null;
	}

	public boolean peutDegainer(Game game) {
		return weapons.canChange();
	}

	public ArrayList<Case> whereDesengager(Game game) {
		if (game.getPA() > 1){
			if (isengaged){
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
			}else{
				return null;
			}}
		return null;
	}

	public ArrayList<Case> whereAttaqueRapide(Game game) {
		if (game.getPA() > 1){
			if(weapons.getWeapon() instanceof MeleeWeapon)
			{
				ArrayList<Case> all = game.getMap().getInRangeCases(this, 1, 1);
				if(all == null)
					return null;
				ArrayList<Case> result = new ArrayList<Case>();
				for(Case c : all)
					if(c instanceof Hero)
						result.add(c);
				if(result.size() == 0)
					return null;
				return result;
			}else{
				return null;
			}}
		return null;
	}

	public boolean peutRecharger(Game game) {
		return !loaded;
	}

	public void viser(Game game) {
		game.usePA(1);
		this.hasVisee = true;
	}

	public void move(Game game, Case dest, Dice dice) {
		if (isengaged)
		{
			ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
			for(Case c: enemy)
				if(c instanceof Hero)
					if(!(c instanceof Player))
						((Hero)(c)).attaqueStandard(game, this, dice);
		}
		game.usePA(1);
		game.getMap().setCase(this, dest);
		hasVisee = false;
	}

	public void attaqueStandard(Game game, Hero hero, Dice dice) {
		String nameAttacker, nameDefender;
		if(this instanceof Player)
			nameAttacker = ((Player) this).getName();
		else
			nameAttacker = this.getRace().toString();
		if(hero instanceof Player)
			nameDefender = ((Player) hero).getName();
		else
			nameDefender = hero.getRace().toString();
		Log.d(TAG, nameAttacker+" performs a standard attack on "+nameDefender+" !");

		int result = dice.hundredDice();
		int invresult = result == 100 ? 0 : (result-(result/10)*10)*10 + result/10;
		int localisation;
		int modif = hasVisee ? 10 : 0;
		int damages, tmpDamage;
		game.usePA(1);
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
					do{
						tmpDamage = dice.tenDice();
						if (tmpDamage == 10)
							damages += 10;
					}while (tmpDamage == 10);
					damages += tmpDamage;
				}
			}
			Log.d(TAG, this.representInString()+" weapon : "+weapons.getWeapon());
			if (weapons.getWeapon() instanceof RangedWeapon){
				hero.recevoirDamage(weapons.getWeapon().getDegats() + damages, localisation, dice);
				loaded = false;
			}
			else
				hero.recevoirDamage(weapons.getWeapon().getDegats() + stats.getStats(10) + damages, localisation, dice);
		}
		else
			Log.d(TAG, nameAttacker+" failed to hit "+nameDefender);
		hasVisee = false;
	}

	public void charge(Game game, Hero hero, Case dest, Dice dice) {
		if (isengaged)
		{
			ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
			for(Case c: enemy)
				if(c instanceof Hero)
					if(!(c instanceof Player))
						((Hero)(c)).attaqueStandard(game, this, dice);
		}
		game.usePA(2);
		game.getMap().setCase(this, dest);
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
			hero.recevoirDamage(weapons.getWeapon().getDegats() + stats.getStats(10) + damages, localisation, dice);
		}
		hasVisee = false;
	}

	public void degainer(Game game, Weapon arme) {
		game.usePA(1);
		weapons.changeStyle();
	}

	public void recharger(Game game) {
		if(!(weapons.getWeapon() instanceof RangedWeapon))
			return;
		game.usePA(((RangedWeapon)(weapons.getWeapon())).getReload());
		loaded = true;
	}

	public void attaqueRapide(Game game, Hero hero, Dice dice) {
		game.usePA(2);
		for (int i = 0; i<stats.getStats(8); i++)
			attaqueStandard(game, hero, dice);
	}

	public void recevoirDamage(int damages, int localisation, Dice dice) {
		String nameDefender;
		if(this instanceof Player)
			nameDefender = ((Player) this).getName();
		else
			nameDefender = this.getRace().toString();


		if (weapons.getLeftHand() != null && !hasBlocked)
		{
			hasBlocked = true;
			if(skillTest(false, 0, dice.hundredDice(), 10))
				return;
		}
		int tmpDamage = damages - stats.getStats(11) - armor.getProtection(localisation);
		if (tmpDamage < 0)
			tmpDamage = 0;
		tmpDamage = B - tmpDamage < 0 ? B : tmpDamage;
		Log.d(TAG, nameDefender+" suffers "+damages+" damages !");
		B -= tmpDamage;
		if (B == 0)
			death();
	}

	@Override
	public String representInString() {
		return Integer.toString(this.id);
	}

	@Override
	public String describeAsString() {

		String result = Integer.toString(this.race.getIndex()) + "HHH" + stats.describeAsString() + "HHH" + randomTalentIndexToString();
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		// TODO: dummy implementation here
		String[] parts = s.split("HHH");
		String test = "[";
		for(int i=0;i<parts.length;i++)
			test += parts[i] + ",";
		test += "]";
		Log.d("ro",test);
		try {
			setRandomTalentIndex(parts[2]);
			this.setRace(Race.fromIndex(Integer.parseInt(parts[0])));
			stats.constructFromString(service, parts[1]);
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number !" + parts[0]);
		}
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

	public int getInitiativeForFight() {
		return this.initiative_for_fight;
	}

	public void computeTurnInFight(Hero hero, int init) {
		if(init > this.initiative_for_fight || (init == this.initiative_for_fight && hero.representInString().compareTo(this.representInString())<0))
			this.turn_in_fight++;
	}

	public int getTurnInFight() {
		return this.turn_in_fight;
	}

	public void death()
	{
		String nameDefender;
		if(this instanceof Player)
			nameDefender = ((Player) this).getName();
		else
			nameDefender = this.getRace().toString();
		Log.d(TAG, nameDefender+" is dead !");
	}

	public boolean isAlive() {
		return this.B != 0;
	}

	public void resetB()
	{
		B = stats.getStats(9);
	}

	protected void chooseImage() {
		switch(race){
		case GOBLIN:
			this.resource = R.drawable.goblin;
			break;
		case GUARD:
			this.resource = R.drawable.guard;
			break;
		case BANDIT:
			this.resource = R.drawable.bandit;
			break;
		case ORC:
			this.resource = R.drawable.orc;
			break;
		case SKELETON:
			this.resource = R.drawable.skeleton;
		default:
			//TODO: default image
			break;
		}
	}

	public void nextTurn()
	{
		hasBlocked = false;
	}

	public ArrayList<String[]> getWeapon(){
		ArrayList<String[]> result = weapons.toArrayString();
		return result;
	}
	public ArrayList<String[]> getArmor(){
		ArrayList<String[]> result = armor.toArrayString();
		return result;
	}
	public ArrayList<String[]> getskills(){
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(Skills s:skills)
			result.add(s.toArrayString(stats));
		return result;
	}
	public String[] getArmorRecap(){
		return armor.recapArmor();
	}

}
