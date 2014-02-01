package fr.eurecom.warhammerontheroad.model;

import fr.eurecom.warhammerontheroad.R;
import fr.eurecom.warhammerontheroad.network.NetworkParser;
import android.content.Context;
import android.util.Log;

public class Player extends Hero {
	public final static int COLOR_BLUE =	0;
	public final static int COLOR_RED =		1;
	
	private final static String TAG =		"Player";
	
	private String name;
	private int color;
	
	public Player(Context context, String name) {
		super(context);
		this.setId(0);
		this.name = name;
	}

	public Player(Context context, int race, String name) {
		super(context);
		this.setId(0);
		this.name = name;
		
		//TODO: for testing purpose
		this.AddJob(1);
		init(race);
	}
	
	@Override
	protected void init_stats(int race) {
		stats = new ActualStats(new PrimaryStats(race));
	}


	public void AddJob(int index)
	{
		int i;
		if (index == 0)
			armeDraw = new RangedWeapon("arc long","0 3 0");
		else
			armeDraw = new MeleeWeapon("Sword","0 0 0");
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

	}
	
	public void setColor(int color) {
		this.color = color;
		
		// TODO: change this according to color...
		this.resource = R.drawable.mage;
	}
	
	public int getColor() {
		return this.color;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String representInString() {
		return this.name;
	}
	
	@Override
	public String describeAsString() {
		String result = NetworkParser.constructStringFromArgs(Integer.toString(this.color), super.describeAsString());
		return result;
	}

	@Override
	public void constructFromString(WotrService service, String s) {
		String[] parts = s.split(NetworkParser.SEPARATOR, 2);
		if(parts.length < 2) {
			Log.e(TAG, "Not enough arguments !");
			return;
		}
		super.constructFromString(service, parts[1]);
		
		//TODO: for testing purpose
		this.AddJob(1);
		
		try {
			this.setColor(Integer.parseInt(parts[0]));
		} catch(NumberFormatException e) {
			Log.e(TAG, "Not a number !");
		}
	}
}
