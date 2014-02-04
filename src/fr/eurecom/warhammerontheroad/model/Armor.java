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

}
