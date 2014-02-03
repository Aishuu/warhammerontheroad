package fr.eurecom.warhammerontheroad.model;

import java.util.ArrayList;
import java.math.*;
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
			level = Math.min(level, 1);
			return;
		case 3 :
			level = Math.min(level, 2);
			return;
		case 5 :
			level = Math.min(level, 3);
		}
	}
	
	public int getProtection(int localisation){
		return protection[localisation];
	}
	
	public int getLevel(){
		return level;
	}
}
