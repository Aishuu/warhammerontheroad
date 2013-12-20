package fr.eurecom.warhammerontheroad.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import fr.eurecom.warhammerontheroad.R;

public class Hero {
	public final static int RACE_HUMAN		= 0;
	public final static int RACE_ELF		= 1;
	public final static int RACE_DWARF		= 2;
	public final static int RACE_HOBBIT		= 3;
	private final static String TAG			= "Hero";
	
	private Context context;
	private Stats primarystats, actualstats;
	private int race; //0 = human, 1 = elf, 2 = dwarf, 3 = hobbit
	private ArrayList<Skills> skills;
	private ArrayList<Talents> talents;
	private Job job;
	
	public Hero(Context context, int race){
		this.context = context;
		this.race = race;
		primarystats = new Stats(race);
		skills = new ArrayList<Skills>();
		talents = new ArrayList<Talents>();
		CreateBasicsSkills();
		switch(race){
		case RACE_HUMAN:
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
			
		case RACE_ELF:
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
			
		case RACE_DWARF:
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
			
		case RACE_HOBBIT:
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
			Log.e(TAG, "Unknown race !");
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
	
	public void AddJob(int index)
	{
		int i;
		job = new Job(index, context);
		actualstats = new Stats(primarystats, job.getSecondaryStats());
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
	
	public void show(){
		switch(race){
		case RACE_HUMAN:
			Log.d("race","humain");
			break;
			
		case RACE_ELF:
			Log.d("race","elfe");
			break;
			
		case RACE_DWARF:
			Log.d("race","nain");
			break;
			
		case RACE_HOBBIT:
			Log.d("race","hobbit");
			break;
		default:
			Log.d("race", "Unknown");
		}
		if(actualstats != null)
			actualstats.show();
		for(Skills s : skills){
			s.show();
		}
		for(Talents t: talents){
			t.show();
		}
	}

}
