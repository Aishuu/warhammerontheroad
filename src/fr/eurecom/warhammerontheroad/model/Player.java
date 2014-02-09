package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.network.NetworkParser;

public class Player extends Hero {	
	private final static String TAG =		"Player";

	private String name;
	private Color color;
	private Gender gender;
	private int age, size, weight, siblings;
	private String eyeColor, hairColor;
	private Place birthPlace;
	private int jobIndex;
	private Job job;


	public Player(Context context, String name, ArrayList<Hero> heros) {
		super(context);
		this.setId(0);
		this.name = name;
		int index = 0;
		for(Hero h: heros)
			if(h instanceof Player && ((Player)h).getName().compareTo(this.name) < 0)
				index++;
		this.color = Color.fromIndex(index);
		setOrigin();
	}
	
	public void updateColor(String newname) {
		if(newname.compareTo(this.name) < 0)
			this.color = Color.fromIndex(this.color.getIndex()+1);
	}
	
	@Override
	protected void init_stats() {
		stats = new ActualStats(new PrimaryStats(race));
	}


	public void AddJob(int index)
	{
		int i;
		jobIndex = index;
		job = new Job(index, context);
		((ActualStats) stats).SetSecondaryStats(job.getSecondaryStats());
		resetB();
		int tmps[] = job.getSkills();
		for(i = 0; i<tmps.length; i++)
		{
			if(tmps[i]<100)
			{
				skills.get(tmps[i]).upgrade();
			}
			else
			{
				AddAdvancedSkills(tmps[i]-100, "");
			}
		}
		int tmpt[] = job.getTalents();
		for(i = 0; i<tmpt.length; i++)
		{
			AddTalents(tmpt[i]);
		}
		int tmpi[] = job.getItems();
		for(i = 0; i<tmpi.length; i++)
		{
			AddItem(tmpi[i]);
		}
		
		
	}

	public void setColor(Color color) {
		if(color!=this.color){
			this.color = color;
			this.chooseImage();
		}
	}

	public Color getColor() {
		return this.color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void chooseImage(){
		if(gender==null||color==null||race==null)
			return;
		if(gender==Gender.FEMALE){
			switch(race){
			case HUMAN:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_f_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_f_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_f_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_f_orange);
					break;
				}
				break;
			case ELF:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_f_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_f_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_f_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_f_orange);
					break;
				}
				break;
			case DWARF:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_f_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_f_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_f_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_f_orange);
					break;
				}
				break;
			case HOBBIT:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_f_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_f_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_f_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_f_orange);
					break;
				}
				break;
			default:
				break;
			}

		}else{
			switch(race){
			case HUMAN:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_m_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_m_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_m_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.human_m_orange);
					break;
				}
				break;
			case ELF:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_m_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_m_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_m_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.elf_m_orange);
					break;
				}
				break;
			case DWARF:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_m_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_m_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_m_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.dwarf_m_orange);
					break;
				}
				break;
			case HOBBIT:
				switch(color){
				case BLUE:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_m_blue);
					break;
				case GREEN:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_m_green);
					break;
				case VIOLET:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_m_violet);
					break;
				case ORANGE:
					this.resource = this.context.getResources().getDrawable(R.drawable.halfling_m_orange);
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String representInString() {
		return this.name;
	}

	@Override
	public String describeAsString() {
		String result = NetworkParser.constructStringFromArgs(super.describeAsString(), gender.toString(),
				eyeColor, hairColor,Integer.toString(age), Integer.toString(size), Integer.toString(weight), 
				Integer.toString(siblings), birthPlace.toString(), Integer.toString(jobIndex));
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		String[] parts = s.split(NetworkParser.SEPARATOR, 10);
		if(parts.length < 10) {
			Log.e(TAG, "Not enough arguments !");
			return;
		}

		super.constructFromString(service, parts[0]);
		this.init();		// not called from Hero
		try {
			this.setGender(Gender.fromIndex(Integer.parseInt(parts[1])));
			this.setEyeColor(parts[2]);
			this.setHairColor(parts[3]);
			this.setAge(Integer.parseInt(parts[4]));
			this.setSize(Integer.parseInt(parts[5]));
			this.setWeight(Integer.parseInt(parts[6]));
			this.setSiblings(Integer.parseInt(parts[7]));
			this.setBirthPlace(Place.fromIndex(Integer.parseInt(parts[8])));
			this.AddJob(Integer.parseInt(parts[9]));
			this.stats.constructFromString(service, parts[0].split("HHH")[1]);	// not called from Hero. Must be last otherwise AddJob and init change the stats
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number !");
		}
	}
	
	@Override
	public void setRace(Race race){
		if(race==this.race)
			return;
		this.race=race;
		setDetails(race);
	}

	public void setDetails(Race race){
		//TODO: change methods to use those from the book and use dices
		Random rand=new Random();

		//age, size, weight function of race
		switch(race){
		case HUMAN:
			setAge(rand.nextInt(20)+16);
			setSize(rand.nextInt(34)+52);
			setWeight(rand.nextInt(53)+47);
			break;
		case ELF:
			setAge(rand.nextInt(96)+30);
			setSize(rand.nextInt(31)+60);
			setWeight(rand.nextInt(44)+36);
			break;
		case DWARF:
			setAge(rand.nextInt(96)+20);
			setSize(rand.nextInt(31)+25);
			setWeight(rand.nextInt(44)+40);
			break;
		case HOBBIT:
			setAge(rand.nextInt(41)+20);
			setSize(rand.nextInt(31));
			setWeight(rand.nextInt(32)+34);
			break;
		default:
			setAge(rand.nextInt(101)+20);
			setSize(rand.nextInt(91));
			setWeight(rand.nextInt(60)+40);
			break;
		}
		
	}

	public boolean nextToEnemy(Game game)
	{
		ArrayList<Case> enemy = game.getMap().getInRangeCases(this, 1, 1);
		for(Case c: enemy)
			if(c instanceof Hero)
				if(!(c instanceof Player))
					return true;
		return false;
	}
	
	public void setOrigin(){
		Random rand=new Random();
		//birthPlace
		setBirthPlace(Place.fromIndex(rand.nextInt(Place.getNumberOfPlaces())));
		//siblings
		setSiblings(rand.nextInt(5));

	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		if(gender!=this.gender){
			this.gender = gender;
			chooseImage();
		}
	}

	public int getAge() {
		return age;
	}

	private void setAge(int age) {
		this.age = age;
	}

	public int getSize() {
		return size;
	}

	private void setSize(int size) {
		this.size = size;
	}

	public int getWeight() {
		return weight;
	}

	private void setWeight(int weight) {
		this.weight = weight;
	}

	public int getSiblings() {
		return siblings;
	}

	private void setSiblings(int siblings) {
		this.siblings = siblings;
	}

	public String getEyeColor() {
		return eyeColor;
	}

	public void setEyeColor(String eyeColor) {
		this.eyeColor = eyeColor;
	}

	public String getHairColor() {
		return hairColor;
	}

	public void setHairColor(String hairColor) {
		this.hairColor = hairColor;
	}

	public Place getBirthPlace() {
		return birthPlace;
	}

	private void setBirthPlace(Place birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Job getJob() {
		return job;
	}
}
