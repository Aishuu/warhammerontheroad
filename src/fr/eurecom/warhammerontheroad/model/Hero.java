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
	protected Job job;
	private boolean hasVisee;
	protected Weapon armeDraw;
	private ArrayList<Armor> armor;
	private int id;
	private int turn_in_fight;
	private int initiative_for_fight;
	protected int resource;

	public Hero(Context context, Race race){
		this.id = ++cmp_id;
		this.context = context;
		setRace(race);
		armor = new ArrayList<Armor>();
		armor.add(new Armor("casque", 0, 0));
		armor.add(new Armor("veste", 1, 0));
		armor.add(new Armor("veste", 2, 0));
		armor.add(new Armor("pantalon", 3, 0));
	}

	public Hero(Context context) {
		this.context = context;
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
		if(id >= cmp_id)
			cmp_id = id+1;
	}

	protected void init_stats() {
		stats = new PrimaryStats(race);
	}

	public void init() {
		this.hasVisee = false;
		this.armeDraw = null;

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
			index1 = Tools.getStartingTalent(0);
			do{
				index2 = Tools.getStartingTalent(0);
			}while(index1 == index2);
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
			AddTalents(Tools.getStartingTalent(3));
			break;
		default:
			//TODO: create skills for other races
			break;
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

	public void show(){
		Log.d("race",race.toString());
		/*
		if(stats != null)
			stats.show();
		 */
		for(Skills s : skills){
			s.show();
		}
		for(Talents t: talents){
			t.show();
		}
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
		if ((dice+diffModificator)>tmpstat)
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
		// TODO: PA
	}

	public ArrayList<Case> whereMovement(Game game) {
		// TODO: Change range according to movement
		ArrayList<Case> all = game.getMap().getInRangeCases(this, 0, 2);
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
		// TODO: check degainer...
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
	}

	public ArrayList<Case> whereCharge(Game game) {
		// TODO: implement this
		return whereAttaqueStandard(game);
	}

	public boolean peutDegainer(Game game) {
		return this.armeDraw == null;
	}

	public ArrayList<Case> whereDesengager(Game game) {
		// TODO: Change range according to movement
		ArrayList<Case> all = game.getMap().getInRangeCases(this, 0, 2);
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

	public ArrayList<Case> whereAttaqueRapide(Game game) {
		// TODO: check degainer...
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
	}

	public boolean peutRecharger(Game game) {
		return false;
	}

	public void viser(Game game) {
		this.hasVisee = true;
	}

	public void move(Game game, Case dest) {
		game.getMap().setCase(this, dest);
		hasVisee = false;
	}

	public void attaqueStandard(Game game, Hero hero, Dice dice) {
		int result = dice.hundredDice();
		int invresult = result == 100 ? 0 : (result-result/10)*10 + result/10;
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
					do{
						tmpDamage = dice.tenDice();
						if (tmpDamage == 10)
							damages += 10;
					}while (tmpDamage == 10);
					damages += tmpDamage;
				}
			}
			if (armeDraw instanceof RangedWeapon)
				hero.recevoirDamage(armeDraw.getDegats() + damages, localisation);
			else
				hero.recevoirDamage(armeDraw.getDegats() + stats.getStats(10) + damages, localisation);
		}
		hasVisee = false;
	}

	public void charge(Game game, Hero hero, Case dest, Dice dice) {
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
			hero.recevoirDamage(armeDraw.getDegats() + stats.getStats(10) + damages, localisation);
		}
		hasVisee = false;
	}

	public void degainer(Game game, Weapon arme) {
		this.armeDraw = arme;
	}

	public void recharger(Game game) {
		// TODO
	}

	public void attaqueRapide(Game game, Hero hero, Dice dice) {
		for (int i = 0; i<stats.getStats(8); i++)
		{
			attaqueStandard(game, hero, dice);
		}
	}

	public void recevoirDamage(int damages, int localisation) {
		int tmpDamage = damages - stats.getStats(11) - armor.get(localisation).getArmorPoints();
		if (tmpDamage < 0)
			tmpDamage = 0;
		if (damages - tmpDamage < 0)
			B = 0;
		else
			B -= tmpDamage;
		if (B == 0)
			death();
		Log.d(TAG, damages+" degats recus !");
	}

	@Override
	public String representInString() {
		return Integer.toString(this.id);
	}

	@Override
	public String describeAsString() {
		// TODO: dummy implementation here
		String result = NetworkParser.constructStringFromArgs(Integer.toString(this.race.getIndex()));
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		// TODO: dummy implementation here
		String[] parts = s.split(NetworkParser.SEPARATOR, -1);
		try {
			this.setRace(Race.fromIndex(Integer.parseInt(parts[0])));
			this.init();
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number !");
		}
	}
	public int beginBattle()
	{
		resetB();
		this.initiative_for_fight = stats.getStats(4) + new Dice().tenDice();
		this.turn_in_fight = 0;
		return this.initiative_for_fight;
	}

	public void computeTurnInFight(Hero hero, int init) {
		if(init > this.initiative_for_fight || (init == this.initiative_for_fight && hero.representInString().compareTo(this.representInString())<0))
			this.turn_in_fight++;
		Log.d(TAG, "Hero "+hero.representInString()+" did "+init+" as initiative...");
	}

	public int getTurnInFight() {
		return this.turn_in_fight;
	}

	public void death()
	{
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
}