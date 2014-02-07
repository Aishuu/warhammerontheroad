package fr.eurecom.warhammerontheroad.model;

import android.util.Log;

public class Armor extends GeneralItem{
	
	private int localisation;
	private int armorPoints;
	
	public Armor(String name, int localisation, int armorPoints)
	{
		super(name);
		this.localisation = localisation;
		this.armorPoints = armorPoints;
	}
	
	public int getLocalisation()
	{
		return localisation;
	}
	
	public int getArmorPoints()
	{
		return armorPoints;
	}
	
	public void show(){
		super.show();
		Log.d("armor",localisation + ", " + armorPoints);
	}
	
	public String[] toArrayString(){
		String[] result = new String[3];
		result[0] = name;
		switch(localisation){
		case 0:
			result[1] = "head";
			break;
		case 1:
			result[1] = "arms";
			break;
		case 2:
			result[1] = "body";
			break;
		case 3:
			result[1] = "legs";
			break;
		}
		result[2] = Integer.toBinaryString(armorPoints);
		return result;
	}

}
