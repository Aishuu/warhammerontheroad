package fr.eurecom.warhammerontheroad.model;

import java.util.*;

import android.util.Log;
public class ArmorSet {
	
	private ArrayList<Armor> set;
	private int level;
	private int[] protection = {0,0,0,0};
	
	public ArmorSet()
	{
		set = new ArrayList<Armor>();
		level = 0;
	}
	
	public void addArmor(Armor armor){
		set.add(armor);
		protection[armor.getLocalisation()] += armor.getArmorPoints();
		switch(protection[armor.getLocalisation()]){
		case 1 :
			level = Math.max(level, 1);
			return;
		case 3 :
			level = Math.max(level, 2);
			return;
		case 5 :
			level = Math.max(level, 3);
		}
	}
	
	public int getProtection(int localisation){
		return protection[localisation];
	}
	
	public int getLevel(){
		return level;
	}
	
	public String[] recapArmor(){
		String[] result = new String[4];
		result[0] = "Head : " + protection[0];
		result[1] = "Arms : " + protection[1];
		result[2] = "Body : " + protection[2];
		result[3] = "Legs : " + protection[3];
		return result;
	}
	
	public void show(){
		Log.d("armorset", Arrays.toString(protection) + ", level :" + level);
		for(Armor a:set)
			a.show();
	}
	
	public ArrayList<String[]> toArrayString()
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		for(Armor a:set)
			result.add(a.toArrayString());
		return result;
	}
}
